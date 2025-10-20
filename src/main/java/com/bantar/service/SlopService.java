package com.bantar.service;

import com.bantar.entity.AiQuestionEntity;
import com.bantar.entity.QuestionEntity;
import com.bantar.mapper.QuestionMapper;
import com.bantar.model.ResponseDTO;
import com.bantar.model.Question;
import com.bantar.model.QuestionCategory;
import com.bantar.repository.AiQuestionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import static com.bantar.config.Constants.GEMINI_MODEL;
import static com.bantar.config.Constants.ICEBREAKERS_LLM_PROMPT;

@Service
public class SlopService {

    private static final Logger logger = LogManager.getLogger(SlopService.class);
    Map<String, Question> questionMap = new ConcurrentHashMap<>();
    private final Client aiClient;
    private final AiQuestionRepository aiQuestionRepository;
    private static final int INITIAL_QUESTION_COUNT = 30;

    @Autowired
    public SlopService(Client aiClient, AiQuestionRepository aiQuestionRepository) {
        this.aiClient = aiClient;
        this.aiQuestionRepository = aiQuestionRepository;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void initialize() {
        try {
            aiQuestionRepository.findAll().forEach(entity -> {
                ResponseDTO<QuestionCategory> dto = QuestionMapper.toGenericModel(new QuestionEntity(entity.getId(), entity.getText(), null));
                Question q = new Question(dto.getText(), dto.getId());
                q.setCategories(List.of(QuestionCategory.ICEBREAKER));
                try {
                    String key = sha256(entity.getText().trim().toLowerCase());
                    questionMap.putIfAbsent(key, q);
                } catch (Exception e) {
                    // fallback to text key if hashing fails
                    questionMap.putIfAbsent(q.getText(), q);
                }
            });
            logger.info("{} ai questions preloaded from database", questionMap.size());


            generateQuestions(INITIAL_QUESTION_COUNT);
        } catch (Exception e) {
            logger.error("An error occurred during the initial question generation", e);
        }
    }

    public void generateQuestions(int count) {
        String prompt = String.format(ICEBREAKERS_LLM_PROMPT, count);

        try {
            GenerateContentResponse response =
                    aiClient.models.generateContent(GEMINI_MODEL, prompt, null);

            if (response == null || response.text() == null) {
                throw new Exception("Empty response from AI Client");
            }

            parseAndStoreQuestions(response.text());
        } catch (Exception e) {
            logger.error("An error occurred while trying to generateQuestions", e);
        }
    }

    public Question getRandomQuestion() {
        if (questionMap.isEmpty()) {
            throw new IllegalStateException("No questions available");
        }

        List<Question> questions = new ArrayList<>(questionMap.values());
        int randomIndex = ThreadLocalRandom.current().nextInt(questions.size());
        return questions.get(randomIndex);
    }

    private void parseAndStoreQuestions(String jsonResponse) throws JsonProcessingException {
        String cleaned = cleanJsonResponse(jsonResponse);
        ObjectMapper mapper = new ObjectMapper();

        List<Question> questions = mapper.readValue(cleaned,
                mapper.getTypeFactory().constructCollectionType(List.class, Question.class));

        int added = 0;
        for (Question q : questions) {
            String normalized = q.getText() == null ? "" : q.getText().trim();
            if (normalized.isBlank()) continue;

            String key;
            try {
                key = sha256(normalized.toLowerCase());
            } catch (NoSuchAlgorithmException e) {
                // fallback to text key
                key = normalized;
            }

            if (questionMap.putIfAbsent(key, q) == null) {
                q.setCategories(List.of(QuestionCategory.ICEBREAKER));
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

    private String sha256(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
