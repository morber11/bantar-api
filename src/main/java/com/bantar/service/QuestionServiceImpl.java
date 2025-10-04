package com.bantar.service;

import com.bantar.entity.QuestionCategoryEntity;
import com.bantar.entity.QuestionEntity;
import com.bantar.model.Question;
import com.bantar.model.QuestionCategory;
import com.bantar.repository.QuestionCategoryRepository;
import com.bantar.repository.QuestionRepository;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bantar.mapper.QuestionMapper.toModel;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final Logger logger = LogManager.getLogger(QuestionServiceImpl.class);
    private final QuestionRepository questionRepository;
    private final QuestionCategoryRepository questionCategoryRepository;
    private volatile List<Question> cachedQuestions;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository, QuestionCategoryRepository questionCategoryRepository) {
        logger.info("Initializing QuestionServiceImpl");
        this.questionRepository = questionRepository;
        this.questionCategoryRepository = questionCategoryRepository;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void initialize() {
        try {
            loadQuestions();
        } catch (Exception e) {
            logger.error("An error occurred during the initial question load", e);
        }
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
    public List<Question> getQuestionsByCategory(String category) {
        ensureQuestionsLoaded();

        QuestionCategory questionCategory;
        try {
            questionCategory = QuestionCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
        return findQuestionsByCategory(questionCategory);
    }

    @Override
    public List<Question> getQuestionsByCategories(List<String> categories) {
        ensureQuestionsLoaded();

        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }

        List<QuestionCategory> validCategories = categories.stream()
                .map(String::toUpperCase)
                .map(category -> {
                    try {
                        return QuestionCategory.valueOf(category);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        if (validCategories.isEmpty()) {
            return null;
        }

        return findQuestionsByCategories(validCategories);
    }

    @Override
    public void refreshQuestions() {
        loadQuestions();
    }

    @SuppressWarnings("SameParameterValue")
    private synchronized void loadQuestions() {
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
            synchronized (this) {
                if (cachedQuestions == null || cachedQuestions.isEmpty()) {
                    loadQuestions();
                }
            }
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

    private List<Question> findQuestionsByCategory(QuestionCategory category) {
        return cachedQuestions.stream()
                .filter(q -> q.getCategories().contains(category))
                .collect(Collectors.toList());
    }

    private List<Question> findQuestionsByCategories(List<QuestionCategory> requiredCategories) {
        return cachedQuestions.stream()
                .filter(q -> new HashSet<>(q.getCategories()).containsAll(requiredCategories))
                .collect(Collectors.toList());
    }


}
