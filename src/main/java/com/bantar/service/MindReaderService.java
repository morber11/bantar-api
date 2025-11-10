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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MindReaderService implements QuestionService {

    private static final Logger logger = LogManager.getLogger(MindReaderService.class);

    private final MindReaderRepository mindReaderRepository;
    private volatile List<ResponseDTO<MindReaderCategory>> cachedItems;

    @Autowired
    public MindReaderService(MindReaderRepository mindReaderRepository) {
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
        return cachedItems.stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ResponseDTO<?>> getByRange(int startId, int limit) {
        ensureQuestionsLoaded();
        return cachedItems.stream()
                .skip(startId)
                .limit(limit)
                .map(q -> (ResponseDTO<?>) q)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getAll() {
        ensureQuestionsLoaded();
        return cachedItems.stream()
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

        return cachedItems.stream()
                .filter(q -> q.getCategories() != null && q.getCategories().contains(questionCategory))
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

        Set<MindReaderCategory> categorySet = new HashSet<>(validCategories);
        return cachedItems.stream()
                .filter(q -> q.getCategories() != null && !Collections.disjoint(q.getCategories(), categorySet))
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

        Set<MindReaderCategory> categorySet = new HashSet<>(validCategories);
        return cachedItems.stream()
                .filter(q -> q.getCategories() != null && new HashSet<>(q.getCategories()).containsAll(categorySet))
                .map(q -> (ResponseDTO<?>) q)
                .collect(Collectors.toList());
    }

    @Override
    public void refresh() {
        loadQuestions();
    }

    private void ensureQuestionsLoaded() {
        if (cachedItems == null || cachedItems.isEmpty()) {
            synchronized (this) {
                if (cachedItems == null || cachedItems.isEmpty()) {
                    loadQuestions();
                }
            }
        }
    }

    private synchronized void loadQuestions() {
        List<MindReaderEntity> items = mindReaderRepository.getAll();

        // Build category mapping from the categories present on each MindReaderEntity
        java.util.Map<Long, List<MindReaderCategory>> categoryMap = items.stream()
                .collect(Collectors.toMap(
                        MindReaderEntity::getId,
                        e -> {
                            if (e.getCategories() == null) return Collections.emptyList();
                            return e.getCategories().stream()
                                    .map(cat -> MindReaderCategory.valueOf(cat.getCategoryCode()))
                                    .collect(Collectors.toList());
                        }
                ));

        cachedItems = items.stream()
                .map(entity -> new ResponseDTO<>(entity.getText(), entity.getId(),
                        categoryMap.getOrDefault(entity.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }
}