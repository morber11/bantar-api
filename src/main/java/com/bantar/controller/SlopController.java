package com.bantar.controller;

import com.bantar.dto.ResponseDTO;
import com.bantar.model.QuestionCategory;
import com.bantar.service.SlopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/slop")
public class SlopController {
    private final SlopService slopService;

    @Autowired
    public SlopController(SlopService slopService) {
        this.slopService = slopService;
    }

    @GetMapping("/getRandom")
    public ResponseEntity<ResponseDTO<QuestionCategory>> getRandomQuestion() {
        ResponseDTO<QuestionCategory> dto = slopService.getRandomQuestion();

        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ResponseDTO<QuestionCategory>>> getAllQuestions() {
        List<ResponseDTO<QuestionCategory>> results = slopService.getAllQuestions();

        if (results == null || results.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
