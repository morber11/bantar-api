package com.bantar.controller;

import com.bantar.dto.ResponseDTO;
import com.bantar.service.interfaces.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for TopList endpoints.
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/toplists")
public class TopListController {

    private final QuestionService topListService;

    @Autowired
    public TopListController(@Qualifier("topListService") QuestionService topListService) {
        this.topListService = topListService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDTO<?>> getById(@PathVariable int id) {
        ResponseDTO<?> result = topListService.getById(id);
        ResponseEntity<ResponseDTO<?>> resp;
        if (result == null) {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            resp = ResponseEntity.ok(result);
        }

        return resp;
    }

    @GetMapping("/getByRange")
    public ResponseEntity<List<ResponseDTO<?>>> getByRange(@RequestParam(defaultValue = "0") int startId,
            @RequestParam(defaultValue = "100") int limit) {
        List<ResponseDTO<?>> result = topListService.getByRange(startId, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ResponseDTO<?>>> getAll() {
        return ResponseEntity.ok(topListService.getAll());
    }

    @GetMapping("/getByCategory")
    public ResponseEntity<List<ResponseDTO<?>>> getByCategory(@RequestParam String category) {
        List<ResponseDTO<?>> result = topListService.getByCategory(category);
        ResponseEntity<List<ResponseDTO<?>>> resp;
        if (result == null || result.isEmpty()) {
            resp = ResponseEntity.badRequest().build();
        } else {
            resp = ResponseEntity.ok(result);
        }

        return resp;
    }

    @GetMapping("/getByCategories")
    public ResponseEntity<List<ResponseDTO<?>>> getByCategories(@RequestParam List<String> categories) {
        List<ResponseDTO<?>> result = topListService.getByCategories(categories);
        ResponseEntity<List<ResponseDTO<?>>> resp;
        if (result == null || result.isEmpty()) {
            resp = ResponseEntity.badRequest().build();
        } else {
            resp = ResponseEntity.ok(result);
        }

        return resp;
    }

    @GetMapping("/getByFilteredCategories")
    public ResponseEntity<List<ResponseDTO<?>>> getByFilteredCategories(@RequestParam List<String> categories) {
        List<ResponseDTO<?>> result = topListService.getByFilteredCategories(categories);
        ResponseEntity<List<ResponseDTO<?>>> resp;
        if (result == null || result.isEmpty()) {
            resp = ResponseEntity.badRequest().build();
        } else {
            resp = ResponseEntity.ok(result);
        }

        return resp;
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh() {
        topListService.refresh();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
