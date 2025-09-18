package com.example.kiemtra.repository;

import com.example.kiemtra.entity.Question;
import com.example.kiemtra.entity.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    boolean existsByContentAndExamId(String content, Long examId);

    @Query("SELECT q FROM Question q WHERE " +
            "(:search IS NULL OR :search = '' OR UPPER(q.content) LIKE UPPER(CONCAT('%', :search, '%')))")
    Page<Question> findBySearch(Pageable pageable, @Param("search") String search);

    List<Question> findByExamId(Long id);
    long countByExamId(Long examId);
    @Transactional
    @Modifying
    void deleteByExamId(Long id);
}
