package com.bantar.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/health")
@SuppressWarnings("unused")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    private final DataSource dataSource;

    @Autowired
    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<String> health() {
        try (Connection c = dataSource.getConnection()) {
            if (c != null && c.isValid(1)) {
                return ResponseEntity.ok("OK");
            }
        } catch (SQLException e) {
            logger.warn("Health check: database unavailable");
        }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("ERROR");
    }
}
