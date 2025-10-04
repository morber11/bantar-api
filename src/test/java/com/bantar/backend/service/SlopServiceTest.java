package com.bantar.backend.service;

import com.bantar.model.Question;
import com.bantar.service.SlopService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
public class SlopServiceTest {

    private SlopService slopService;
    // add autoCloseable to remove warning
    private AutoCloseable closeable;

    @Mock
    private Models mockModels;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        Client mockAiClient = new Client();
        ReflectionTestUtils.setField(mockAiClient, "models", mockModels); // janky reflection because it is a final field
        slopService = new SlopService(mockAiClient);
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

        Question result = slopService.getRandomQuestion();
        assertNotNull(result);
        assertEquals("Question 1", result.getText());
    }

    @Test
    void testGenerateQuestionsWithEmptyResponse() {
        GenerateContentResponse response = Mockito.mock(GenerateContentResponse.class);
        when(response.text()).thenReturn(null);
        when(mockModels.generateContent(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(response);

        slopService.generateQuestions(5);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> slopService.getRandomQuestion());
        assertEquals("No questions available", exception.getMessage());
    }

    @Test
    void testGenerateQuestionsWithInvalidJson() {
        String mockResponse = "invalid json";
        GenerateContentResponse response = Mockito.mock(GenerateContentResponse.class);
        when(response.text()).thenReturn(mockResponse);
        when(mockModels.generateContent(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(response);

        slopService.generateQuestions(5);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> slopService.getRandomQuestion());
        assertEquals("No questions available", exception.getMessage());
    }

    @Test
    void testGenerateQuestionsWithNullResponse() {
        when(mockModels.generateContent(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(null);

        slopService.generateQuestions(5);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> slopService.getRandomQuestion());
        assertEquals("No questions available", exception.getMessage());
    }

    @Test
    void testGenerateQuestionsMultiple() {
        String mockResponse = "[{\"text\": \"Question 1\"}, {\"text\": \"Question 2\"}, {\"text\": \"Question 3\"}]";
        GenerateContentResponse response = Mockito.mock(GenerateContentResponse.class);
        when(response.text()).thenReturn(mockResponse);
        when(mockModels.generateContent(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(response);

        slopService.generateQuestions(3);

        Question result = slopService.getRandomQuestion();
        assertNotNull(result);
        assertTrue(result.getText().startsWith("Question"));
    }

    @Test
    void testGetRandomQuestionWhenEmpty() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> slopService.getRandomQuestion());

        assertEquals("No questions available", exception.getMessage());
    }

    @Test
    void testGetRandomQuestionReturnsFromMap() {
        String mockResponse = "[{\"text\": \"Sample Question\"}]";
        GenerateContentResponse response = Mockito.mock(GenerateContentResponse.class);
        when(response.text()).thenReturn(mockResponse);
        when(mockModels.generateContent(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .thenReturn(response);

        slopService.generateQuestions(1);

        Question result = slopService.getRandomQuestion();
        assertNotNull(result);
        assertEquals("Sample Question", result.getText());
    }
}