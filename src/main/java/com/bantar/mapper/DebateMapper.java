package com.bantar.mapper;

import com.bantar.entity.DebateEntity;
import com.bantar.model.Question;
import org.springframework.stereotype.Component;

@Component
public class DebateMapper {
    public static Question toModel(DebateEntity entity) { return new Question(entity.getText(), entity.getId()); }
}
