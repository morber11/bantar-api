package com.bantar.config;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@SuppressWarnings("unused")
public class AppConfig {

    // needed so spring can inject google client
    @Bean
    public Client googleClient() {
        return new Client();
    }

    @Bean(name = "slopSeedExecutor")
    public Executor slopSeedExecutor() {
        return Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "slop-seed-executor");
            t.setDaemon(true);
            
            return t;
        });
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}