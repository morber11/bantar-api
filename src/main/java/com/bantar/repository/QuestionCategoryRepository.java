package com.bantar.repository;

import com.bantar.entity.QuestionCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionCategoryRepository extends JpaRepository<QuestionCategoryEntity, Integer> {
    List<QuestionCategoryEntity> findByCategoryCode(String categoryCode);
    List<QuestionCategoryEntity> findByQuestionIdIn(List<Long> questionIds);
}
