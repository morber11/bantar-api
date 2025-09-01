package com.bantar.service;

import com.bantar.model.Question;

import java.util.List;

public interface QuestionService {
    Question getQuestionById(int id);
    List<Question> getQuestionsByRange(int startId, int limit);
    List<Question> getAllQuestions();
    void refreshQuestions();
}
