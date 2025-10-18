package com.bantar.repository;

import com.bantar.entity.DebateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebateRepository extends JpaRepository<DebateEntity, Long> {
}
