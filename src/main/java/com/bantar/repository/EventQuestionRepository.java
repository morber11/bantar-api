package com.bantar.repository;

import com.bantar.entity.EventQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventQuestionRepository extends JpaRepository<EventQuestionEntity, Long> {
    List<EventQuestionEntity> findByEventIdIn(List<Long> eventIds);
}
