package com.bantar.service.interfaces;

public interface SyncService {
    long getLatestChecksum();
    boolean isLatestChecksum(long checksum);
}
