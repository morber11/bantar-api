package com.bantar.service;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.MindReaderEntity;
import com.bantar.model.MindReaderCategory;
import com.bantar.repository.MindReaderRepository;
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
public class MindReaderService implements QuestionService {

    private static final Logger logger = LogManager.getLogger(MindReaderService.class);

    private final MindReaderRepository mindReaderRepository;
    private final AtomicReference<List<ResponseDTO<MindReaderCategory>>> cachedQuestions = new AtomicReference<>();

    @Autowired
    public MindReaderService(MindReaderRepository mindReaderRepository) {
        logger.info("Initializing MindReaderService");
        this.mindReaderRepository = mindReaderRepository;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void initialize() {
        try {
            loadQuestions();
        } catch (Exception e) {
            logger.error("An error occurred during the MindReader initial load", e);
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
                .map(q -> (ResponseDTO<?>) q)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getAll() {
        ensureQuestionsLoaded();
        return findQuestionsByRange(0, cachedQuestions.get().size()).stream()
                .map(q -> (ResponseDTO<?>) q)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByCategory(String category) {
        if (category == null || category.isBlank()) {
            return Collections.emptyList();
        }

        MindReaderCategory questionCategory;
        try {
            questionCategory = MindReaderCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Collections.emptyList();
        }

        ensureQuestionsLoaded();
        return findQuestionsByCategory(questionCategory).stream()
                .map(q -> (ResponseDTO<?>) q)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByCategories(List<String> categories) {
        List<MindReaderCategory> validCategories = MindReaderCategory.fromStrings(categories);
        if (validCategories.isEmpty()) {
            return Collections.emptyList();
        }

        ensureQuestionsLoaded();
        return findQuestionsByCategories(validCategories).stream()
                .map(q -> (ResponseDTO<?>) q)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByFilteredCategories(List<String> categories) {
        List<MindReaderCategory> validCategories = MindReaderCategory.fromStrings(categories);
        if (validCategories.isEmpty()) {
            return Collections.emptyList();
        }

        ensureQuestionsLoaded();
        return findQuestionsByFilteredCategories(validCategories).stream()
                .map(q -> (ResponseDTO<?>) q)
                .collect(Collectors.toList());
    }

    @Override
    public void refresh() {
        loadQuestions();
    }

    private synchronized void loadQuestions() {
        List<MindReaderEntity> items = mindReaderRepository.findAllWithCategories();

        cachedQuestions.set(items.stream()
                .map(entity -> {
                    List<MindReaderCategory> categories = entity.getCategories() == null
                            ? Collections.emptyList()
                            : entity.getCategories().stream()
                                    .map(c -> MindReaderCategory.valueOf(c.getCategoryCode()))
                                    .collect(Collectors.toList());
                    return new ResponseDTO<>(entity.getText(), entity.getId(), categories);
                })
                .collect(Collectors.toList()));
    }

    private void ensureQuestionsLoaded() {
        List<ResponseDTO<MindReaderCategory>> snapshot = cachedQuestions.get();
        if (snapshot == null || snapshot.isEmpty()) {
            synchronized (this) {
                snapshot = cachedQuestions.get();
                if (snapshot == null || snapshot.isEmpty()) {
                    loadQuestions();
                }
            }
        }
    }

    private ResponseDTO<MindReaderCategory> findQuestionById(int id) {
        return cachedQuestions.get().stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private List<ResponseDTO<MindReaderCategory>> findQuestionsByRange(int startId, int limit) {
        return cachedQuestions.get().stream()
                .skip(startId)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<MindReaderCategory>> findQuestionsByCategory(MindReaderCategory category) {
        return cachedQuestions.get().stream()
                .filter(d -> d.getCategories() != null && d.getCategories().contains(category))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<MindReaderCategory>> findQuestionsByCategories(
            List<MindReaderCategory> requiredCategories) {
        Set<MindReaderCategory> requiredCategorySet = new HashSet<>(requiredCategories);
        return cachedQuestions.get().stream()
                .filter(d -> d.getCategories() != null
                        && !Collections.disjoint(new HashSet<>(d.getCategories()), requiredCategorySet))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<MindReaderCategory>> findQuestionsByFilteredCategories(
            List<MindReaderCategory> requiredCategories) {
        return cachedQuestions.get().stream()
                .filter(d -> d.getCategories() != null
                        && new HashSet<>(d.getCategories()).containsAll(requiredCategories))
                .collect(Collectors.toList());
    }
}