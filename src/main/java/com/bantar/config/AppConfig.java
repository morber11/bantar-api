package com.bantar.config;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("unused")
public class AppConfig {

    // needed so spring can inject google client
    @Bean
    public Client googleClient() {
        return new Client();
    }
}