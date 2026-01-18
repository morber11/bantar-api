package com.bantar.repository;

import com.bantar.entity.TopListEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopListRepository extends JpaRepository<TopListEntity, Long> {
    default List<TopListEntity> getAllTopLists() {
        return findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
