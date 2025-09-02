package com.bantar.service;

import com.bantar.model.Question;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface QuestionMappingService {
    List<Question> getQuestionsFromJsonResource(String resource);
    List<Question> mapJsonToQuestions(JsonNode jsonNode);
}
