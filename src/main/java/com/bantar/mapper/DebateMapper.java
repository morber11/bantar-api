package com.bantar.mapper;

import com.bantar.entity.DebateCategoryEntity;
import com.bantar.entity.DebateEntity;
import com.bantar.model.Debate;
import com.bantar.dto.ResponseDTO;
import com.bantar.model.DebateCategory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DebateMapper {
    public static Debate toModel(DebateEntity entity) { return new Debate(entity.getText(), entity.getId()); }

    public static ResponseDTO<DebateCategory> toGenericModel(DebateEntity entity) {
        if (entity == null) {
            return null;
        }

        List<DebateCategory> categories;
        List<DebateCategoryEntity> ents = entity.getCategories();
        if (ents != null) {
            categories = ents.stream()
                    .filter(Objects::nonNull)
                    .map(DebateCategoryEntity::getCategory)
                    .filter(Objects::nonNull)
                    .map(DebateCategory::fromString)
                    .collect(Collectors.toList());
        } else {
            categories = Collections.emptyList();
        }

        return new ResponseDTO<>(entity.getText(), entity.getId(), categories);
    }
}
