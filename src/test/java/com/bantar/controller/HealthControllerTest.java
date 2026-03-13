package com.bantar.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

// unnecessary test for the controller, mainly to check the DB exception handling
class HealthControllerTest {

    private HealthController healthController;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        healthController = new HealthController(dataSource);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void healthOk() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(1)).thenReturn(true);

        ResponseEntity<String> resp = healthController.health();

        assertEquals(200, resp.getStatusCode().value());
        assertEquals("OK", resp.getBody());
    }

    @Test
    void healthDbDown() throws Exception {
        when(dataSource.getConnection()).thenThrow(new SQLException("simulated"));

        ResponseEntity<String> resp = healthController.health();

        assertEquals(503, resp.getStatusCode().value());
        assertEquals("ERROR", resp.getBody());
    }
}
