package com.bantar.controller;

import com.bantar.model.Question;
import com.bantar.service.QuestionServiceImpl;
import com.bantar.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final RateLimiterService rateLimiterService; //TODO: change to middleware at some point
    private final QuestionServiceImpl questionService;

    @Autowired
    public QuestionController(QuestionServiceImpl questionService, RateLimiterService rateLimiterService) {
        this.questionService = questionService;
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable int id) {
        if (rateLimiterService.isRateLimited()) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }

        Question result = questionService.getQuestionById(id);

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/getByRange")
    public ResponseEntity<List<Question>> getQuestionsByRange(
            @RequestParam(defaultValue = "0") int startId,
            @RequestParam(defaultValue = "100") int limit
    ) {
        if (rateLimiterService.isRateLimited()) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }

        List<Question> result = questionService.getQuestionsByRange(startId, limit);

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Question>> getAllQuestions() {
        if (rateLimiterService.isRateLimited()) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }

        List<Question> result = questionService.getAllQuestions();

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshQuestions() {
        if (rateLimiterService.isRateLimited()) {
            return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
        }

        questionService.refreshQuestions();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
