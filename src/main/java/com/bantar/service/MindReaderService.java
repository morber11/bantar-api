package com.bantar.service;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.MindReaderEntity;
import com.bantar.model.MindReaderCategory;
import com.bantar.repository.MindReaderRepository;
import com.bantar.repository.MindReaderCategoryRepository;
import com.bantar.entity.MindReaderCategoryEntity;
import com.bantar.service.interfaces.QuestionService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MindReaderService implements QuestionService {

    private static final Logger logger = LogManager.getLogger(MindReaderService.class);

    private final MindReaderRepository mindReaderRepository;
    private final MindReaderCategoryRepository mindReaderCategoryRepository;
    private volatile List<ResponseDTO<MindReaderCategory>> cachedQuestions;

    @Autowired
    public MindReaderService(MindReaderRepository mindReaderRepository, MindReaderCategoryRepository mindReaderCategoryRepository) {
        logger.info("Initializing MindReaderService");
        this.mindReaderRepository = mindReaderRepository;
        this.mindReaderCategoryRepository = mindReaderCategoryRepository;
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
        return findQuestionsByRange(0, cachedQuestions.size()).stream()
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
        List<MindReaderEntity> items = mindReaderRepository.getAll();

        List<Long> ids = items.stream()
                .map(MindReaderEntity::getId)
                .collect(Collectors.toList());

        List<MindReaderCategoryEntity> categoryEntities = ids.isEmpty()
                ? Collections.emptyList()
                : mindReaderCategoryRepository.findByMindReaderIdIn(ids);

        Map<Long, List<MindReaderCategory>> categoryMap = categoryEntities.stream()
                .collect(Collectors.groupingBy(ent -> ent.getMindReader().getId(),
                        Collectors.mapping(ent -> MindReaderCategory.valueOf(ent.getCategoryCode()),
                                Collectors.toList())));

        cachedQuestions = items.stream()
                .map(entity -> new ResponseDTO<>(entity.getText(), entity.getId(),
                        categoryMap.getOrDefault(entity.getId(), Collections.emptyList())))
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

    private ResponseDTO<MindReaderCategory> findQuestionById(int id) {
        return cachedQuestions.stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private List<ResponseDTO<MindReaderCategory>> findQuestionsByRange(int startId, int limit) {
        return cachedQuestions.stream()
                .skip(startId)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<MindReaderCategory>> findQuestionsByCategory(MindReaderCategory category) {
        return cachedQuestions.stream()
                .filter(d -> d.getCategories() != null && d.getCategories().contains(category))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<MindReaderCategory>> findQuestionsByCategories(
            List<MindReaderCategory> requiredCategories) {
        Set<MindReaderCategory> requiredCategorySet = new HashSet<>(requiredCategories);
        return cachedQuestions.stream()
                .filter(d -> d.getCategories() != null
                        && !Collections.disjoint(new HashSet<>(d.getCategories()), requiredCategorySet))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<MindReaderCategory>> findQuestionsByFilteredCategories(
            List<MindReaderCategory> requiredCategories) {
        return cachedQuestions.stream()
                .filter(d -> d.getCategories() != null
                        && new HashSet<>(d.getCategories()).containsAll(requiredCategories))
                .collect(Collectors.toList());
    }
}