package com.bantar.controller;

import com.bantar.model.ResponseDTO;
import com.bantar.service.DebateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for debate endpoints.
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/debates")
public class DebateController {

    private final DebateService debateService;

    @Autowired
    public DebateController(DebateService debateService) {
        this.debateService = debateService;
    }

    /**
     * Get a single debate by id.
     * @param id debate id
     * @return 200 with debate when found, 404 when not found
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDTO<?>> getQuestionById(@PathVariable int id) {
        ResponseDTO<?> result = debateService.getById(id);

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Get a list of debates by range.
     * @param startId index to start from (default 0)
     * @param limit max number of debates to return (default 100)
     * @return 200 with list when available, 404 if not
     */
    @GetMapping("/getByRange")
    public ResponseEntity<List<ResponseDTO<?>>> getQuestionsByRange(
            @RequestParam(defaultValue = "0") int startId,
            @RequestParam(defaultValue = "100") int limit
    ) {
        List<ResponseDTO<?>> result = debateService.getByRange(startId, limit);

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Get all debates.
     * @return 200 with list when available, 404 if not
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ResponseDTO<?>>> getAllQuestions() {
        List<ResponseDTO<?>> result = debateService.getAll();

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Get debates that match a single category.
     * @param category category name (case-insensitive)
     * @return 200 with list when valid, 400 when category is invalid
     */
    @GetMapping("/getByCategory")
    public ResponseEntity<List<ResponseDTO<?>>> getQuestionsByCategory(@RequestParam String category) {
        List<ResponseDTO<?>> result = debateService.getByCategory(category);

        if (result == null || result.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Get debates that match any of the supplied categories (inclusive).
     * @param categories list of category names
     * @return 200 with list when at least one valid category, 400 when none
     */
    @GetMapping("/getByCategories")
    public ResponseEntity<List<ResponseDTO<?>>> getQuestionsByCategories(@RequestParam List<String> categories) {
        List<ResponseDTO<?>> result = debateService.getByCategories(categories);

        if (result == null || result.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Get debates that match all the supplied categories (strict filter).
     * @param categories list of category names
     * @return 200 with list when at least one debate matches all categories, 400 when none
     */
    @GetMapping("/getByFilteredCategories")
    public ResponseEntity<List<ResponseDTO<?>>> getQuestionsByFilteredCategories(@RequestParam List<String> categories) {
        List<ResponseDTO<?>> result = debateService.getByFilteredCategories(categories);

        if (result == null || result.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Refresh debates from the data source.
     * This reloads the cached debates.
     * @return 200 on success
     */
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshQuestions() {
        debateService.refresh();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}