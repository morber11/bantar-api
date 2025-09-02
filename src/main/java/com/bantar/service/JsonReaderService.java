package com.bantar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class JsonReaderService {

    private final ClassLoader classLoader;
    private final ObjectMapper objectMapper;

    public JsonReaderService(ClassLoader classLoader, ObjectMapper objectMapper) {
        this.classLoader = classLoader;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unused")
    public JsonReaderService() {
        this(JsonReaderService.class.getClassLoader(), new ObjectMapper());
    }

    public JsonNode readJsonResource(String resource) {
        try (InputStream inputStream = classLoader.getResourceAsStream(resource)) {
            if (inputStream == null) {
                return null;
            }
            return readTreeFromStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JsonNode readTreeFromStream(InputStream inputStream) throws IOException {
        return objectMapper.readTree(inputStream);
    }
}
