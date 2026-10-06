package com.bantar.controller;

import com.bantar.service.SyncServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/sync")
public class SyncController {

    private final SyncServiceImpl syncService;

    @Autowired
    public SyncController(SyncServiceImpl syncService) {
        this.syncService = syncService;
    }

    @GetMapping("/getChecksum")
    public ResponseEntity<Long> getChecksum() {
        Long result = syncService.getLatestChecksum();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/checkIfLatestChecksum")
    public ResponseEntity<Boolean> checkIfLatestChecksum(@RequestParam() long checksum) {
        Boolean result = syncService.isLatestChecksum(checksum);
        return ResponseEntity.ok(result);
    }
}
