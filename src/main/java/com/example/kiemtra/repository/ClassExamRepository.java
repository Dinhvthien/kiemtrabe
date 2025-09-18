package com.example.kiemtra.repository;

import com.example.kiemtra.entity.ClassExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassExamRepository extends JpaRepository<ClassExam, Long> {
    boolean existsByClassIdAndExamId(Long classId, Long examId);

    List<ClassExam> findByClassId(Long classId);
    List<ClassExam> findByExamId(Long examId);

    void deleteByClassId(Long classId);

    void deleteByExamId(Long id);
}
