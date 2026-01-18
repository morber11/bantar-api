package com.bantar.controller;

import com.bantar.service.SyncServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/sync")
public class SyncController {

    private static final Logger logger = LoggerFactory.getLogger(SyncController.class);

    private final SyncServiceImpl syncService;

    @Autowired
    public SyncController(SyncServiceImpl syncService) {
        this.syncService = syncService;
    }

    @GetMapping("/getChecksum")
    public ResponseEntity<Long> getChecksum(HttpServletRequest request, HttpServletResponse response) {
        Long result = syncService.getLatestChecksum();
        ResponseEntity<Long> resp = ResponseEntity.ok(result);

        logger.info("Request URL: {} Method: {} Status: {}",
                request.getRequestURL().toString(), request.getMethod(), resp.getStatusCode().value());

        return resp;
    }

    @GetMapping("/checkIfLatestChecksum")
    public ResponseEntity<Boolean> checkIfLatestChecksum(@RequestParam() long checksum, HttpServletRequest request, HttpServletResponse response) {
        Boolean result = syncService.isLatestChecksum(checksum);
        ResponseEntity<Boolean> resp = ResponseEntity.ok(result);

        logger.info("Request URL: {} Method: {} Status: {}",
                request.getRequestURL().toString(), request.getMethod(), resp.getStatusCode().value());

        return resp;
    }
}
