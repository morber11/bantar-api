package com.bantar.service;

import com.bantar.entity.QuestionCategoryEntity;
import com.bantar.entity.QuestionEntity;
import com.bantar.model.Question;
import com.bantar.model.QuestionCategory;
import com.bantar.repository.QuestionCategoryRepository;
import com.bantar.repository.QuestionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bantar.config.Constants.DEFAULT_QUESTIONS_ICEBREAKERS_PATH;
import static com.bantar.mapper.QuestionMapper.toModel;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final Logger logger = LogManager.getLogger(QuestionServiceImpl.class);
    private QuestionRepository questionRepository;
    private QuestionCategoryRepository questionCategoryRepository;
    private List<Question> cachedQuestions;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository, QuestionCategoryRepository questionCategoryRepository) {
        logger.info("Initializing QuestionServiceImpl");
        this.questionRepository = questionRepository;
        this.questionCategoryRepository = questionCategoryRepository;
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
        List<QuestionEntity> icebreakerQuestions = questionRepository.getAllIcebreakers();

        List<Long> questionIds = icebreakerQuestions.stream()
                .map(QuestionEntity::getId)
                .collect(Collectors.toList());

        List<QuestionCategoryEntity> questionCategoryEntities = questionCategoryRepository.findByQuestionIdIn(questionIds);

        Map<Long, List<QuestionCategory>> questionCategoriesMap = questionCategoryEntities.stream()
                .collect(Collectors.groupingBy(categoryEntity -> categoryEntity.getQuestion().getId(),
                        Collectors.mapping(categoryEntity -> QuestionCategory.valueOf(categoryEntity.getCategory()), Collectors.toList())));

        cachedQuestions = icebreakerQuestions.stream()
                .map(q -> {
                    Question question = toModel(q);
                    List<QuestionCategory> categories = questionCategoriesMap.getOrDefault(q.getId(), Collections.emptyList());
                    question.setCategories(categories);
                    return question;
                })
                .collect(Collectors.toList());
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
