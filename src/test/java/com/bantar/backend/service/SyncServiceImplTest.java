package com.bantar.backend.service;

import com.bantar.service.SyncServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import com.bantar.service.JsonReaderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.when;

class SyncServiceImplTest {

    @Mock
    private JsonReaderService jsonReaderService;

    private SyncServiceImpl syncService;

    // add autoCloseable to remove warning
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        syncService = new SyncServiceImpl(jsonReaderService);
    }

    private JsonNode createMockJsonNode() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.createObjectNode().put("key", "value");
    }

    @AfterEach
    void destroy() throws Exception {
        closeable.close();
    }

    @Test
    void testGetLatestChecksum() {
        JsonNode jsonNode = createMockJsonNode();
        // calculated from the createMockJsonNode
        long expectedChecksum = 3910021588L;
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(jsonNode);
        long result = syncService.getLatestChecksum();
        assertEquals(expectedChecksum, result);
    }

    @Test
    void testIsLatestChecksum() {
        JsonNode jsonNode = createMockJsonNode();
        // calculated from the createMockJsonNode
        long checksum = 3910021588L;
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(jsonNode);
        syncService.getLatestChecksum();
        assertTrue(syncService.isLatestChecksum(checksum));
        assertFalse(syncService.isLatestChecksum(checksum + 1));
    }

    @Test
    void testGetLatestChecksumhInvalidJsonData() {
        when(jsonReaderService.readJsonResource(anyString())).thenReturn(null);
        long result = syncService.getLatestChecksum();
        assertEquals(-1L, result);
    }
}
