package com.bantar.service;

import com.bantar.model.Question;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.bantar.config.Constants.DEFAULT_QUESTIONS_ICEBREAKERS_PATH;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final Logger logger = LogManager.getLogger(QuestionServiceImpl.class);
    private final QuestionMappingService questionMappingService;
    private List<Question> cachedQuestions;

    @Autowired
    public QuestionServiceImpl(QuestionMappingService questionMappingService) {
        logger.info("Initializing QuestionServiceImpl");
        this.questionMappingService = questionMappingService;
        loadQuestions(DEFAULT_QUESTIONS_ICEBREAKERS_PATH);
    }

    @Override
    public Question getQuestionById(int id) {
        ensureQuestionsLoaded();
        return findQuestionById(id);
    }

    @Override
    public List<Question> getQuestionsByRange(int startId, int limit) {
        ensureQuestionsLoaded();
        return findQuestionsByRange(startId, limit);
    }

    @Override
    public List<Question> getAllQuestions() {
        ensureQuestionsLoaded();
        return cachedQuestions;
    }

    @Override
    public void refreshQuestions() {
        loadQuestions(DEFAULT_QUESTIONS_ICEBREAKERS_PATH);
    }

    @SuppressWarnings("SameParameterValue")
    private void loadQuestions(String path) {
        cachedQuestions = questionMappingService.getQuestionsFromJsonResource(path);
    }

    private void ensureQuestionsLoaded() {
        if (cachedQuestions == null || cachedQuestions.isEmpty()) {
            loadQuestions(DEFAULT_QUESTIONS_ICEBREAKERS_PATH);
        }
    }

    private Question findQuestionById(int id) {
        return cachedQuestions.stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private List<Question> findQuestionsByRange(int startId, int limit) {
        return cachedQuestions.stream()
                .skip(startId)
                .limit(limit)
                .collect(Collectors.toList());
    }
}
