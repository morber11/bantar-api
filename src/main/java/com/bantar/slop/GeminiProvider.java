package com.bantar.slop;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.bantar.config.Constants.GEMINI_MODEL;

@Service
public class GeminiProvider implements SlopProvider {

    private final Client aiClient;

    @Autowired
    public GeminiProvider(Client aiClient) {
        this.aiClient = aiClient;
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