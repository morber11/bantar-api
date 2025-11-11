package com.bantar.service;

import com.bantar.model.Icebreaker;
import com.bantar.service.interfaces.QuestionMappingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class QuestionMappingServiceImpl implements QuestionMappingService {

    private static final Logger logger = LogManager.getLogger(QuestionMappingServiceImpl.class);
    private final JsonReaderService jsonReaderUtil;

    @Autowired
    public QuestionMappingServiceImpl(JsonReaderService jsonReaderUtil) {
        this.jsonReaderUtil = jsonReaderUtil;
    }

    public List<Icebreaker> getQuestionsFromJsonResource(String resource) {
        logger.info("Initializing QuestionMappingServiceImpl");
        JsonNode jsonNode = jsonReaderUtil.readJsonResource(resource);
        return mapJsonToQuestions(jsonNode);
    }

    public List<Icebreaker> mapJsonToQuestions(JsonNode jsonNode) {
        if (jsonNode == null) {
            logger.warn("jsonNode is null");
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonNode.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Icebreaker.class));
        } catch (IOException e) {
            logger.error("An error occurred in mapJsonToQuestions", e);
            return null;
        }
    }

}
