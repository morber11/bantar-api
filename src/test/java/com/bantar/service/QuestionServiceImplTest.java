package com.bantar.service;

import com.bantar.entity.QuestionCategoryEntity;
import com.bantar.entity.QuestionEntity;
import com.bantar.model.ResponseDTO;
import com.bantar.model.QuestionCategory;
import com.bantar.repository.QuestionCategoryRepository;
import com.bantar.repository.QuestionRepository;
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

class QuestionServiceImplTest {

    private QuestionService questionService;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    QuestionCategoryRepository questionCategoryRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        questionService = new QuestionServiceImpl(questionRepository, questionCategoryRepository);
    }

    @AfterEach
    void destroy() throws Exception {
        closeable.close();
    }

    private List<QuestionEntity> createQuestionEntities() {
        QuestionCategoryEntity category = new QuestionCategoryEntity();
        category.setCategory("ICEBREAKER");
        List<QuestionCategoryEntity> categories = Collections.singletonList(category);

        return Arrays.asList(
                new QuestionEntity(1L, "What is your favorite color?", categories),
                new QuestionEntity(2L, "What is your dream job?", categories),
                new QuestionEntity(3L, "What is your favorite book?", categories)
        );
    }

    private List<QuestionCategoryEntity> createQuestionCategoriesEntities() {
        QuestionEntity question1 = new QuestionEntity();
        question1.setId(1L);

        QuestionEntity question2 = new QuestionEntity();
        question2.setId(2L);

        QuestionEntity question3 = new QuestionEntity();
        question3.setId(3L);

        return Arrays.asList(
                new QuestionCategoryEntity(1L, "ICEBREAKER", question1),
                new QuestionCategoryEntity(2L, "ICEBREAKER", question2),
                new QuestionCategoryEntity(3L, "ICEBREAKER", question3),
                new QuestionCategoryEntity(2L, "CASUAL", question2),
                new QuestionCategoryEntity(3L, "SPORTS", question3)
        );
    }

    @Test
    void testGetQuestionById() {
        QuestionEntity question = new QuestionEntity(1, "What is your favorite color?");
        List<QuestionEntity> questions = List.of(question);

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

    ResponseDTO<?> dto = questionService.getById(1);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("What is your favorite color?", dto.getText());
    }

    @Test
    void testGetAllQuestions() {
        List<QuestionEntity> questions = createQuestionEntities();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<ResponseDTO<?>> result = questionService.getAll();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionByIdNotFound() {
        QuestionCategoryEntity category = new QuestionCategoryEntity();
        category.setCategory("ICEBREAKER");
        List<QuestionCategoryEntity> categories = Collections.singletonList(category);

        List<QuestionEntity> questions = List.of(new QuestionEntity(1, "What is your favorite color?", categories));
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);


    ResponseDTO<?> result = questionService.getById(999);

        assertNull(result);
    }

    @Test
    void testRefreshQuestions() {
        List<QuestionEntity> questions = createQuestionEntities();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

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
        List<QuestionEntity> questions = createQuestionEntities();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<ResponseDTO<?>> result = questionService.getByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is your dream job?", result.get(0).getText());
        assertEquals("What is your favorite book?", result.get(1).getText());
    }

    @Test
    void testGetQuestionsByRangeWithStartGreaterThanEnd() {
        List<QuestionEntity> questions = createQuestionEntities();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<ResponseDTO<?>> result = questionService.getByRange(2, 1000);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithLimitExceedingSize() {
        List<QuestionEntity> questions = createQuestionEntities();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<ResponseDTO<?>> result = questionService.getByRange(0, 10);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithValidRange() {
        List<QuestionEntity> questions = createQuestionEntities();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<ResponseDTO<?>> result = questionService.getByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is your dream job?", result.get(0).getText());
        assertEquals("What is your favorite book?", result.get(1).getText());
    }

    @Test
    void testGetQuestionsByValidCategory() {
        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);
        List<ResponseDTO<?>> result = questionService.getByCategory("ICEBREAKER");

        assertNotNull(result);
        assertEquals(questions.size(), result.size());

        result.forEach(dto -> {
            @SuppressWarnings("unchecked") // skip checking the cast
            List<QuestionCategory> resultCategories = (List<QuestionCategory>) dto.getCategories();
            assertTrue(resultCategories.contains(QuestionCategory.ICEBREAKER));
        });
    }

    @Test
    void testGetQuestionsByInvalidCategory() {
        List<ResponseDTO<?>> result = questionService.getByCategory("invalidCategory");

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByValidCategories() {
        List<String> categories = List.of("ICEBREAKER", "CASUAL");

        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByFilteredCategories(categories);

        assertNotNull(result);
        assertEquals(1, result.size());

        @SuppressWarnings("unchecked")
        List<QuestionCategory> resultCategories = (List<QuestionCategory>) result.get(0).getCategories();
        boolean hasIcebreaker = resultCategories.contains(QuestionCategory.ICEBREAKER);
        boolean hasCasual = resultCategories.contains(QuestionCategory.CASUAL);

        assertTrue(hasIcebreaker && hasCasual, "Question should have both ICEBREAKER and CASUAL categories");
    }

    @Test
    void testGetQuestionsByFilteredCategoriesWithSomeInvalid() {
        List<String> categories = List.of("CASUAL", "invalid");

        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByFilteredCategories(categories);

        assertNotNull(result);
        assertEquals(1, result.size());

        @SuppressWarnings("unchecked") // do not check the cast
        List<QuestionCategory> resultCategories = (List<QuestionCategory>) result.get(0).getCategories();
        assertTrue(resultCategories.contains(QuestionCategory.CASUAL));
    }

    @Test
    void testGetQuestionsByFilteredCategoriesAllInvalid() {
        List<String> categories = List.of("wrong", "invalid");

        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByFilteredCategories(categories);

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByFilteredCategoriesEmptyList() {
        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByFilteredCategories(Collections.emptyList());

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByFilteredCategoriesNullList() {
        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByFilteredCategories(null);

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByCategoriesAnyMatch() {
        List<String> categories = List.of("CASUAL", "SPORTS");

        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByCategories(categories);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetQuestionsByCategoriesWithSomeInvalid() {
        List<String> categories = List.of("SPORTS", "invalid");

        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByCategories(categories);

        assertNotNull(result);
        assertEquals(1, result.size());
        @SuppressWarnings("unchecked") // do not check the cast
        List<QuestionCategory> resultCategories = (List<QuestionCategory>) result.get(0).getCategories();
        assertTrue(resultCategories.contains(QuestionCategory.SPORTS));
    }

    @Test
    void testGetQuestionsByCategoriesAllInvalid() {
        List<String> categories = List.of("wrong", "invalid");

        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByCategories(categories);

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByCategoriesEmptyList() {
        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByCategories(Collections.emptyList());

        assertTrue(result == null || result.isEmpty());
    }

    @Test
    void testGetQuestionsByCategoriesNullList() {
        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<ResponseDTO<?>> result = questionService.getByCategories(null);

        assertTrue(result == null || result.isEmpty());
    }

}
