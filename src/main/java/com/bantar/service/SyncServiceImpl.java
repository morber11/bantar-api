package com.bantar.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.CRC32;

import static com.bantar.config.Constants.DEFAULT_QUESTIONS_ICEBREAKERS_PATH;

@Service
public class SyncServiceImpl implements SyncService {

    private final JsonReaderService jsonReaderUtil;
    private final AtomicLong latestChecksum = new AtomicLong(0L);

    public SyncServiceImpl(JsonReaderService jsonReaderUtil) {
        this.jsonReaderUtil = jsonReaderUtil;
    }

    @Override
    public long getLatestChecksum() {
        ensureLatestChecksumCalculated();
        return latestChecksum.get();
    }

    @Override
    public boolean isLatestChecksum(long checksum) {
        ensureLatestChecksumCalculated();
        return checksum == latestChecksum.get();
    }

    private void ensureLatestChecksumCalculated() {
        if (latestChecksum.get() == 0L) {
            latestChecksum.set(calculateLatestChecksumFromFile());
        }
    }

    private long calculateLatestChecksumFromFile() {
        try {
            JsonNode questions = jsonReaderUtil.readJsonResource(DEFAULT_QUESTIONS_ICEBREAKERS_PATH);
            String jsonString = questions.toString();
            return calculateChecksum(jsonString);
        } catch (Exception e) {
            return -1L;
        }
    }

    private long calculateChecksum(Object jsonObject) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(jsonObject);

        CRC32 crc32 = new CRC32();
        crc32.update(jsonString.getBytes());

        return crc32.getValue();
    }
}
