package com.bantar.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class JsonReaderServiceTest {

    private JsonReaderService jsonReaderService;
    private ClassLoader classLoader;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        classLoader = mock(ClassLoader.class);
        objectMapper = mock(ObjectMapper.class);
        jsonReaderService = new JsonReaderService(classLoader, objectMapper);
    }

    @Test
    void testReadJsonResource_success() throws Exception {
        String resource = "test.json";
        String jsonContent = "{\"key\":\"value\"}";

        InputStream mockInputStream = new ByteArrayInputStream(jsonContent.getBytes());
        when(classLoader.getResourceAsStream(resource)).thenReturn(mockInputStream);

        JsonNode expectedJsonNode = new ObjectMapper().readTree(jsonContent);
        when(objectMapper.readTree(mockInputStream)).thenReturn(expectedJsonNode);

        JsonNode result = jsonReaderService.readJsonResource(resource);

        assertNotNull(result);
        assertEquals(expectedJsonNode, result);
    }

    @Test
    void testReadJsonResource_resourceNotFound() {
        String resource = "non_existent.json";
        when(classLoader.getResourceAsStream(resource)).thenReturn(null);

        JsonNode result = jsonReaderService.readJsonResource(resource);

        assertNull(result);
    }

    @Test
    void testReadJsonResource_ioException() throws Exception {
        String resource = "test.json";

        InputStream mockInputStream = mock(InputStream.class);
        when(classLoader.getResourceAsStream(resource)).thenReturn(mockInputStream);

        doThrow(new IOException("Simulated IOException")).when(objectMapper).readTree(mockInputStream);

        JsonNode result = jsonReaderService.readJsonResource(resource);

        assertNull(result);
    }
}
