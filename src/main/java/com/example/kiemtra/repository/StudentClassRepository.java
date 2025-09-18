package com.example.kiemtra.repository;

import com.example.kiemtra.entity.StudentClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, Long> {
    boolean existsByStudentIdAndClassId(Long studentId, Long classId);
    List<StudentClass> findByClassId(Long classId);
    List<StudentClass> findByStudentId(Long studentId);
    void deleteByStudentId(Long studentId);

    Optional<StudentClass> findByStudentIdAndClassId(Long studentId, Long classId);
}
