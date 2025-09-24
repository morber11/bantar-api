package com.bantar.mapper;

import com.bantar.entity.QuestionEntity;
import com.bantar.model.Question;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {
    public static Question toModel(QuestionEntity entity) { return new Question(entity.getText(), entity.getId()); }
}