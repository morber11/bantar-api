package com.bantar.controller;

import com.bantar.service.RateLimiterService;
import com.bantar.service.SyncServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/sync")
public class SyncController {

    private final RateLimiterService rateLimiterService; //TODO: change to middleware at some point
    private final SyncServiceImpl syncService;

    @Autowired
    public SyncController(RateLimiterService rateLimiterService, SyncServiceImpl syncService) {
        this.rateLimiterService = rateLimiterService;
        this.syncService = syncService;
    }

    @GetMapping("/getChecksum")
    public ResponseEntity<Long> getChecksum() {
        if (rateLimiterService.isRateLimited()) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }

        Long result = syncService.getLatestChecksum();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/checkIfLatestChecksum")
    public ResponseEntity<Boolean> checkIfLatestChecksum(@RequestParam() long checksum) {
        if (rateLimiterService.isRateLimited()) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }

        Boolean result = syncService.isLatestChecksum(checksum);
        return ResponseEntity.ok(result);
    }
}
