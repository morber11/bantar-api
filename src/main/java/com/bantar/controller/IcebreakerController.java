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
 * Controller for icebreaker endpoints.
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/icebreakers")
public class IcebreakerController {

    private final QuestionService questionService;

    @Autowired
    public IcebreakerController(@Qualifier("icebreakerService") QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * Get a single question by id.
     *
     * @param id question id
     * @return 200 with question when found, 404 when not found
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDTO<?>> getQuestionById(@PathVariable int id) {
        ResponseDTO<?> result = questionService.getById(id);
        ResponseEntity<ResponseDTO<?>> resp;

        if (result == null) {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            resp = new ResponseEntity<>(result, HttpStatus.OK);
        }

        return resp;
    }

    /**
     * Get a list of questions by range.
     *
     * @param startId index to start from (default 0)
     * @param limit   max number of questions to return (default 100)
     * @return 200 with list when available, 404 if not
     */
    @GetMapping("/getByRange")
    public ResponseEntity<List<ResponseDTO<?>>> getQuestionsByRange(
            @RequestParam(defaultValue = "0") int startId,
            @RequestParam(defaultValue = "100") int limit
            
    ) {
        List<ResponseDTO<?>> result = questionService.getByRange(startId, limit);
        ResponseEntity<List<ResponseDTO<?>>> resp;

        if (result == null) {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            resp = new ResponseEntity<>(result, HttpStatus.OK);
        }

        return resp;
    }

    /**
     * Get all questions.
     *
     * @return 200 with list when available, 404 if not
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<ResponseDTO<?>>> getAllQuestions() {
        List<ResponseDTO<?>> result = questionService.getAll();
        ResponseEntity<List<ResponseDTO<?>>> resp;

        if (result == null) {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            resp = new ResponseEntity<>(result, HttpStatus.OK);
        }

        return resp;
    }

    /**
     * Get questions that match a single category.
     *
     * @param category category name (case-insensitive)
     * @return 200 with list when valid, 400 when category is invalid
     */
    @GetMapping("/getByCategory")
    public ResponseEntity<List<ResponseDTO<?>>> getQuestionsByCategory(@RequestParam String category) {
        List<ResponseDTO<?>> result = questionService.getByCategory(category);
        ResponseEntity<List<ResponseDTO<?>>> resp;

        if (result == null || result.isEmpty()) {
            resp = ResponseEntity.badRequest().build();
        } else {
            resp = ResponseEntity.ok(result);
        }

        return resp;
    }

    /**
     * Get questions that match any of the supplied categories (inclusive).
     *
     * @param categories list of category names
     * @return 200 with list when at least one valid category, 400 when none
     */
    @GetMapping("/getByCategories")
    public ResponseEntity<List<ResponseDTO<?>>> getQuestionsByCategories(@RequestParam List<String> categories) {
        List<ResponseDTO<?>> result = questionService.getByCategories(categories);
        ResponseEntity<List<ResponseDTO<?>>> resp;

        if (result == null || result.isEmpty()) {
            resp = ResponseEntity.badRequest().build();
        } else {
            resp = ResponseEntity.ok(result);
        }

        return resp;
    }

    /**
     * Get questions that match all the supplied categories (strict filter).
     *
     * @param categories list of category names
     * @return 200 with list when at least one question matches all categories, 400 when none
     */
    @GetMapping("/getByFilteredCategories")
    public ResponseEntity<List<ResponseDTO<?>>> getQuestionsByFilteredCategories(@RequestParam List<String> categories) {
        List<ResponseDTO<?>> result = questionService.getByFilteredCategories(categories);
        ResponseEntity<List<ResponseDTO<?>>> resp;

        if (result == null || result.isEmpty()) {
            resp = ResponseEntity.badRequest().build();
        } else {
            resp = ResponseEntity.ok(result);
        }

        return resp;
    }

    /**
     * Refresh questions from the data source.
     * This reloads the cached questions.
     *
     * @return 200 on success
     */
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshQuestions() {
        questionService.refresh();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}