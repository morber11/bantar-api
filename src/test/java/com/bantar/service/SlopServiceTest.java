package com.bantar.service;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.AiQuestionEntity;
import com.bantar.model.Icebreaker;
import com.bantar.model.IcebreakerCategory;
import com.bantar.repository.AiQuestionRepository;
import com.google.genai.Client;
import com.google.genai.Models;
import com.google.genai.types.GenerateContentResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class SlopServiceTest {

    private SlopService slopService;
    // add autoCloseable to remove warning
    private AutoCloseable closeable;

    @Mock
    private Models mockModels;

    @Mock
    private AiQuestionRepository mockAiQuestionRepository;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Client mockAiClient = new Client();
        ReflectionTestUtils.setField(mockAiClient, "models", mockModels); // janky reflection because it is a final field
        slopService = new SlopService(mockAiClient, mockAiQuestionRepository);
    }

    @AfterEach
    void destroy() throws Exception {
        closeable.close();
    }

    @Test
    void testGenerateQuestions() {
        String mockResponse = "[{\"text\": \"Question 1\"}]";
        GenerateContentResponse response = Mockito.mock(GenerateContentResponse.class);
        when(response.text()).thenReturn(mockResponse);
        when(mockModels.generateContent(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(response);
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
    void testGenerateQuestionsWithEmptyResponse() {
        GenerateContentResponse response = Mockito.mock(GenerateContentResponse.class);
        when(response.text()).thenReturn(null);
        when(mockModels.generateContent(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(response);

        slopService.generateQuestions(5);

    List<ResponseDTO<IcebreakerCategory>> results = slopService.getAllQuestions();
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGenerateQuestionsWithInvalidJson() {
        String mockResponse = "invalid json";
        GenerateContentResponse response = Mockito.mock(GenerateContentResponse.class);
        when(response.text()).thenReturn(mockResponse);
        when(mockModels.generateContent(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(response);

        slopService.generateQuestions(5);

    List<ResponseDTO<IcebreakerCategory>> results = slopService.getAllQuestions();
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGenerateQuestionsWithNullResponse() {
        when(mockModels.generateContent(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(null);

        slopService.generateQuestions(5);

    List<ResponseDTO<IcebreakerCategory>> results = slopService.getAllQuestions();
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGenerateQuestionsMultiple() {
        String mockResponse = "[{\"text\": \"Question 1\"}, {\"text\": \"Question 2\"}, {\"text\": \"Question 3\"}]";
        GenerateContentResponse response = Mockito.mock(GenerateContentResponse.class);
        when(response.text()).thenReturn(mockResponse);
        when(mockModels.generateContent(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(response);

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
    void testGetRandomQuestionReturnsFromMap() {
        String mockResponse = "[{\"text\": \"Sample Question\"}]";
        GenerateContentResponse response = Mockito.mock(GenerateContentResponse.class);
        when(response.text()).thenReturn(mockResponse);
        when(mockModels.generateContent(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(response);

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