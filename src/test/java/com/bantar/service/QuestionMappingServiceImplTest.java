package com.bantar.service;

import com.bantar.model.Question;
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

class QuestionMappingServiceImplTest {

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

    private List<Question> createQuestions() {
        return Arrays.asList(
                new Question("What is your favorite color?", 1),
                new Question("What is your dream job?", 2),
                new Question("What is your favorite book?", 3)
        );
    }

    @Test
    void testGetQuestionsFromJsonResource() {
        List<Question> questions = createQuestions();
        JsonNode jsonNode = getJsonNodeFromQuestionsList(questions);

        when(jsonReaderService.readJsonResource(anyString())).thenReturn(jsonNode);

        List<Question> result = questionMappingService.getQuestionsFromJsonResource("static/questions/questions_icebreakers.json");

        assertNotNull(result);
        assertEquals(questions.size(), result.size());
        for (int i = 0; i < questions.size(); i++) {
            assertEquals(questions.get(i).getId(), result.get(i).getId());
            assertEquals(questions.get(i).getText(), result.get(i).getText());
        }
    }

    @Test
    void testGetQuestionsFromJsonResourceNullJsonNode() {
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(null);

        List<Question> result = questionMappingService.getQuestionsFromJsonResource("static/questions/questions_icebreakers.json");

        assertNull(result);
    }

    @Test
    void testMapJsonToQuestions() {
        List<Question> questions = createQuestions();
        JsonNode jsonNode = getJsonNodeFromQuestionsList(questions);

        List<Question> result = questionMappingService.mapJsonToQuestions(jsonNode);

        assertNotNull(result);
        assertEquals(questions.size(), result.size());
        for (int i = 0; i < questions.size(); i++) {
            assertEquals(questions.get(i).getId(), result.get(i).getId());
            assertEquals(questions.get(i).getText(), result.get(i).getText());
        }
    }

    @Test
    void testMapJsonToQuestionsNullJsonNode() {
        List<Question> result = questionMappingService.mapJsonToQuestions(null);

        assertNull(result);
    }

    @Test
    void testMapJsonToQuestionsMalformedJson() {
        JsonNode malformedJsonNode = mock(JsonNode.class);
        when(malformedJsonNode.toString()).thenReturn("{malformed json}");

        List<Question> result = questionMappingService.mapJsonToQuestions(malformedJsonNode);

        assertNull(result);
    }

    private JsonNode getJsonNodeFromQuestionsList(List<Question> questions) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.valueToTree(questions);
    }
}

