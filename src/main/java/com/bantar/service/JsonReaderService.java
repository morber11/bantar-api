package com.bantar.service;

import com.bantar.model.Question;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class JsonReaderService {

    public List<Question> readJsonResource(String resource) {
        try (InputStream inputStream = JsonReaderService.class.getClassLoader().getResourceAsStream(resource)) {

            if (inputStream == null) {
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Question.class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
