package com.bantar.backend.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.bantar.model.Question;
import com.bantar.service.QuestionMappingService;
import com.bantar.service.QuestionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;


class QuestionServiceImplTest {

    @Mock
    private QuestionMappingService questionMappingService;

    private QuestionServiceImpl questionService;

    // add autoCloseable to remove warning
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        questionService = new QuestionServiceImpl(questionMappingService);
    }

    @AfterEach
    void destroy() throws Exception {
        closeable.close();
    }

    private List<Question> createQuestions() {
        return Arrays.asList(
                new Question("What is your favorite color?", 1),
                new Question("What is your dream job?", 2),
                new Question("What is your favorite book?", 3)
        );
    }

    @Test
    void testGetQuestionById() {
        Question question = new Question("What is your favorite color?", 1);
        List<Question> questions = List.of(question);

        when(questionMappingService.getQuestionsFromJsonResource(anyString())).thenReturn(questions);

        Question result = questionService.getQuestionById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("What is your favorite color?", result.getText());
    }

    @Test
    void testGetAllQuestions() {
        List<Question> questions = createQuestions();
        when(questionMappingService.getQuestionsFromJsonResource(anyString())).thenReturn(questions);

        List<Question> result = questionService.getAllQuestions();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionByIdNotFound() {
        List<Question> questions = List.of(new Question("What is your favorite color?", 1));
        when(questionMappingService.getQuestionsFromJsonResource(anyString())).thenReturn(questions);


        Question result = questionService.getQuestionById(999);

        assertNull(result);
    }

    @Test
    void testRefreshQuestions() {
        List<Question> questions = createQuestions();

        when(questionMappingService.getQuestionsFromJsonResource(anyString())).thenReturn(questions);

        questionService.refreshQuestions();

        // it is invoked once on construction, so we need to check that it is invoked twice
        verify(questionMappingService, times(2)).getQuestionsFromJsonResource(eq("static/questions/questions_icebreakers.json"));
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
        List<Question> questions = createQuestions();
        when(questionMappingService.getQuestionsFromJsonResource(anyString())).thenReturn(questions);

        List<Question> result = questionService.getQuestionsByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is your dream job?", result.get(0).getText());
        assertEquals("What is your favorite book?", result.get(1).getText());
    }

    @Test
    void testGetQuestionsByRangeWithStartGreaterThanEnd() {
        List<Question> questions = createQuestions();
        when(questionMappingService.getQuestionsFromJsonResource(anyString())).thenReturn(questions);

        List<Question> result = questionService.getQuestionsByRange(2, 1000);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithLimitExceedingSize() {
        List<Question> questions = createQuestions();
        when(questionMappingService.getQuestionsFromJsonResource(anyString())).thenReturn(questions);

        List<Question> result = questionService.getQuestionsByRange(0, 10);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithValidRange() {
        List<Question> questions = createQuestions();
        when(questionMappingService.getQuestionsFromJsonResource(anyString())).thenReturn(questions);

        List<Question> result = questionService.getQuestionsByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is your dream job?", result.get(0).getText());
        assertEquals("What is your favorite book?", result.get(1).getText());
    }
}
