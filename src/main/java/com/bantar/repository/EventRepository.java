package com.bantar.repository;

import com.bantar.entity.EventEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    default List<EventEntity> getAllEvents() {
        return findByIsDeletedFalse(Sort.by(Sort.Direction.ASC, "id"));
    }

    List<EventEntity> findByIsDeletedFalse(Sort sort);

    @Query(value = "SELECT * FROM EVENT e WHERE e.FROM_DATE <= :date AND e.UNTIL_DATE >= :date AND COALESCE(e.IS_DELETED, 0) = 0", nativeQuery = true)
    List<EventEntity> getAvailableEvents(@Param("date") LocalDate date);
}
