package com.bantar.service;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.IcebreakerEntity;
import com.bantar.model.IcebreakerCategory;
import com.bantar.repository.IcebreakerRepository;
import com.bantar.service.interfaces.QuestionService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class IcebreakerService implements QuestionService {

    private static final Logger logger = LogManager.getLogger(IcebreakerService.class);
    private final IcebreakerRepository icebreakerRepository;

    private final AtomicReference<List<ResponseDTO<IcebreakerCategory>>> cachedQuestions = new AtomicReference<>();

    @Autowired
    public IcebreakerService(IcebreakerRepository icebreakerRepository) {
        logger.info("Initializing QuestionServiceImpl");
        this.icebreakerRepository = icebreakerRepository;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void initialize() {
        try {
            loadQuestions();
        } catch (Exception e) {
            logger.error("Failed to initialize IcebreakerService", e);
            throw new IllegalStateException("IcebreakerService initialization failed", e);
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
        return findQuestionsByRange(0, cachedQuestions.get().size()).stream()
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
        List<IcebreakerEntity> icebreakerQuestions = icebreakerRepository.findAllWithCategories();

        cachedQuestions.set(icebreakerQuestions.stream()
                .map(q -> {
                    List<IcebreakerCategory> categories = q.getCategories() == null
                            ? Collections.emptyList()
                            : q.getCategories().stream()
                                    .map(c -> IcebreakerCategory.valueOf(c.getCategory()))
                                    .collect(Collectors.toList());
                    return new ResponseDTO<>(q.getText(), q.getId(), categories);
                })
                .collect(Collectors.toList()));
    }

    private void ensureQuestionsLoaded() {
        List<ResponseDTO<IcebreakerCategory>> snapshot = cachedQuestions.get();
        if (snapshot == null || snapshot.isEmpty()) {
            synchronized (this) {
                snapshot = cachedQuestions.get();
                if (snapshot == null || snapshot.isEmpty()) {
                    loadQuestions();
                }
            }
        }
    }

    private ResponseDTO<IcebreakerCategory> findQuestionById(int id) {
        return cachedQuestions.get().stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private List<ResponseDTO<IcebreakerCategory>> findQuestionsByRange(int startId, int limit) {
        return cachedQuestions.get().stream()
                .skip(startId)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<IcebreakerCategory>> findQuestionsByCategory(IcebreakerCategory category) {
        return cachedQuestions.get().stream()
                .filter(d -> d.getCategories() != null && d.getCategories().contains(category))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<IcebreakerCategory>> findQuestionsByCategories(
            List<IcebreakerCategory> requiredCategories) {
        Set<IcebreakerCategory> requiredCategorySet = new HashSet<>(requiredCategories);
        return cachedQuestions.get().stream()
                .filter(d -> d.getCategories() != null
                        && !Collections.disjoint(new HashSet<>(d.getCategories()), requiredCategorySet))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<IcebreakerCategory>> findQuestionsByFilteredCategories(
            List<IcebreakerCategory> requiredCategories) {
        return cachedQuestions.get().stream()
                .filter(d -> d.getCategories() != null
                        && new HashSet<>(d.getCategories()).containsAll(requiredCategories))
                .collect(Collectors.toList());
    }
}
