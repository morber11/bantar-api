package com.bantar.service;

import com.bantar.model.Question;
import com.bantar.model.QuestionCategory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static com.bantar.config.Constants.GEMINI_MODEL;
import static com.bantar.config.Constants.ICEBREAKERS_LLM_PROMPT;

@Service
public class SlopService {

    private static final Logger logger = LogManager.getLogger(SlopService.class);
    Map<String, Question> questionMap = new ConcurrentHashMap<>();
    private final Client aiClient;
    private static final int INITIAL_QUESTION_COUNT = 30;

    public SlopService(Client aiClient) {
        this.aiClient = aiClient;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void initialize() {
        try {
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
            if (questionMap.putIfAbsent(q.getText(), q) == null) {
                q.setCategories(List.of(QuestionCategory.ICEBREAKER));
                added++;
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
}
