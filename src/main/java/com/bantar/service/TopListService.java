package com.bantar.service;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.TopListEntity;
import com.bantar.model.TopListCategory;
import com.bantar.repository.TopListRepository;
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
public class TopListService implements QuestionService {

    private static final Logger logger = LogManager.getLogger(TopListService.class);

    private final TopListRepository topListRepository;
    private final AtomicReference<List<ResponseDTO<TopListCategory>>> cachedTopLists = new AtomicReference<>();

    @Autowired
    public TopListService(TopListRepository topListRepository) {
        logger.info("Initializing TopListService");
        this.topListRepository = topListRepository;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void initialize() {
        try {
            loadTopLists();
        } catch (Exception e) {
            logger.error("An error occurred during the initial toplist load", e);
        }
    }

    @Override
    public ResponseDTO<?> getById(int id) {
        ensureTopListsLoaded();
        return findTopListById(id);
    }

    @Override
    public List<ResponseDTO<?>> getByRange(int startId, int limit) {
        ensureTopListsLoaded();
        return findTopListsByRange(startId, limit).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getAll() {
        ensureTopListsLoaded();
        return findTopListsByRange(0, cachedTopLists.get().size()).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByCategory(String category) {
        if (category == null || category.isBlank()) {
            return Collections.emptyList();
        }

        TopListCategory topListCategory;
        try {
            topListCategory = TopListCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }

        ensureTopListsLoaded();

        return findTopListsByCategory(topListCategory).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByCategories(List<String> categories) {
        List<TopListCategory> validCategories = TopListCategory.fromStrings(categories);
        if (validCategories.isEmpty()) {
            return Collections.emptyList();
        }

        ensureTopListsLoaded();

        return findTopListsByCategories(validCategories).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseDTO<?>> getByFilteredCategories(List<String> categories) {
        List<TopListCategory> validCategories = TopListCategory.fromStrings(categories);
        if (validCategories.isEmpty()) {
            return Collections.emptyList();
        }

        ensureTopListsLoaded();

        return findTopListsByFilteredCategories(validCategories).stream()
                .map(p -> (ResponseDTO<?>) p)
                .collect(Collectors.toList());
    }

    @Override
    public void refresh() {
        loadTopLists();
    }

    private synchronized void loadTopLists() {
        List<TopListEntity> topLists = topListRepository.findAllWithCategories();

        cachedTopLists.set(topLists.stream()
                .map(i -> {
                    List<TopListCategory> categories = i.getCategories() == null
                            ? Collections.emptyList()
                            : i.getCategories().stream()
                                    .map(c -> TopListCategory.valueOf(c.getCategory()))
                                    .collect(Collectors.toList());
                    return new ResponseDTO<>(i.getText(), i.getId(), categories);
                })
                .collect(Collectors.toList()));
    }

    private void ensureTopListsLoaded() {
        List<ResponseDTO<TopListCategory>> snapshot = cachedTopLists.get();
        if (snapshot == null || snapshot.isEmpty()) {
            synchronized (this) {
                snapshot = cachedTopLists.get();
                if (snapshot == null || snapshot.isEmpty()) {
                    loadTopLists();
                }
            }
        }
    }

    private ResponseDTO<TopListCategory> findTopListById(int id) {
        return cachedTopLists.get().stream()
                .filter(q -> q.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private List<ResponseDTO<TopListCategory>> findTopListsByRange(int startId, int limit) {
        return cachedTopLists.get().stream()
                .skip(startId)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<TopListCategory>> findTopListsByCategory(TopListCategory category) {
        return cachedTopLists.get().stream()
                .filter(d -> d.getCategories() != null && d.getCategories().contains(category))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<TopListCategory>> findTopListsByCategories(List<TopListCategory> requiredCategories) {
        Set<TopListCategory> requiredCategorySet = new HashSet<>(requiredCategories);
        return cachedTopLists.get().stream()
                .filter(d -> d.getCategories() != null && !Collections.disjoint(new HashSet<>(d.getCategories()), requiredCategorySet))
                .collect(Collectors.toList());
    }

    private List<ResponseDTO<TopListCategory>> findTopListsByFilteredCategories(List<TopListCategory> requiredCategories) {
        return cachedTopLists.get().stream()
                .filter(d -> d.getCategories() != null && new HashSet<>(d.getCategories()).containsAll(requiredCategories))
                .collect(Collectors.toList());
    }
}
