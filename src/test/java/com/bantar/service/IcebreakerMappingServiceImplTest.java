package com.bantar.service;

import com.bantar.model.Icebreaker;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IcebreakerMappingServiceImplTest {

    @Mock
    private JsonReaderService jsonReaderService;

    private QuestionMappingServiceImpl questionMappingService;

    // add autoCloseable to remove warning
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        questionMappingService = new QuestionMappingServiceImpl(jsonReaderService);
    }

    @AfterEach
    void destroy() throws Exception {
        closeable.close();
    }

    private List<Icebreaker> createQuestions() {
        return Arrays.asList(
                new Icebreaker("What is your favorite color?", 1),
                new Icebreaker("What is your dream job?", 2),
                new Icebreaker("What is your favorite book?", 3)
        );
    }

    @Test
    void testGetQuestionsFromJsonResource() {
        List<Icebreaker> icebreakers = createQuestions();
        JsonNode jsonNode = getJsonNodeFromQuestionsList(icebreakers);

        when(jsonReaderService.readJsonResource(anyString())).thenReturn(jsonNode);

        List<Icebreaker> result = questionMappingService.getQuestionsFromJsonResource("static/questions/questions_icebreakers.json");

        assertNotNull(result);
        assertEquals(icebreakers.size(), result.size());
        for (int i = 0; i < icebreakers.size(); i++) {
            assertEquals(icebreakers.get(i).getId(), result.get(i).getId());
            assertEquals(icebreakers.get(i).getText(), result.get(i).getText());
        }
    }

    @Test
    void testGetQuestionsFromJsonResourceNullJsonNode() {
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(null);

        List<Icebreaker> result = questionMappingService.getQuestionsFromJsonResource("static/questions/questions_icebreakers.json");

        assertNull(result);
    }

    @Test
    void testMapJsonToQuestions() {
        List<Icebreaker> icebreakers = createQuestions();
        JsonNode jsonNode = getJsonNodeFromQuestionsList(icebreakers);

        List<Icebreaker> result = questionMappingService.mapJsonToQuestions(jsonNode);

        assertNotNull(result);
        assertEquals(icebreakers.size(), result.size());
        for (int i = 0; i < icebreakers.size(); i++) {
            assertEquals(icebreakers.get(i).getId(), result.get(i).getId());
            assertEquals(icebreakers.get(i).getText(), result.get(i).getText());
        }
    }

    @Test
    void testMapJsonToQuestionsNullJsonNode() {
        List<Icebreaker> result = questionMappingService.mapJsonToQuestions(null);

        assertNull(result);
    }

    @Test
    void testMapJsonToQuestionsMalformedJson() {
        JsonNode malformedJsonNode = mock(JsonNode.class);
        when(malformedJsonNode.toString()).thenReturn("{malformed json}");

        List<Icebreaker> result = questionMappingService.mapJsonToQuestions(malformedJsonNode);

        assertNull(result);
    }

    private JsonNode getJsonNodeFromQuestionsList(List<Icebreaker> icebreakers) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.valueToTree(icebreakers);
    }
}

