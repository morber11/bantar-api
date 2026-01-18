package com.bantar.service;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.TopListCategoryEntity;
import com.bantar.entity.TopListEntity;
import com.bantar.model.TopListCategory;
import com.bantar.repository.TopListCategoryRepository;
import com.bantar.repository.TopListRepository;
import com.bantar.service.interfaces.QuestionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TopListServiceTest {

    @Mock
    private TopListRepository topListRepository;
    @Mock
    private TopListCategoryRepository topListCategoryRepository;

    private QuestionService topListService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        topListService = new TopListService(topListRepository, topListCategoryRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    private List<TopListEntity> createTopListEntities() {
        TopListCategoryEntity category = new TopListCategoryEntity();
        category.setCategory("CASUAL");
        List<TopListCategoryEntity> categories = Collections.singletonList(category);

        return Arrays.asList(
                new TopListEntity(1L, "Top 10 Movies of All Time", categories),
                new TopListEntity(2L, "Most Popular Restaurants in 2025", categories),
                new TopListEntity(3L, "Classic Books Everyone Should Read", categories)
        );
    }

    private List<TopListCategoryEntity> createTopListCategoryEntities() {
        TopListEntity t1 = new TopListEntity();
        t1.setId(1L);

        TopListEntity t2 = new TopListEntity();
        t2.setId(2L);

        TopListEntity t3 = new TopListEntity();
        t3.setId(3L);

        return Arrays.asList(
                new TopListCategoryEntity(1L, "TELEVISION_MOVIES", t1),
                new TopListCategoryEntity(2L, "CASUAL", t2),
                new TopListCategoryEntity(3L, "TRAVEL", t3)
        );
    }

    @Test
    void testGetById() {
        TopListEntity item = new TopListEntity(1, "Top 10 Movies of All Time");
        List<TopListEntity> items = List.of(item);

        when(topListRepository.getAllTopLists()).thenReturn(items);

        ResponseDTO<?> dto = topListService.getById(1);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Top 10 Movies of All Time", dto.getText());
    }

    @Test
    void testGetAll() {
        List<TopListEntity> items = createTopListEntities();
        when(topListRepository.getAllTopLists()).thenReturn(items);

        List<ResponseDTO<?>> result = topListService.getAll();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetByIdNotFound() {
        List<TopListEntity> items = List.of(new TopListEntity(1, "Top 10 Movies of All Time"));
        when(topListRepository.getAllTopLists()).thenReturn(items);

        ResponseDTO<?> result = topListService.getById(999);

        assertNull(result);
    }

    @Test
    void testRefresh() {
        List<TopListEntity> items = createTopListEntities();
        when(topListRepository.getAllTopLists()).thenReturn(items);

        topListService.refresh();

        List<ResponseDTO<?>> refreshed = topListService.getAll();

        assertEquals(items.size(), refreshed.size());
        for (int i = 0; i < items.size(); i++) {
            assertEquals(items.get(i).getId(), refreshed.get(i).getId());
            assertEquals(items.get(i).getText(), refreshed.get(i).getText());
        }
    }

    @Test
    void testGetByRange() {
        List<TopListEntity> items = createTopListEntities();
        when(topListRepository.getAllTopLists()).thenReturn(items);

        List<ResponseDTO<?>> result = topListService.getByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Most Popular Restaurants in 2025", result.get(0).getText());
        assertEquals("Classic Books Everyone Should Read", result.get(1).getText());
    }

    @Test
    void testGetByValidCategory() {
        List<TopListEntity> items = createTopListEntities();
        List<TopListCategoryEntity> categories = createTopListCategoryEntities();

        when(topListRepository.getAllTopLists()).thenReturn(items);
        when(topListCategoryRepository.findByTopListIdIn(Arrays.asList(1L,2L,3L))).thenReturn(categories);

        List<ResponseDTO<?>> result = topListService.getByCategory("CASUAL");

        assertNotNull(result);
        assertEquals(1, result.size());

        result.forEach(dto -> {
            @SuppressWarnings("unchecked")
            List<TopListCategory> resultCategories = (List<TopListCategory>) dto.getCategories();
            assertTrue(resultCategories.contains(TopListCategory.CASUAL));
        });
    }

    @Test
    void testGetByInvalidCategory() {
        List<ResponseDTO<?>> result = topListService.getByCategory("invalidCategory");

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetByCategoriesAnyMatch() {
        List<String> categoriesFilter = List.of("CASUAL","TRAVEL");

        List<TopListEntity> items = createTopListEntities();
        List<TopListCategoryEntity> categories = createTopListCategoryEntities();

        when(topListRepository.getAllTopLists()).thenReturn(items);
        when(topListCategoryRepository.findByTopListIdIn(Arrays.asList(1L,2L,3L))).thenReturn(categories);

        List<ResponseDTO<?>> result = topListService.getByCategories(categoriesFilter);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetByFilteredCategories() {
        List<String> categoriesFilter = List.of("TRAVEL","invalid");

        List<TopListEntity> items = createTopListEntities();
        List<TopListCategoryEntity> categories = createTopListCategoryEntities();

        when(topListRepository.getAllTopLists()).thenReturn(items);
        when(topListCategoryRepository.findByTopListIdIn(Arrays.asList(1L,2L,3L))).thenReturn(categories);

        List<ResponseDTO<?>> result = topListService.getByFilteredCategories(categoriesFilter);

        assertNotNull(result);
        assertEquals(1, result.size());

        @SuppressWarnings("unchecked")
        List<TopListCategory> resultCategories = (List<TopListCategory>) result.get(0).getCategories();
        assertTrue(resultCategories.contains(TopListCategory.TRAVEL));
    }

}