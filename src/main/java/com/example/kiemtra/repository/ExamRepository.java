package com.example.kiemtra.repository;

import com.example.kiemtra.entity.Exam;
import com.example.kiemtra.entity.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    boolean existsByExamCode(String examCode);

    @Query("SELECT e FROM Exam e WHERE " +
            "(:search IS NULL OR :search = '' OR UPPER(e.title) LIKE UPPER(CONCAT('%', :search, '%')) " +
            "OR UPPER(e.examCode) LIKE UPPER(CONCAT('%', :search, '%')))")
    Page<Exam> findBySearch(Pageable pageable, @Param("search") String search);
}
