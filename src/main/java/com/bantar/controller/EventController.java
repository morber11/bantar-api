package com.bantar.controller;

import com.bantar.dto.EventDTO;
import com.bantar.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller for event endpoints.
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/events")
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    /**
     * Get a list of events that are currently available.
     * @return 200 with list when available, 404 if not
     */
    @GetMapping("/getLatestEvents")
    public ResponseEntity<List<EventDTO>> getLatestEvents(HttpServletRequest request, HttpServletResponse response) {
        List<EventDTO> result = eventService.getCurrentEvents();
        ResponseEntity<List<EventDTO>> resp = new ResponseEntity<>(result, HttpStatus.OK);

        logger.info("Request URL: {} Method: {} Status: {}",
                request.getRequestURL().toString(), request.getMethod(), resp.getStatusCode().value());

        return resp;
    }
}
