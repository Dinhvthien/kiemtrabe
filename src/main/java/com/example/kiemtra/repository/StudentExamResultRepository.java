package com.example.kiemtra.repository;

import com.example.kiemtra.entity.StudentAnswer;
import com.example.kiemtra.entity.StudentExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentExamResultRepository extends JpaRepository<StudentExamResult, Long> {
    List<StudentExamResult> findByClassId(Long classId);
    List<StudentExamResult> findByStudentId(Long studentId);
    Optional<StudentExamResult> findByStudentIdAndExamId(Long studentId, Long examId);

    Optional<Object> findByStudentIdAndClassId(Long studentId, Long classId);

    StudentExamResult findByStudentIdAndExamIdAndClassId(Long studentId, Long examId, Long classId);
}
