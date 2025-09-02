package com.bantar.controller;

import com.bantar.model.Question;
import com.bantar.service.QuestionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionServiceImpl questionService;

    @Autowired
    public QuestionController(QuestionServiceImpl questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable int id) {
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
        List<Question> result = questionService.getQuestionsByRange(startId, limit);

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> result = questionService.getAllQuestions();

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshQuestions() {
        questionService.refreshQuestions();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
