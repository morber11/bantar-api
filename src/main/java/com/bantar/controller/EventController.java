package com.bantar.controller;

import com.bantar.dto.EventDTO;
import com.bantar.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for event endpoints.
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/events")
public class EventController {

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
    public ResponseEntity<List<EventDTO>> getLatestEvents() {
        List<EventDTO> result = eventService.getCurrentEvents();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
