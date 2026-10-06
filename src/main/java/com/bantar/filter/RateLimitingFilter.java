package com.bantar.filter;

import io.github.bucket4j.Bucket;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int CAPACITY = 50;
    private static final Duration REFILL_PERIOD = Duration.ofMinutes(1);

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final AtomicLong lastIdleBucketRemovalNanos = new AtomicLong(System.nanoTime());

    private Bucket createBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(CAPACITY).refillIntervally(CAPACITY, REFILL_PERIOD))
                .build();
    }

    // a full bucket behaves exactly like a new one, so dropping it loses nothing
    private void removeIdleBuckets() {
        long now = System.nanoTime();
        long lastRemoval = lastIdleBucketRemovalNanos.get();

        if (now - lastRemoval >= REFILL_PERIOD.toNanos()
            && lastIdleBucketRemovalNanos.compareAndSet(lastRemoval, now)) {
            buckets.values().removeIf(bucket -> bucket.getAvailableTokens() == CAPACITY);
        }
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        removeIdleBuckets();

        String clientIp = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createBucket());

        // note: retryAfter is hardcoded to 60 seconds in this - not dynamic
        if (!bucket.tryConsume(1)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests\",\"retryAfter\":60}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}