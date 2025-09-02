package com.bantar.service;

import com.bantar.model.Question;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class QuestionMappingServiceImpl implements QuestionMappingService {

    private final JsonReaderService jsonReaderUtil;

    @Autowired
    public QuestionMappingServiceImpl(JsonReaderService jsonReaderUtil) {
        this.jsonReaderUtil = jsonReaderUtil;
    }

    public List<Question> getQuestionsFromJsonResource(String resource) {
        JsonNode jsonNode = jsonReaderUtil.readJsonResource(resource);
        return mapJsonToQuestions(jsonNode);
    }

    public List<Question> mapJsonToQuestions(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonNode.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Question.class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
