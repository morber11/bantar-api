package com.bantar.service;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.IcebreakerCategoryEntity;
import com.bantar.entity.IcebreakerEntity;
import com.bantar.model.IcebreakerCategory;
import com.bantar.repository.IcebreakerCategoryRepository;
import com.bantar.repository.IcebreakerRepository;
import com.bantar.service.interfaces.QuestionService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IcebreakerService implements QuestionService {

    private static final Logger logger = LogManager.getLogger(IcebreakerService.class);
    private final IcebreakerRepository icebreakerRepository;
    private final IcebreakerCategoryRepository icebreakerCategoryRepository;

    private volatile List<ResponseDTO<IcebreakerCategory>> cachedQuestions;

    @Autowired
    public IcebreakerService(IcebreakerRepository icebreakerRepository, IcebreakerCategoryRepository icebreakerCategoryRepository) {
        logger.info("Initializing QuestionServiceImpl");
        this.icebreakerRepository = icebreakerRepository;
        this.icebreakerCategoryRepository = icebreakerCategoryRepository;
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

        IcebreakerCategory questionCategory;
        try {
            questionCategory = IcebreakerCategory.valueOf(category.toUpperCase());
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
        List<IcebreakerCategory> validCategories = IcebreakerCategory.fromStrings(categories);
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
        List<IcebreakerCategory> validCategories = IcebreakerCategory.fromStrings(categories);
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
        List<IcebreakerEntity> icebreakerQuestions = icebreakerRepository.getAllIcebreakers();

        List<Long> questionIds = icebreakerQuestions.stream()
                .map(IcebreakerEntity::getId)
                .collect(Collectors.toList());

        List<IcebreakerCategoryEntity> questionCategoryEntities = questionIds.isEmpty()
                ? Collections.emptyList()
                : icebreakerCategoryRepository.findByQuestionIdIn(questionIds);

        Map<Long, List<IcebreakerCategory>> questionCategoriesMap = questionCategoryEntities.stream()
                .collect(Collectors.groupingBy(categoryEntity -> categoryEntity.getQuestion().getId(),
                        Collectors.mapping(categoryEntity -> IcebreakerCategory.valueOf(categoryEntity.getCategory()),
                                Collectors.toList())));

        cachedQuestions = icebreakerQuestions.stream()
                .map(q -> {
                    List<IcebreakerCategory> categories = questionCategoriesMap.getOrDefault(q.getId(),
                            Collections.emptyList());
                    return new ResponseDTO<>(q.getText(), q.getId(), categories);
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

    private ResponseDTO<IcebreakerCategory> findQuestionById(int id) {
        return cachedQuestions.stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private List<ResponseDTO<IcebreakerCategory>> findQuestionsByRange(int startId, int limit) {
        return cachedQuestions.stream()
                .skip(startId)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<IcebreakerCategory>> findQuestionsByCategory(IcebreakerCategory category) {
        return cachedQuestions.stream()
                .filter(d -> d.getCategories() != null && d.getCategories().contains(category))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<IcebreakerCategory>> findQuestionsByCategories(
            List<IcebreakerCategory> requiredCategories) {
        Set<IcebreakerCategory> requiredCategorySet = new HashSet<>(requiredCategories);
        return cachedQuestions.stream()
                .filter(d -> d.getCategories() != null
                        && !Collections.disjoint(new HashSet<>(d.getCategories()), requiredCategorySet))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<IcebreakerCategory>> findQuestionsByFilteredCategories(
            List<IcebreakerCategory> requiredCategories) {
        return cachedQuestions.stream()
                .filter(d -> d.getCategories() != null
                        && new HashSet<>(d.getCategories()).containsAll(requiredCategories))
                .collect(Collectors.toList());
    }
}
