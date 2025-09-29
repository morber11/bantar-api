package com.bantar.backend.service;

import com.bantar.entity.QuestionCategoryEntity;
import com.bantar.entity.QuestionEntity;
import com.bantar.model.Question;
import com.bantar.model.QuestionCategory;
import com.bantar.repository.QuestionCategoryRepository;
import com.bantar.repository.QuestionRepository;
import com.bantar.service.QuestionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class QuestionServiceImplTest {

    @Mock
    private QuestionServiceImpl questionService;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    QuestionCategoryRepository questionCategoryRepository;

    // add autoCloseable to remove warning
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

        List<QuestionCategoryEntity> list = Arrays.asList(
                new QuestionCategoryEntity(1L, "ICEBREAKER", question1),
                new QuestionCategoryEntity(2L, "ICEBREAKER", question2),
                new QuestionCategoryEntity(3L, "ICEBREAKER", question3),
                new QuestionCategoryEntity(2L, "CASUAL", question2),
                new QuestionCategoryEntity(3L, "SPORTS", question3)
        );
        return list;
    }

    @Test
    void testGetQuestionById() {
        QuestionEntity question = new QuestionEntity(1, "What is your favorite color?");
        List<QuestionEntity> questions = List.of(question);

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        Question result = questionService.getQuestionById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("What is your favorite color?", result.getText());
    }

    @Test
    void testGetAllQuestions() {
        List<QuestionEntity> questions = createQuestionEntities();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<Question> result = questionService.getAllQuestions();

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


        Question result = questionService.getQuestionById(999);

        assertNull(result);
    }

    @Test
    void testRefreshQuestions() {
        List<QuestionEntity> questions = createQuestionEntities();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        questionService.refreshQuestions();

        List<Question> refreshedQuestions = questionService.getAllQuestions();

        // check fields manually because they will be treated as new objects due to the refresh
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

        List<Question> result = questionService.getQuestionsByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is your dream job?", result.get(0).getText());
        assertEquals("What is your favorite book?", result.get(1).getText());
    }

    @Test
    void testGetQuestionsByRangeWithStartGreaterThanEnd() {
        List<QuestionEntity> questions = createQuestionEntities();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<Question> result = questionService.getQuestionsByRange(2, 1000);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithLimitExceedingSize() {
        List<QuestionEntity> questions = createQuestionEntities();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<Question> result = questionService.getQuestionsByRange(0, 10);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithValidRange() {
        List<QuestionEntity> questions = createQuestionEntities();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<Question> result = questionService.getQuestionsByRange(1, 2);

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
        // we need to be explicit with the questionIds here
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);
        List<Question> result = questionService.getQuestionsByCategory("ICEBREAKER");

        assertNotNull(result);
        assertEquals(questions.size(), result.size());

        result.forEach(question ->
                assertTrue(question.getCategories().contains(QuestionCategory.ICEBREAKER))
        );
    }

    @Test
    void testGetQuestionsByInvalidCategory() {
        List<Question> result = questionService.getQuestionsByCategory("invalidCategory");

        assertNull(result);
    }

    @Test
    void testGetQuestionsByValidCategories() {
        List<String> categories = List.of("ICEBREAKER", "CASUAL");

        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        // we need to be explicit with the questionIds here
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<Question> result = questionService.getQuestionsByCategories(categories);

        assertNotNull(result);
        assertEquals(1, result.size());

        result.forEach(question -> {
            boolean hasIcebreaker = question.getCategories().contains(QuestionCategory.ICEBREAKER);
            boolean hasCasual = question.getCategories().contains(QuestionCategory.CASUAL);

            assertTrue(hasIcebreaker && hasCasual, "Question should have both ICEBREAKER and CASUAL categories");
        });
    }

    @Test
    void testGetQuestionsByCategoriesWithSomeInvalid() {
        List<String> categories = List.of("CASUAL", "invalid");

        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        // we need to be explicit with the questionIds here
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<Question> result = questionService.getQuestionsByCategories(categories);

        assertNotNull(result);
        assertEquals(1, result.size());

        result.forEach(question ->
                assertTrue(question.getCategories().contains(QuestionCategory.CASUAL))
        );
    }

    @Test
    void testGetQuestionsByCategoriesAllInvalid() {
        List<String> categories = List.of("wrong", "invalid");

        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        // we need to be explicit with the questionIds here
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        List<Question> result = questionService.getQuestionsByCategories(categories);

        assertNull(result);
    }

    @Test
    void testGetQuestionsByCategoriesEmptyList() {
        List<Question> result = questionService.getQuestionsByCategories(Collections.emptyList());

        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        // we need to be explicit with the questionIds here
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetQuestionsByCategoriesNullList() {
        List<Question> result = questionService.getQuestionsByCategories(null);

        List<QuestionEntity> questions = createQuestionEntities();
        List<QuestionCategoryEntity> questionCategories = createQuestionCategoriesEntities();

        when(questionRepository.getAllIcebreakers()).thenReturn(questions);
        // we need to be explicit with the questionIds here
        when(questionCategoryRepository.findByQuestionIdIn(Arrays.asList(1L, 2L, 3L))).thenReturn(questionCategories);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
