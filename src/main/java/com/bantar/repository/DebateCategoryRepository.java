package com.bantar.repository;

import com.bantar.entity.DebateCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebateCategoryRepository extends JpaRepository<DebateCategoryEntity, Long> {
    List<DebateCategoryEntity> findByDebateIdIn(List<Long> debateIds);
}
