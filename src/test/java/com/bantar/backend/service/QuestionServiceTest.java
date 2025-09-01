package com.bantar.backend.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.bantar.model.Question;
import com.bantar.service.JsonReaderService;
import com.bantar.service.QuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

class QuestionServiceTest {

    @Mock
    private JsonReaderService jsonReaderService;

    private QuestionServiceImpl questionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        questionService = new QuestionServiceImpl(jsonReaderService);
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
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(questions);

        Question result = questionService.getQuestionById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("What is your favorite color?", result.getText());
    }

    @Test
    void testGetAllQuestions() {
        List<Question> questions = createQuestions();
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(questions);

        List<Question> result = questionService.getAllQuestions();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionByIdNotFound() {
        List<Question> questions = List.of(new Question("What is your favorite color?", 1));
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(questions);

        Question result = questionService.getQuestionById(999);

        assertNull(result);
    }

    @Test
    void testRefreshQuestions() {
        List<Question> mockQuestions = createQuestions();
        when(jsonReaderService.readJsonResource(eq("static/questions/questions_icebreakers.json")))
                .thenReturn(mockQuestions);

        questionService.refreshQuestions();

        // it is invoked once on construction, so we need to check that it is invoked twice
        verify(jsonReaderService, times(2)).readJsonResource(eq("static/questions/questions_icebreakers.json"));
        assertEquals(mockQuestions, questionService.getAllQuestions());
    }

    @Test
    void testGetQuestionsByRange() {
        List<Question> mockQuestions = createQuestions();
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(mockQuestions);

        List<Question> result = questionService.getQuestionsByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is your dream job?", result.get(0).getText());
        assertEquals("What is your favorite book?", result.get(1).getText());
    }

    @Test
    void testGetQuestionsByRangeWithStartGreaterThanEnd() {
        List<Question> mockQuestions = createQuestions();
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(mockQuestions);

        List<Question> result = questionService.getQuestionsByRange(2, 1000);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithLimitExceedingSize() {
        List<Question> mockQuestions = createQuestions();
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(mockQuestions);

        List<Question> result = questionService.getQuestionsByRange(0, 10);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetQuestionsByRangeWithValidRange() {
        List<Question> mockQuestions = createQuestions();
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(mockQuestions);

        List<Question> result = questionService.getQuestionsByRange(1, 2);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("What is your dream job?", result.get(0).getText());
        assertEquals("What is your favorite book?", result.get(1).getText());
    }
}
