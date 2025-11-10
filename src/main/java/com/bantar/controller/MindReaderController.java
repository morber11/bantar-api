package com.bantar.controller;

import com.bantar.dto.ResponseDTO;
import com.bantar.service.MindReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mindreader")
@SuppressWarnings("unused")
public class MindReaderController {

    private final MindReaderService mindReaderService;

    @Autowired
    public MindReaderController(MindReaderService mindReaderService) {
        this.mindReaderService = mindReaderService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDTO<?>> getById(@PathVariable int id) {
        ResponseDTO<?> result = mindReaderService.getById(id);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getByRange")
    public ResponseEntity<List<ResponseDTO<?>>> getByRange(@RequestParam(defaultValue = "0") int startId,
                                                           @RequestParam(defaultValue = "100") int limit) {
        List<ResponseDTO<?>> result = mindReaderService.getByRange(startId, limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ResponseDTO<?>>> getAll() {
        return ResponseEntity.ok(mindReaderService.getAll());
    }

    @GetMapping("/getByCategory")
    public ResponseEntity<List<ResponseDTO<?>>> getByCategory(@RequestParam String category) {
        List<ResponseDTO<?>> result = mindReaderService.getByCategory(category);
        if (result == null || result.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getByCategories")
    public ResponseEntity<List<ResponseDTO<?>>> getByCategories(@RequestParam List<String> categories) {
        List<ResponseDTO<?>> result = mindReaderService.getByCategories(categories);
        if (result == null || result.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/getByFilteredCategories")
    public ResponseEntity<List<ResponseDTO<?>>> getByFilteredCategories(@RequestParam List<String> categories) {
        List<ResponseDTO<?>> result = mindReaderService.getByFilteredCategories(categories);
        if (result == null || result.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh() {
        mindReaderService.refresh();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
