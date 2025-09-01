package com.bantar.service;

import com.bantar.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final String DEFAULT_QUESTIONS_ICEBREAKERS_PATH = "static/questions/questions_icebreakers.json";

    private final JsonReaderService jsonReaderUtil;
    private List<Question> cachedQuestions;

    @Autowired
    public QuestionServiceImpl(JsonReaderService jsonReaderUtil) {
        this.jsonReaderUtil = jsonReaderUtil;
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

    private void loadQuestions(String path) {
        cachedQuestions = jsonReaderUtil.readJsonResource(path);
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
