package com.bantar.service;

import com.bantar.entity.QuestionCategoryEntity;
import com.bantar.entity.QuestionEntity;
import com.bantar.mapper.QuestionMapper;
import com.bantar.model.QuestionCategory;
import com.bantar.model.ResponseDTO;
import com.bantar.repository.QuestionCategoryRepository;
import com.bantar.repository.QuestionRepository;
import com.bantar.service.interfaces.QuestionService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final Logger logger = LogManager.getLogger(QuestionServiceImpl.class);
    private final QuestionRepository questionRepository;
    private final QuestionCategoryRepository questionCategoryRepository;

    private volatile List<ResponseDTO<QuestionCategory>> cachedQuestions;

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
    public ResponseDTO<?> getById(int id) {
        ensureQuestionsLoaded();
        return findQuestionById(id);
    }

    @Override
    public List<ResponseDTO<?>> getByRange(int startId, int limit) {
        ensureQuestionsLoaded();
        return findQuestionsByRange(startId, limit).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getAll() {
        ensureQuestionsLoaded();
        return findQuestionsByRange(0, cachedQuestions.size()).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByCategory(String category) {
        if (category == null || category.isBlank()) {
            return Collections.emptyList();
        }

        QuestionCategory questionCategory;
        try {
            questionCategory = QuestionCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }

        ensureQuestionsLoaded();

        return findQuestionsByCategory(questionCategory).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByCategories(List<String> categories) {
        List<QuestionCategory> validCategories = getValidCategories(categories);
        if (validCategories.isEmpty()) {
            return Collections.emptyList();
        }

        ensureQuestionsLoaded();

        return findQuestionsByCategories(validCategories).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByFilteredCategories(List<String> categories) {
        List<QuestionCategory> validCategories = getValidCategories(categories);
        if (validCategories.isEmpty()) {
            return Collections.emptyList();
        }

        ensureQuestionsLoaded();

        return findQuestionsByFilteredCategories(validCategories).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public void refresh() {
        loadQuestions();
    }

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
                    ResponseDTO<QuestionCategory> question = QuestionMapper.toGenericModel(q);
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

    private List<QuestionCategory> getValidCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }

        return categories.stream()
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
    }

    private ResponseDTO<QuestionCategory> findQuestionById(int id) {
        return cachedQuestions.stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private List<ResponseDTO<QuestionCategory>> findQuestionsByRange(int startId, int limit) {
        return cachedQuestions.stream()
                .skip(startId)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<QuestionCategory>> findQuestionsByCategory(QuestionCategory category) {
        return cachedQuestions.stream()
                .filter(d -> d.getCategories() != null && d.getCategories().contains(category))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<QuestionCategory>> findQuestionsByCategories(List<QuestionCategory> requiredCategories) {
        Set<QuestionCategory> requiredCategorySet = new HashSet<>(requiredCategories);
        return cachedQuestions.stream()
                .filter(d -> d.getCategories() != null && !Collections.disjoint(new HashSet<>(d.getCategories()), requiredCategorySet))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<QuestionCategory>> findQuestionsByFilteredCategories(List<QuestionCategory> requiredCategories) {
        return cachedQuestions.stream()
                .filter(d -> d.getCategories() != null && new HashSet<>(d.getCategories()).containsAll(requiredCategories))
                .collect(Collectors.toList());
    }
}
