package com.example.kiemtra.repository;

import com.example.kiemtra.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByStudentIdAndExamId(Long studentId, Long examId);
}
