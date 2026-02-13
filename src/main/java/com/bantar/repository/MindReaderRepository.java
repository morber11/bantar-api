package com.bantar.repository;

import com.bantar.entity.MindReaderEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MindReaderRepository extends JpaRepository<MindReaderEntity, Long> {

    default List<MindReaderEntity> getAll() {
        return findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
