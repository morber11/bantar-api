package com.bantar.repository;

import com.bantar.entity.MindReaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MindReaderRepository extends JpaRepository<MindReaderEntity, Long> {

    String GET_ALL_QUERY =
            "SELECT M.ID, M.TEXT " +
                    "FROM MIND_READER M " +
                    "JOIN MIND_READER_CATEGORY MC ON M.ID = MC.MIND_READER_ID " +
                    "ORDER BY M.ID ASC";

    @Query(value = GET_ALL_QUERY, nativeQuery = true)
    List<MindReaderEntity> getAll();
}
