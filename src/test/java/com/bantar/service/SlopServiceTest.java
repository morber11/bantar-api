package com.bantar.service;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.AiQuestionEntity;
import com.bantar.model.Icebreaker;
import com.bantar.model.IcebreakerCategory;
import com.bantar.repository.AiQuestionRepository;
import com.bantar.slop.SlopProvider; // use provider abstraction

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SlopServiceTest {

    private SlopService slopService;
    // add autoCloseable to remove warning
    private AutoCloseable closeable;

    @Mock
    private SlopProvider mockSlopProvider;

    @Mock
    private AiQuestionRepository mockAiQuestionRepository;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        slopService = new SlopService(mockSlopProvider, mockAiQuestionRepository);
    }

    @AfterEach
    void destroy() throws Exception {
        closeable.close();
    }

    @Test
    void testGenerateQuestions() throws Exception {
        String mockResponse = "[{\"text\": \"Question 1\"}]";
        when(mockSlopProvider.generate(Mockito.anyString())).thenReturn(mockResponse);
        slopService.generateQuestions(5);

    ResponseDTO<IcebreakerCategory> result = slopService.getRandomQuestion();
        assertNotNull(result);
        assertEquals("Question 1", result.getText());

        Mockito.verify(mockAiQuestionRepository, Mockito.atLeastOnce()).save(Mockito.argThat(entity ->
                entity != null && entity.getText().equals("Question 1") && entity.getHash() != null
        ));
    }

    @Test
    void testPreloadFromDatabase() {
        AiQuestionEntity persisted = new AiQuestionEntity("Persisted Question", "test123hash");
        persisted.setId(123L);
        when(mockAiQuestionRepository.findAll()).thenReturn(java.util.List.of(persisted));

        // manually initialize because spring never calls it
        slopService.initialize();

    ResponseDTO<IcebreakerCategory> q = slopService.getRandomQuestion();

        assertNotNull(q);
        assertEquals("Persisted Question", q.getText());
    }

    @Test
    void testGenerateQuestionsWithEmptyResponse() throws Exception {
        when(mockSlopProvider.generate(Mockito.anyString())).thenReturn(null);
        slopService.generateQuestions(5);

    List<ResponseDTO<IcebreakerCategory>> results = slopService.getAllQuestions();
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGenerateQuestionsWithInvalidJson() throws Exception {
        String mockResponse = "invalid json";
        when(mockSlopProvider.generate(Mockito.anyString())).thenReturn(mockResponse);
        slopService.generateQuestions(5);

    List<ResponseDTO<IcebreakerCategory>> results = slopService.getAllQuestions();
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGenerateQuestionsWithNullResponse() throws Exception {
        when(mockSlopProvider.generate(Mockito.anyString())).thenReturn(null);

        slopService.generateQuestions(5);

    List<ResponseDTO<IcebreakerCategory>> results = slopService.getAllQuestions();
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGenerateQuestionsMultiple() throws Exception {
        String mockResponse = "[{\"text\": \"Question 1\"}, {\"text\": \"Question 2\"}, {\"text\": \"Question 3\"}]";
        when(mockSlopProvider.generate(Mockito.anyString())).thenReturn(mockResponse);
        slopService.generateQuestions(3);

    ResponseDTO<IcebreakerCategory> result = slopService.getRandomQuestion();
        assertNotNull(result);
        assertTrue(result.getText().startsWith("Question"));
    }

    @Test
    void testGetRandomQuestionWhenEmpty() {
    List<ResponseDTO<IcebreakerCategory>> results = slopService.getAllQuestions();
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetRandomQuestionReturnsFromMap() throws Exception {
        String mockResponse = "[{\"text\": \"Sample Question\"}]";
        when(mockSlopProvider.generate(Mockito.anyString())).thenReturn(mockResponse);
        slopService.generateQuestions(1);

    ResponseDTO<IcebreakerCategory> result = slopService.getRandomQuestion();
        assertNotNull(result);
        assertEquals("Sample Question", result.getText());
    }

    @Test
    void testGetAllQuestionsEmpty() {
    List<ResponseDTO<IcebreakerCategory>> results = slopService.getAllQuestions();

        assertNotNull(results);
        assertTrue(results.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> results.add(new ResponseDTO<>("x", 1, null)));
    }

    @Test
    void testGetAllQuestions() {
        Icebreaker q1 = new Icebreaker("Hello world", 42L);
        Icebreaker q2 = new Icebreaker("How are you?", 43L);
        Icebreaker q3 = new Icebreaker("What's up?", 44L);
        slopService.questionMap.put("k1", q1);
        slopService.questionMap.put("k2", q2);
        slopService.questionMap.put("k3", q3);

    List<ResponseDTO<IcebreakerCategory>> questions = slopService.getAllQuestions();
        assertEquals(3, questions.size());
        List<String> texts = questions.stream().map(ResponseDTO::getText).toList();
        assertTrue(texts.contains("Hello world"));
        assertTrue(texts.contains("How are you?"));
        assertTrue(texts.contains("What's up?"));

        slopService.questionMap.clear();
        assertEquals(3, questions.size());
    }
}
