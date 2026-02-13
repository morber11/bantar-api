package com.bantar.service;

import com.bantar.entity.DebateCategoryEntity;
import com.bantar.entity.DebateEntity;
import com.bantar.model.DebateCategory;
import com.bantar.dto.ResponseDTO;
import com.bantar.repository.DebateCategoryRepository;
import com.bantar.repository.DebateRepository;
import com.bantar.service.interfaces.QuestionService;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DebateService implements QuestionService {

    private static final Logger logger = LogManager.getLogger(DebateService.class);
    private final DebateRepository debateRepository;
    private final DebateCategoryRepository debateCategoryRepository;

    private volatile List<ResponseDTO<DebateCategory>> cachedDebates;

    @Autowired
    public DebateService(DebateRepository debateRepository, DebateCategoryRepository debateCategoryRepository) {
        logger.info("Initializing DebateService");
        this.debateRepository = debateRepository;
        this.debateCategoryRepository = debateCategoryRepository;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void initialize() {
        try {
            loadDebates();
        } catch (Exception e) {
            logger.error("An error occurred during the initial debate load", e);
        }
    }

    @Override
    public ResponseDTO<?> getById(int id) {
        ensureDebatesLoaded();
        return findDebateById(id);
    }

    @Override
    public List<ResponseDTO<?>> getByRange(int startId, int limit) {
        ensureDebatesLoaded();
        return findDebatesByRange(startId, limit).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getAll() {
        ensureDebatesLoaded();
        return findDebatesByRange(0, cachedDebates.size()).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByCategory(String category) {
        if (category == null || category.isBlank()) {
            return Collections.emptyList();
        }

        DebateCategory debateCategory;
        try {
            debateCategory = DebateCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }

        ensureDebatesLoaded();

        return findDebatesByCategory(debateCategory).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByCategories(List<String> categories) {
        List<DebateCategory> validCategories = DebateCategory.fromStrings(categories);
        if (validCategories.isEmpty()) {
            return Collections.emptyList();
        }

        ensureDebatesLoaded();

        return findDebatesByCategories(validCategories).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByFilteredCategories(List<String> categories) {
        List<DebateCategory> validCategories = DebateCategory.fromStrings(categories);
        if (validCategories.isEmpty()) {
            return Collections.emptyList();
        }

        ensureDebatesLoaded();

        return findDebatesByFilteredCategories(validCategories).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public void refresh() {
        loadDebates();
    }

    private synchronized void loadDebates() {
        List<DebateEntity> debates = debateRepository.findAll();

        List<Long> debateIds = debates.stream()
                .map(DebateEntity::getId)
                .collect(Collectors.toList());

        List<DebateCategoryEntity> debateCategoryEntities = debateIds.isEmpty()
                ? Collections.emptyList()
                : debateCategoryRepository.findByDebateIdIn(debateIds);

        Map<Long, List<DebateCategory>> debateCategoriesMap = debateCategoryEntities.stream()
                .collect(Collectors.groupingBy(categoryEntity -> categoryEntity.getDebate().getId(),
                        Collectors.mapping(categoryEntity -> DebateCategory.valueOf(categoryEntity.getCategory()), Collectors.toList())));

        cachedDebates = debates.stream()
                .map(q -> {
                    List<DebateCategory> categories = debateCategoriesMap.getOrDefault(q.getId(), Collections.emptyList());
                    return new ResponseDTO<>(q.getText(), q.getId(), categories);
                })
                .collect(Collectors.toList());
    }

    private void ensureDebatesLoaded() {
        if (cachedDebates == null || cachedDebates.isEmpty()) {
            synchronized (this) {
                if (cachedDebates == null || cachedDebates.isEmpty()) {
                    loadDebates();
                }
            }
        }
    }

    private ResponseDTO<DebateCategory> findDebateById(int id) {
        return cachedDebates.stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private List<ResponseDTO<DebateCategory>> findDebatesByRange(int startId, int limit) {
        return cachedDebates.stream()
                .skip(startId)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<DebateCategory>> findDebatesByCategory(DebateCategory category) {
        return cachedDebates.stream()
                .filter(d -> d.getCategories() != null && d.getCategories().contains(category))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<DebateCategory>> findDebatesByCategories(List<DebateCategory> requiredCategories) {
        Set<DebateCategory> requiredCategorySet = new HashSet<>(requiredCategories);
        return cachedDebates.stream()
                .filter(d -> d.getCategories() != null && !Collections.disjoint(new HashSet<>(d.getCategories()), requiredCategorySet))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<DebateCategory>> findDebatesByFilteredCategories(List<DebateCategory> requiredCategories) {
        return cachedDebates.stream()
                .filter(d -> d.getCategories() != null && new HashSet<>(d.getCategories()).containsAll(requiredCategories))
                .collect(Collectors.toList());
    }
}
