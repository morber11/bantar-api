package com.bantar.repository;

import com.bantar.entity.AiQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiQuestionRepository extends JpaRepository<AiQuestionEntity, Long> {
    Optional<AiQuestionEntity> findByHash(String hash);
}
