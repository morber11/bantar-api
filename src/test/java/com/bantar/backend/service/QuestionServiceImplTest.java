package com.bantar.backend.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.bantar.entity.QuestionCategoryEntity;
import com.bantar.entity.QuestionEntity;
import com.bantar.model.Question;
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

    private List<QuestionEntity> createQuestions() {
        QuestionCategoryEntity category = new QuestionCategoryEntity();
        category.setCategory("ICEBREAKER");
        List<QuestionCategoryEntity> categories = Collections.singletonList(category);

        return Arrays.asList(
                new QuestionEntity(1, "What is your favorite color?", categories),
                new QuestionEntity(2, "What is your dream job?", categories),
                new QuestionEntity(3, "What is your favorite book?", categories)
        );
    }

    private List<QuestionCategoryEntity> createQuestionCategories() {
        QuestionEntity question1 = new QuestionEntity();
        question1.setId(1L);

        QuestionEntity question2 = new QuestionEntity();
        question2.setId(2L);

        QuestionEntity question3 = new QuestionEntity();
        question3.setId(3L);

        return Arrays.asList(
                new QuestionCategoryEntity(1L, "ICEBREAKER", question1),
                new QuestionCategoryEntity(2L, "CASUAL", question2),
                new QuestionCategoryEntity(3L, "SPORTS", question3)
        );
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
        List<QuestionEntity> questions = createQuestions();
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
        List<QuestionEntity> questions = createQuestions();
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
        List<QuestionEntity> questions = createQuestions();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<Question> result = questionService.getQuestionsByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is your dream job?", result.get(0).getText());
        assertEquals("What is your favorite book?", result.get(1).getText());
    }

    @Test
    void testGetQuestionsByRangeWithStartGreaterThanEnd() {
        List<QuestionEntity> questions = createQuestions();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<Question> result = questionService.getQuestionsByRange(2, 1000);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithLimitExceedingSize() {
        List<QuestionEntity> questions = createQuestions();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<Question> result = questionService.getQuestionsByRange(0, 10);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithValidRange() {
        List<QuestionEntity> questions = createQuestions();
        when(questionRepository.getAllIcebreakers()).thenReturn(questions);

        List<Question> result = questionService.getQuestionsByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is your dream job?", result.get(0).getText());
        assertEquals("What is your favorite book?", result.get(1).getText());
    }
}
