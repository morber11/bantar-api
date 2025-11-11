package com.bantar.service;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.IcebreakerCategoryEntity;
import com.bantar.entity.IcebreakerEntity;
import com.bantar.model.IcebreakerCategory;
import com.bantar.repository.IcebreakerCategoryRepository;
import com.bantar.repository.IcebreakerRepository;
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

class IcebreakerServiceTest {

    private QuestionService questionService;
    @Mock
    private IcebreakerRepository icebreakerRepository;
    @Mock
    IcebreakerCategoryRepository icebreakerCategoryRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        questionService = new IcebreakerService(icebreakerRepository, icebreakerCategoryRepository);
    }

    @AfterEach
    void destroy() throws Exception {
        closeable.close();
    }

    private List<IcebreakerEntity> createQuestionEntities() {
        IcebreakerCategoryEntity category = new IcebreakerCategoryEntity();
        category.setCategory("CASUAL");
        List<IcebreakerCategoryEntity> categories = Collections.singletonList(category);

        return Arrays.asList(
                new IcebreakerEntity(1L, "What is your favorite color?", categories),
                new IcebreakerEntity(2L, "What is your dream job?", categories),
                new IcebreakerEntity(3L, "What is your favorite book?", categories)
        );
    }

    private List<IcebreakerCategoryEntity> createQuestionCategoriesEntities() {
        IcebreakerEntity question1 = new IcebreakerEntity();
        question1.setId(1L);

        IcebreakerEntity question2 = new IcebreakerEntity();
        question2.setId(2L);

        IcebreakerEntity question3 = new IcebreakerEntity();
        question3.setId(3L);

    return Arrays.asList(
        new IcebreakerCategoryEntity(1L, "ROMANTIC", question1),
        new IcebreakerCategoryEntity(2L, "CASUAL", question2),
        new IcebreakerCategoryEntity(3L, "SPORTS", question3)
    );
    }

    @Test
    void testGetQuestionById() {
        IcebreakerEntity question = new IcebreakerEntity(1, "What is your favorite color?");
        List<IcebreakerEntity> questions = List.of(question);

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);

        ResponseDTO<?> dto = questionService.getById(1);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("What is your favorite color?", dto.getText());
    }

    @Test
    void testGetAllQuestions() {
        List<IcebreakerEntity> questions = createQuestionEntities();
        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);

        List<ResponseDTO<?>> result = questionService.getAll();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionByIdNotFound() {
        IcebreakerCategoryEntity category = new IcebreakerCategoryEntity();
        category.setCategory("CASUAL");
        List<IcebreakerCategoryEntity> categories = Collections.singletonList(category);

        List<IcebreakerEntity> questions = List.of(new IcebreakerEntity(1, "What is your favorite color?", categories));
        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);


        ResponseDTO<?> result = questionService.getById(999);

        assertNull(result);
    }

    @Test
    void testRefreshQuestions() {
        List<IcebreakerEntity> questions = createQuestionEntities();
        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);

        questionService.refresh();

        List<ResponseDTO<?>> refreshedQuestions = questionService.getAll();

        assertEquals(questions.size(), refreshedQuestions.size());
        for (int i = 0; i < questions.size(); i++) {
            assertEquals(questions.get(i).getId(), refreshedQuestions.get(i).getId());
            assertEquals(questions.get(i).getText(), refreshedQuestions.get(i).getText());
        }
    }


    @Test
    void testGetQuestionsByRange() {
        List<IcebreakerEntity> questions = createQuestionEntities();
        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);

        List<ResponseDTO<?>> result = questionService.getByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is your dream job?", result.get(0).getText());
        assertEquals("What is your favorite book?", result.get(1).getText());
    }

    @Test
    void testGetQuestionsByRangeWithStartGreaterThanEnd() {
        List<IcebreakerEntity> questions = createQuestionEntities();
        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);

        List<ResponseDTO<?>> result = questionService.getByRange(2, 1000);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithLimitExceedingSize() {
        List<IcebreakerEntity> questions = createQuestionEntities();
        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);

        List<ResponseDTO<?>> result = questionService.getByRange(0, 10);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithValidRange() {
        List<IcebreakerEntity> questions = createQuestionEntities();
        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);

        List<ResponseDTO<?>> result = questionService.getByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is your dream job?", result.get(0).getText());
        assertEquals("What is your favorite book?", result.get(1).getText());
    }

    @Test
    void testGetQuestionsByValidCategory() {
        List<IcebreakerEntity> questions = createQuestionEntities();
        List<IcebreakerCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);
        when(icebreakerCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);
        List<ResponseDTO<?>> result = questionService.getByCategory("CASUAL");

    assertNotNull(result);
    assertEquals(1, result.size());

        result.forEach(dto -> {
            @SuppressWarnings("unchecked") // skip checking the cast
            List<IcebreakerCategory> resultCategories = (List<IcebreakerCategory>) dto.getCategories();
            assertTrue(resultCategories.contains(IcebreakerCategory.CASUAL));
        });
    }

    @Test
    void testGetQuestionsByInvalidCategory() {
        List<ResponseDTO<?>> result = questionService.getByCategory("invalidCategory");

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByValidCategories() {

        List<IcebreakerEntity> questions = createQuestionEntities();
        List<IcebreakerCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);
        when(icebreakerCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByFilteredCategories(List.of("CASUAL"));

        assertNotNull(result);
        assertEquals(1, result.size());

        @SuppressWarnings("unchecked")
        List<IcebreakerCategory> resultCategories = (List<IcebreakerCategory>) result.get(0).getCategories();
        boolean hasCasual = resultCategories.contains(IcebreakerCategory.CASUAL);

        assertTrue(hasCasual, "Question should have CASUAL category");
    }

    @Test
    void testGetQuestionsByFilteredCategoriesWithSomeInvalid() {
        List<String> categories = List.of("CASUAL", "invalid");

        List<IcebreakerEntity> questions = createQuestionEntities();
        List<IcebreakerCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);
        when(icebreakerCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByFilteredCategories(categories);

        assertNotNull(result);
        assertEquals(1, result.size());

        @SuppressWarnings("unchecked") // do not check the cast
        List<IcebreakerCategory> resultCategories = (List<IcebreakerCategory>) result.get(0).getCategories();
        assertTrue(resultCategories.contains(IcebreakerCategory.CASUAL));
    }

    @Test
    void testGetQuestionsByFilteredCategoriesAllInvalid() {
        List<String> categories = List.of("wrong", "invalid");

        List<IcebreakerEntity> questions = createQuestionEntities();
        List<IcebreakerCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);
        when(icebreakerCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByFilteredCategories(categories);

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByFilteredCategoriesEmptyList() {
        List<IcebreakerEntity> questions = createQuestionEntities();
        List<IcebreakerCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);
        when(icebreakerCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByFilteredCategories(Collections.emptyList());

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByFilteredCategoriesNullList() {
        List<IcebreakerEntity> questions = createQuestionEntities();
        List<IcebreakerCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);
        when(icebreakerCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByFilteredCategories(null);

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByCategoriesAnyMatch() {
        List<String> categories = List.of("CASUAL", "SPORTS");

        List<IcebreakerEntity> questions = createQuestionEntities();
        List<IcebreakerCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);
        when(icebreakerCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByCategories(categories);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetQuestionsByCategoriesWithSomeInvalid() {
        List<String> categories = List.of("SPORTS", "invalid");

        List<IcebreakerEntity> questions = createQuestionEntities();
        List<IcebreakerCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);
        when(icebreakerCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByCategories(categories);

        assertNotNull(result);
        assertEquals(1, result.size());
        @SuppressWarnings("unchecked") // do not check the cast
        List<IcebreakerCategory> resultCategories = (List<IcebreakerCategory>) result.get(0).getCategories();
        assertTrue(resultCategories.contains(IcebreakerCategory.SPORTS));
    }

    @Test
    void testGetQuestionsByCategoriesAllInvalid() {
        List<String> categories = List.of("wrong", "invalid");

        List<IcebreakerEntity> questions = createQuestionEntities();
        List<IcebreakerCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);
        when(icebreakerCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByCategories(categories);

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByCategoriesEmptyList() {
        List<IcebreakerEntity> questions = createQuestionEntities();
        List<IcebreakerCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);
        when(icebreakerCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByCategories(Collections.emptyList());

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByCategoriesNullList() {
        List<IcebreakerEntity> questions = createQuestionEntities();
        List<IcebreakerCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(icebreakerRepository.getAllIcebreakers()).thenReturn(questions);
        when(icebreakerCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByCategories(null);

        assertTrue(result == null || result.isEmpty());
    }
}
