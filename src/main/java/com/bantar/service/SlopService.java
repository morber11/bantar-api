package com.bantar.service;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.AiQuestionEntity;
import com.bantar.entity.IcebreakerEntity;
import com.bantar.mapper.IcebreakerMapper;
import com.bantar.model.Icebreaker;
import com.bantar.model.IcebreakerCategory;
import com.bantar.repository.AiQuestionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bantar.slop.SlopProvider;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.Executor;

import static com.bantar.config.Constants.ICEBREAKERS_LLM_PROMPT;

@Service
public class SlopService {

    private static final Logger logger = LogManager.getLogger(SlopService.class);
    Map<String, Icebreaker> questionMap = new ConcurrentHashMap<>();
    private final SlopProvider aiProvider;
    private final AiQuestionRepository aiQuestionRepository;
    private final Executor slopSeedExecutor;
    private final boolean slopSeedEnabled;
    private final long slopSeedBatchDelayMillis;
    private final ObjectMapper objectMapper;

    private static final int INITIAL_QUESTION_COUNT = 30;
    private static final int TARGET_QUESTION_COUNT = 100;
    private static final int BATCH_GENERATE_COUNT = 20;
    private static final int MAX_BATCH_ATTEMPTS = 10;

    @Autowired
    public SlopService(SlopProvider aiProvider,
            AiQuestionRepository aiQuestionRepository,
            @Qualifier("slopSeedExecutor") Executor slopSeedExecutor,
            @Value("${slop.seed.enabled:true}") boolean slopSeedEnabled,
            @Value("${slop.seed.batchDelayMillis:5000}") long slopSeedBatchDelayMillis,
            ObjectMapper objectMapper) {
        this.aiProvider = aiProvider;
        this.aiQuestionRepository = aiQuestionRepository;
        this.slopSeedExecutor = slopSeedExecutor;
        this.slopSeedEnabled = slopSeedEnabled;
        this.slopSeedBatchDelayMillis = slopSeedBatchDelayMillis;
        this.objectMapper = objectMapper;
    }

    /**
     * Backwards-compatible constructor for tests that instantiate SlopService
     * directly
     * Uses a direct executor and disables background seeding so behavior remains
     * deterministic
     * Needed for tests
     */
    public SlopService(SlopProvider aiProvider, AiQuestionRepository aiQuestionRepository) {
        this(aiProvider, aiQuestionRepository, Runnable::run, false, 5000L, new ObjectMapper());
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void initialize() {
        logger.info("SlopService initialized using provider {}", aiProvider.getClass().getName());
        try {
            aiQuestionRepository.findAll().forEach(entity -> {
                ResponseDTO<IcebreakerCategory> dto = IcebreakerMapper
                        .toGenericModel(new IcebreakerEntity(entity.getId(), entity.getText(), null));
                Icebreaker q = new Icebreaker(dto.getText(), dto.getId());
                q.setCategories(List.of(IcebreakerCategory.CASUAL));
                try {
                    String key = sha256(entity.getText().trim().toLowerCase());
                    questionMap.putIfAbsent(key, q);
                } catch (Exception e) {
                    // fallback to text key if hashing fails
                    questionMap.putIfAbsent(q.getText(), q);
                }
            });
            logger.info("{} ai questions preloaded from database", questionMap.size());

            if (slopSeedEnabled) {
                generateQuestions(INITIAL_QUESTION_COUNT);
            }

            // ensure we always have 100 questions in the initial repository
            // seeding can be long-running; run it asynchronously after startup
            if (aiQuestionRepository.count() < TARGET_QUESTION_COUNT) {
                logger.info("AI question count below target ({}). Background seeding will run after startup.",
                        aiQuestionRepository.count());
            }
        } catch (Exception e) {
            logger.error("Failed to initialize SlopService", e);
            throw new IllegalStateException("SlopService initialization failed", e);
        }
    }

    public void generateQuestions(int count) {
        String prompt = String.format(ICEBREAKERS_LLM_PROMPT, count);

        try {
            String aiText = aiProvider.generate(prompt);
            if (aiText == null) {
                throw new Exception("Empty response from AI provider");
            }
            parseAndStoreQuestions(aiText);
        } catch (Exception e) {
            logger.error("An error occurred while trying to generateQuestions", e);
        }
    }

    public ResponseDTO<IcebreakerCategory> getRandomQuestion() {
        if (questionMap.isEmpty()) {
            return null;
        }

        List<Icebreaker> icebreakers = new ArrayList<>(questionMap.values());
        int randomIndex = ThreadLocalRandom.current().nextInt(icebreakers.size());
        Icebreaker q = icebreakers.get(randomIndex);
        return new ResponseDTO<>(q.getText(), q.getId(), q.getCategories());
    }

    public List<ResponseDTO<IcebreakerCategory>> getAllQuestions() {
        List<ResponseDTO<IcebreakerCategory>> dtos = questionMap.values().stream()
                .map(q -> new ResponseDTO<>(q.getText(), q.getId(), q.getCategories()))
                .toList();
        return List.copyOf(dtos);
    }

    private void parseAndStoreQuestions(String jsonResponse) throws JsonProcessingException {
        String cleaned = cleanJsonResponse(jsonResponse);
        List<Icebreaker> icebreakers = objectMapper.readValue(cleaned,
            objectMapper.getTypeFactory().constructCollectionType(List.class, Icebreaker.class));

        int added = 0;
        for (Icebreaker q : icebreakers) {
            String normalized = q.getText() == null ? "" : q.getText().trim();

            if (normalized.isBlank())
                continue;

            String key;
            try {
                key = sha256(normalized.toLowerCase());
            } catch (NoSuchAlgorithmException e) {
                // fallback to text key
                key = normalized;
            }

            if (questionMap.putIfAbsent(key, q) == null) {
                q.setCategories(List.of(IcebreakerCategory.CASUAL));
                added++;

                try {
                    if (!aiQuestionRepository.existsByHash(key)) {
                        AiQuestionEntity entity = new AiQuestionEntity(q.getText(), key);
                        try {
                            aiQuestionRepository.save(entity);
                        } catch (DataIntegrityViolationException | PersistenceException dbEx) {
                            // concurrent insert happened
                            logger.warn("AI question save race detected for hash {} - ignoring duplicate save.", key);
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("Failed to persist AI question: {}", q.getText(), ex);
                }
            }
        }
        logger.info("{} ai generated questions added", added);
    }

    private String cleanJsonResponse(String response) {
        String cleaned = response.replace("`", "").trim();
        int startIndex = cleaned.indexOf('[');

        if (startIndex == -1) {
            throw new IllegalArgumentException("No JSON array found in response");
        }

        return cleaned.substring(startIndex);
    }

    // SHA-256 will always exist but getInstance throws a NoSuchAlogrithmException
    // that we need to handle anyway
    private String sha256(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @SuppressWarnings({ "unused", "BusyWait" })
    @EventListener(ApplicationReadyEvent.class)
    public void startBackgroundSeeding() {
        if (!slopSeedEnabled) {
            logger.info("Slop background seeding is disabled by configuration.");
            return;
        }

        long current = aiQuestionRepository.count();
        if (current >= TARGET_QUESTION_COUNT) {
            logger.info("AI question count is {} which meets target {}; skipping background seeding.", current,
                    TARGET_QUESTION_COUNT);
            return;
        }

        logger.info("Scheduling background AI seeding (target={}, current={})", TARGET_QUESTION_COUNT, current);
        slopSeedExecutor.execute(() -> {
            try {
                int attempts = 0;
                while (aiQuestionRepository.count() < TARGET_QUESTION_COUNT && attempts < MAX_BATCH_ATTEMPTS) {
                    logger.info("Background seeding: AI question count is {} - generating {} more (attempt {}/{})",
                            aiQuestionRepository.count(), BATCH_GENERATE_COUNT, attempts + 1, MAX_BATCH_ATTEMPTS);

                    generateQuestions(BATCH_GENERATE_COUNT);
                    attempts++;

                    if (aiQuestionRepository.count() < TARGET_QUESTION_COUNT) {
                        try {
                            Thread.sleep(slopSeedBatchDelayMillis);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            logger.warn("Background seeding interrupted; stopping further attempts.");
                            break;
                        }
                    }
                }

                if (aiQuestionRepository.count() < TARGET_QUESTION_COUNT) {
                    logger.warn("Background seeding reached max attempts but only have {} AI questions; target is {}",
                            aiQuestionRepository.count(), TARGET_QUESTION_COUNT);
                } else {
                    logger.info("Background seeding completed; now have {} AI questions", aiQuestionRepository.count());
                }
            } catch (Exception ex) {
                logger.error("Error while running background AI seeding", ex);
            }
        });
    }
}
