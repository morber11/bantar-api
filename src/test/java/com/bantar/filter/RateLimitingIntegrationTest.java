package com.bantar.filter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RateLimitingIntegrationTest {

    private static final int LIMIT = 50;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void spoofedLeadingForwardedAddressDoesNotBypassTheLimit() {
        for (int i = 0; i < LIMIT; i++) {
            assertEquals(200, requestFrom("fake-" + i + ", 203.0.113.7"));
        }

        assertEquals(429, requestFrom("another-fake, 203.0.113.7"));
    }

    @Test
    void clientsBehindTheProxyAreLimitedIndependently() {
        for (int i = 0; i < LIMIT; i++) {
            requestFrom("203.0.113.20");
        }

        assertEquals(429, requestFrom("203.0.113.20"));
        assertEquals(200, requestFrom("203.0.113.21"));
    }

    private int requestFrom(String forwardedFor) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Forwarded-For", forwardedFor);

        return rest.exchange("/events/getLatestEvents", HttpMethod.GET, new HttpEntity<>(headers), String.class)
                .getStatusCode().value();
    }
}
