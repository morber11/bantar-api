package com.bantar.service;

public interface SyncService {
    long getLatestChecksum();
    boolean isLatestChecksum(long checksum);
}
