package com.bantar.slop;

import com.google.genai.Client;
import com.google.genai.Models;
import com.google.genai.types.GenerateContentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static com.bantar.config.Constants.GEMINI_MODEL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GeminiProviderTest {

    private GeminiProvider provider;
    private AutoCloseable closeable;

    @Mock
    private Models mockModels;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        // models is a final field; use reflection to inject the mock
        Client realClient = new Client();
        ReflectionTestUtils.setField(realClient, "models", mockModels);
        provider = new GeminiProvider(realClient);
        // shorten delays and keep three retries to make tests fast
        provider.setMaxRetries(3);
        provider.setRetryDelaysMs(new long[]{1, 1, 1});
    }

    @Test
    void generateReturnsTextOnSuccess() throws Exception {
        String prompt = "hello";
        String responseText = "generated text";

        GenerateContentResponse mockResponse = Mockito.mock(GenerateContentResponse.class);
        when(mockResponse.text()).thenReturn(responseText);
        when(mockModels.generateContent(GEMINI_MODEL, prompt, null)).thenReturn(mockResponse);

        String actual = provider.generate(prompt);
        assertEquals(responseText, actual);
    }

    @Test
    void generateThrowsWhenResponseNull() {
        when(mockModels.generateContent(GEMINI_MODEL, "x", null)).thenReturn(null);
        Exception ex = assertThrows(Exception.class, () -> provider.generate("x"));
        assertTrue(ex.getMessage().contains("Empty response"));
    }

    @Test
    void generateThrowsWhenTextNull() {
        GenerateContentResponse mockResponse = Mockito.mock(GenerateContentResponse.class);
        when(mockResponse.text()).thenReturn(null);
        when(mockModels.generateContent(GEMINI_MODEL, "y", null)).thenReturn(mockResponse);

        Exception ex = assertThrows(Exception.class, () -> provider.generate("y"));
        assertTrue(ex.getMessage().contains("Empty response"));
    }

    @Test
    void generateRetriesOnFailureThenSucceeds() throws Exception {
        String prompt = "retry";
        String responseText = "eventual text";

        GenerateContentResponse mockResponse = Mockito.mock(GenerateContentResponse.class);
        when(mockResponse.text()).thenReturn(responseText);

        // first two calls throw, third returns response
        when(mockModels.generateContent(GEMINI_MODEL, prompt, null))
                .thenThrow(new RuntimeException("fail1"))
                .thenThrow(new RuntimeException("fail2"))
                .thenReturn(mockResponse);

        String actual = provider.generate(prompt);
        assertEquals(responseText, actual);
        // verify that the method was invoked three times (2 failures + success)
        Mockito.verify(mockModels, Mockito.times(3)).generateContent(GEMINI_MODEL, prompt, null);
    }

    @Test
    void generateThrowsAfterMaxRetries() {
        String prompt = "alwaysFail";
        when(mockModels.generateContent(GEMINI_MODEL, prompt, null))
                .thenThrow(new RuntimeException("permanent failure"));

        Exception ex = assertThrows(Exception.class, () -> provider.generate(prompt));
        // we expect at least the message from the thrown exception
        assertTrue(ex.getMessage().contains("permanent failure"));
        Mockito.verify(mockModels, Mockito.times(provider.maxRetries))
                .generateContent(GEMINI_MODEL, prompt, null);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }
}