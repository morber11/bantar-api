package com.bantar.service;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import io.github.bucket4j.Bandwidth;
import org.springframework.stereotype.Service;

import java.time.Duration;

//TODO: change to middleware
@Service
public class RateLimiterService {

    private final Bucket bucket;

    public RateLimiterService() {
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        this.bucket = Bucket4j.builder().addLimit(limit).build();
    }

    public boolean isRateLimited() {
        return !bucket.tryConsume(1);
    }
}
