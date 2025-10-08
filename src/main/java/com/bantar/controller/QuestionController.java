package com.bantar.controller;

import com.bantar.model.Question;
import com.bantar.service.QuestionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for question endpoints.
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionServiceImpl questionService;

    @Autowired
    public QuestionController(QuestionServiceImpl questionService) {
        this.questionService = questionService;
    }

    /**
     * Get a single question by id.
     * @param id question id
     * @return 200 with question when found, 404 when not found
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable int id) {
        Question result = questionService.getQuestionById(id);

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Get a list of questions by range.
     * @param startId index to start from (default 0)
     * @param limit max number of questions to return (default 100)
     * @return 200 with list when available, 404 if not
     */
    @GetMapping("/getByRange")
    public ResponseEntity<List<Question>> getQuestionsByRange(
            @RequestParam(defaultValue = "0") int startId,
            @RequestParam(defaultValue = "100") int limit
    ) {
        List<Question> result = questionService.getQuestionsByRange(startId, limit);

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Get all questions.
     * @return 200 with list when available, 404 if not
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> result = questionService.getAllQuestions();

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Get questions that match a single category.
     * @param category category name (case-insensitive)
     * @return 200 with list when valid, 400 when category is invalid
     */
    @GetMapping("/getByCategory")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@RequestParam String category) {
        List<Question> result = questionService.getQuestionsByCategory(category);

        if (result == null) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Get questions that match any of the supplied categories (inclusive).
     * @param categories list of category names
     * @return 200 with list when at least one valid category, 400 when none
     */
    @GetMapping("/getByCategories")
    public ResponseEntity<List<Question>> getQuestionsByCategories(@RequestParam List<String> categories) {
        List<Question> result = questionService.getQuestionsByCategories(categories);

        if (result == null) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        return ResponseEntity.ok(result);
    }


    /**
     * Get questions that match all the supplied categories (strict filter).
     * @param categories list of category names
     * @return 200 with list when at least one question matches all categories, 400 when none
     */
    @GetMapping("/getByFilteredCategories")
    public ResponseEntity<List<Question>> getQuestionsByFilteredCategories(@RequestParam List<String> categories) {
        List<Question> result = questionService.getQuestionsByFilteredCategories(categories);

        if (result == null) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Refresh questions from the data source.
     * This reloads the cached questions.
     * @return 200 on success
     */
    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshQuestions() {
        questionService.refreshQuestions();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
