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

    // tests may override via setters
    int maxRetries = 3;
    long[] retryDelaysMs = {5000, 10000, 20000};

    @Autowired
    public GeminiProvider(Client aiClient) {
        this.aiClient = aiClient;
        logger.info("GeminiProvider constructed with client {}", aiClient);
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public void setRetryDelaysMs(long[] retryDelaysMs) {
        if (retryDelaysMs == null || retryDelaysMs.length == 0) {
            throw new IllegalArgumentException("retryDelaysMs must not be null or empty");
        }
        this.retryDelaysMs = retryDelaysMs;
    }


    @PostConstruct
    void started() {
        logger.info("GeminiProvider bean initialized");
    }

    @SuppressWarnings("BusyWait")
    @Override
    public String generate(String prompt) throws Exception {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                GenerateContentResponse response = aiClient.models.generateContent(GEMINI_MODEL, prompt, null);
                if (response == null || response.text() == null) {
                    throw new Exception("Empty response from Gemini provider");
                }
                return response.text();
            } catch (Exception ex) {
                lastException = ex;
                if (attempt < maxRetries) {
                    long delay = retryDelaysMs[Math.min(attempt - 1, retryDelaysMs.length - 1)];
                    logger.warn("Attempt {}/{} failed: {}. Retrying after {}ms", attempt, maxRetries, ex.getMessage(), delay);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw ie;
                    }
                }
            }
        }

        // messy - find an alternative
        if (lastException != null) {
            throw lastException;
        }

        return "";
    }
}