package com.bantar.service;

import com.bantar.entity.DebateCategoryEntity;
import com.bantar.entity.DebateEntity;
import com.bantar.model.DebateCategory;
import com.bantar.dto.ResponseDTO;
import com.bantar.repository.DebateCategoryRepository;
import com.bantar.repository.DebateRepository;
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

class DebateServiceTest {

    @Mock
    private DebateRepository debateRepository;
    @Mock
    private DebateCategoryRepository debateCategoryRepository;

    private DebateService debateService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        debateService = new DebateService(debateRepository, debateCategoryRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    private List<DebateEntity> createDebateEntities() {
        DebateCategoryEntity category = new DebateCategoryEntity();
        category.setCategory("CASUAL");
        List<DebateCategoryEntity> categories = Collections.singletonList(category);

        return Arrays.asList(
                new DebateEntity(1L, "Should we colonize Mars?", categories),
                new DebateEntity(2L, "Is AI a threat?", categories),
                new DebateEntity(3L, "Should voting be mandatory?", categories)
        );
    }

    private List<DebateCategoryEntity> createDebateCategoryEntities() {
        DebateEntity d1 = new DebateEntity();
        d1.setId(1L);

        DebateEntity d2 = new DebateEntity();
        d2.setId(2L);

        DebateEntity d3 = new DebateEntity();
        d3.setId(3L);

        return Arrays.asList(
                new DebateCategoryEntity(1L, "CASUAL", d1),
                new DebateCategoryEntity(2L, "CASUAL", d2),
                new DebateCategoryEntity(3L, "CASUAL", d3),
                new DebateCategoryEntity(2L, "ETHICS", d2),
                new DebateCategoryEntity(3L, "EDUCATION", d3)
        );
    }

    @Test
    void testGetQuestionById() {
        DebateEntity debate = new DebateEntity(1, "Should we colonize Mars?");
        List<DebateEntity> debates = List.of(debate);

        when(debateRepository.findAll()).thenReturn(debates);

        ResponseDTO<?> result = debateService.getById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Should we colonize Mars?", result.getText());
    }

    @Test
    void testGetAllQuestions() {
        List<DebateEntity> debates = createDebateEntities();
        when(debateRepository.findAll()).thenReturn(debates);

        List<ResponseDTO<?>> result = debateService.getAll();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionByIdNotFound() {
        DebateCategoryEntity category = new DebateCategoryEntity();
        category.setCategory("CASUAL");
        List<DebateCategoryEntity> categories = Collections.singletonList(category);

        List<DebateEntity> debates = List.of(new DebateEntity(1, "Should we colonize Mars?", categories));
        when(debateRepository.findAll()).thenReturn(debates);


        ResponseDTO<?> result = debateService.getById(999);

        assertNull(result);
    }

    @Test
    void testRefreshQuestions() {
        List<DebateEntity> debates = createDebateEntities();
        when(debateRepository.findAll()).thenReturn(debates);

        debateService.refresh();

        List<ResponseDTO<?>> refreshed = debateService.getAll();

        assertEquals(debates.size(), refreshed.size());
        for (int i = 0; i < debates.size(); i++) {
            assertEquals(debates.get(i).getId(), refreshed.get(i).getId());
            assertEquals(debates.get(i).getText(), refreshed.get(i).getText());
        }
    }


    @Test
    void testGetQuestionsByRange() {
        List<DebateEntity> debates = createDebateEntities();
        when(debateRepository.findAll()).thenReturn(debates);

        List<ResponseDTO<?>> result = debateService.getByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Is AI a threat?", result.get(0).getText());
        assertEquals("Should voting be mandatory?", result.get(1).getText());
    }

    @Test
    void testGetQuestionsByRangeWithStartGreaterThanEnd() {
        List<DebateEntity> debates = createDebateEntities();
        when(debateRepository.findAll()).thenReturn(debates);

        List<ResponseDTO<?>> result = debateService.getByRange(2, 1000);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithLimitExceedingSize() {
        List<DebateEntity> debates = createDebateEntities();
        when(debateRepository.findAll()).thenReturn(debates);

        List<ResponseDTO<?>> result = debateService.getByRange(0, 10);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithValidRange() {
        List<DebateEntity> debates = createDebateEntities();
        when(debateRepository.findAll()).thenReturn(debates);

        List<ResponseDTO<?>> result = debateService.getByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Is AI a threat?", result.get(0).getText());
        assertEquals("Should voting be mandatory?", result.get(1).getText());
    }

    @Test
    void testGetQuestionsByValidCategory() {
        List<DebateEntity> debates = createDebateEntities();
        List<DebateCategoryEntity> debateCategories = createDebateCategoryEntities();

        when(debateRepository.findAll()).thenReturn(debates);
        when(debateCategoryRepository.findByDebateIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(debateCategories);

        List<ResponseDTO<?>> result = debateService.getByCategory(DebateCategory.CASUAL.name());

        assertNotNull(result);
        assertEquals(debates.size(), result.size());

        result.forEach(dto -> {
            @SuppressWarnings("unchecked")
            List<DebateCategory> resultCategories = (List<DebateCategory>) dto.getCategories();
            assertTrue(resultCategories.contains(DebateCategory.CASUAL));
        });
    }

    @Test
    void testGetQuestionsByInvalidCategory() {
        List<ResponseDTO<?>> result = debateService.getByCategory("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetQuestionsByValidCategories() {
        List<String> categories = List.of("ETHICS");

        List<DebateEntity> debates = createDebateEntities();
        List<DebateCategoryEntity> debateCategories = createDebateCategoryEntities();

        when(debateRepository.findAll()).thenReturn(debates);
        when(debateCategoryRepository.findByDebateIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(debateCategories);

        List<ResponseDTO<?>> result = debateService.getByFilteredCategories(categories);

        assertNotNull(result);
        assertEquals(1, result.size());

        @SuppressWarnings("unchecked")
        List<DebateCategory> resultCategories = (List<DebateCategory>) result.get(0).getCategories();
        boolean hasEthics = resultCategories.contains(DebateCategory.ETHICS);

        assertTrue(hasEthics, "Debate should have ETHICS category");
    }

    @Test
    void testGetQuestionsByFilteredCategoriesWithSomeInvalid() {
        List<String> categories = List.of("ETHICS", "invalid");

        List<DebateEntity> debates = createDebateEntities();
        List<DebateCategoryEntity> debateCategories = createDebateCategoryEntities();

        when(debateRepository.findAll()).thenReturn(debates);
        when(debateCategoryRepository.findByDebateIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(debateCategories);

        List<ResponseDTO<?>> result = debateService.getByFilteredCategories(categories);

        assertNotNull(result);
        assertEquals(1, result.size());

        @SuppressWarnings("unchecked")
        List<DebateCategory> resultCategories = (List<DebateCategory>) result.get(0).getCategories();
        assertTrue(resultCategories.contains(DebateCategory.ETHICS));
    }

    @Test
    void testGetQuestionsByFilteredCategoriesAllInvalid() {
        List<String> categories = List.of("wrong", "invalid");

        List<DebateEntity> debates = createDebateEntities();
        List<DebateCategoryEntity> debateCategories = createDebateCategoryEntities();

        when(debateRepository.findAll()).thenReturn(debates);
        when(debateCategoryRepository.findByDebateIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(debateCategories);

        List<ResponseDTO<?>> result = debateService.getByFilteredCategories(categories);

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByFilteredCategoriesEmptyList() {
        List<DebateEntity> debates = createDebateEntities();
        List<DebateCategoryEntity> debateCategories = createDebateCategoryEntities();

        when(debateRepository.findAll()).thenReturn(debates);
        when(debateCategoryRepository.findByDebateIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(debateCategories);

        List<ResponseDTO<?>> result = debateService.getByFilteredCategories(Collections.emptyList());

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByFilteredCategoriesNullList() {
        List<DebateEntity> debates = createDebateEntities();
        List<DebateCategoryEntity> debateCategories = createDebateCategoryEntities();

        when(debateRepository.findAll()).thenReturn(debates);
        when(debateCategoryRepository.findByDebateIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(debateCategories);

        List<ResponseDTO<?>> result = debateService.getByFilteredCategories(null);

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByCategoriesAnyMatch() {
        List<String> categories = List.of("ETHICS", "EDUCATION");

        List<DebateEntity> debates = createDebateEntities();
        List<DebateCategoryEntity> debateCategories = createDebateCategoryEntities();

        when(debateRepository.findAll()).thenReturn(debates);
        when(debateCategoryRepository.findByDebateIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(debateCategories);

        List<ResponseDTO<?>> result = debateService.getByCategories(categories);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetQuestionsByCategoriesWithSomeInvalid() {
        List<String> categories = List.of("EDUCATION", "invalid");

        List<DebateEntity> debates = createDebateEntities();
        List<DebateCategoryEntity> debateCategories = createDebateCategoryEntities();

        when(debateRepository.findAll()).thenReturn(debates);
        when(debateCategoryRepository.findByDebateIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(debateCategories);

        List<ResponseDTO<?>> result = debateService.getByCategories(categories);

        assertNotNull(result);
        assertEquals(1, result.size());
        @SuppressWarnings("unchecked")
        List<DebateCategory> resultCategories = (List<DebateCategory>) result.get(0).getCategories();
        assertTrue(resultCategories.contains(DebateCategory.EDUCATION));
    }

    @Test
    void testGetQuestionsByCategoriesAllInvalid() {
        List<String> categories = List.of("wrong", "invalid");

        List<DebateEntity> debates = createDebateEntities();
        List<DebateCategoryEntity> debateCategories = createDebateCategoryEntities();

        when(debateRepository.findAll()).thenReturn(debates);
        when(debateCategoryRepository.findByDebateIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(debateCategories);

        List<ResponseDTO<?>> result = debateService.getByCategories(categories);

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByCategoriesEmptyList() {
        List<DebateEntity> debates = createDebateEntities();
        List<DebateCategoryEntity> debateCategories = createDebateCategoryEntities();

        when(debateRepository.findAll()).thenReturn(debates);
        when(debateCategoryRepository.findByDebateIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(debateCategories);

        List<ResponseDTO<?>> result = debateService.getByCategories(Collections.emptyList());

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByCategoriesNullList() {
        List<DebateEntity> debates = createDebateEntities();
        List<DebateCategoryEntity> debateCategories = createDebateCategoryEntities();

        when(debateRepository.findAll()).thenReturn(debates);
        when(debateCategoryRepository.findByDebateIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(debateCategories);

        List<ResponseDTO<?>> result = debateService.getByCategories(null);

        assertTrue(result == null || result.isEmpty());
    }

}
