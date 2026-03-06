package com.bantar.slop;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import static com.bantar.config.Constants.GEMINI_MODEL;

@Service
public class GeminiProvider implements SlopProvider {

    private static final Logger logger = LogManager.getLogger(GeminiProvider.class);
    private final Client aiClient;

    @Autowired
    public GeminiProvider(Client aiClient) {
        this.aiClient = aiClient;
        logger.info("GeminiProvider constructed with client {}", aiClient);
    }

    @PostConstruct
    void started() {
        logger.info("GeminiProvider bean initialized");
    }

    @Override
    public String generate(String prompt) throws Exception {
        GenerateContentResponse response = aiClient.models.generateContent(GEMINI_MODEL, prompt, null);

        if (response == null || response.text() == null) {
            throw new Exception("Empty response from Gemini provider");
        }

        return response.text();
    }
}