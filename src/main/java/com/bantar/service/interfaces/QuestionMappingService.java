package com.bantar.service.interfaces;

import com.bantar.model.Icebreaker;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface QuestionMappingService {
    List<Icebreaker> getQuestionsFromJsonResource(String resource);
    List<Icebreaker> mapJsonToQuestions(JsonNode jsonNode);
}
