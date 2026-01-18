package com.bantar.controller;

import com.bantar.dto.ResponseDTO;
import com.bantar.service.TopListService;
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
 * Controller for TopList endpoints.
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/toplists")
public class TopListController {

    private static final Logger logger = LoggerFactory.getLogger(TopListController.class);

    private final TopListService topListService;

    @Autowired
    public TopListController(TopListService topListService) {
        this.topListService = topListService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDTO<?>> getById(@PathVariable int id, HttpServletRequest request,
            HttpServletResponse response) {
        ResponseDTO<?> result = topListService.getById(id);
        ResponseEntity<ResponseDTO<?>> resp;
        if (result == null) {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            resp = ResponseEntity.ok(result);
        }

        logger.info("Request URL: {} Method: {} Status: {}",
                request.getRequestURL().toString(), request.getMethod(), resp.getStatusCode().value());

        return resp;
    }

    @GetMapping("/getByRange")
    public ResponseEntity<List<ResponseDTO<?>>> getByRange(@RequestParam(defaultValue = "0") int startId,
            @RequestParam(defaultValue = "100") int limit,
            HttpServletRequest request, HttpServletResponse response) {
        List<ResponseDTO<?>> result = topListService.getByRange(startId, limit);
        ResponseEntity<List<ResponseDTO<?>>> resp = ResponseEntity.ok(result);

        logger.info("Request URL: {} Method: {} Status: {}",
                request.getRequestURL().toString(), request.getMethod(), resp.getStatusCode().value());

        return resp;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ResponseDTO<?>>> getAll(HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity<List<ResponseDTO<?>>> resp = ResponseEntity.ok(topListService.getAll());

        logger.info("Request URL: {} Method: {} Status: {}",
                request.getRequestURL().toString(), request.getMethod(), resp.getStatusCode().value());

        return resp;
    }

    @GetMapping("/getByCategory")
    public ResponseEntity<List<ResponseDTO<?>>> getByCategory(@RequestParam String category, HttpServletRequest request,
            HttpServletResponse response) {
        List<ResponseDTO<?>> result = topListService.getByCategory(category);
        ResponseEntity<List<ResponseDTO<?>>> resp;
        if (result == null || result.isEmpty()) {
            resp = ResponseEntity.badRequest().build();
        } else {
            resp = ResponseEntity.ok(result);
        }

        logger.info("Request URL: {} Method: {} Status: {}",
                request.getRequestURL().toString(), request.getMethod(), resp.getStatusCode().value());

        return resp;
    }

    @GetMapping("/getByCategories")
    public ResponseEntity<List<ResponseDTO<?>>> getByCategories(@RequestParam List<String> categories,
            HttpServletRequest request, HttpServletResponse response) {
        List<ResponseDTO<?>> result = topListService.getByCategories(categories);
        ResponseEntity<List<ResponseDTO<?>>> resp;
        if (result == null || result.isEmpty()) {
            resp = ResponseEntity.badRequest().build();
        } else {
            resp = ResponseEntity.ok(result);
        }

        logger.info("Request URL: {} Method: {} Status: {}",
                request.getRequestURL().toString(), request.getMethod(), resp.getStatusCode().value());

        return resp;
    }

    @GetMapping("/getByFilteredCategories")
    public ResponseEntity<List<ResponseDTO<?>>> getByFilteredCategories(@RequestParam List<String> categories,
            HttpServletRequest request, HttpServletResponse response) {
        List<ResponseDTO<?>> result = topListService.getByFilteredCategories(categories);
        ResponseEntity<List<ResponseDTO<?>>> resp;
        if (result == null || result.isEmpty()) {
            resp = ResponseEntity.badRequest().build();
        } else {
            resp = ResponseEntity.ok(result);
        }

        logger.info("Request URL: {} Method: {} Status: {}",
                request.getRequestURL().toString(), request.getMethod(), resp.getStatusCode().value());

        return resp;
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        topListService.refresh();
        ResponseEntity<Void> resp = new ResponseEntity<>(HttpStatus.OK);

        logger.info("Request URL: {} Method: {} Status: {}",
                request.getRequestURL().toString(), request.getMethod(), resp.getStatusCode().value());

        return resp;
    }
}
