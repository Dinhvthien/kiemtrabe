package com.example.kiemtra.repository;

import com.example.kiemtra.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Student> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    @Query("SELECT s FROM Student s WHERE s.studentId = :id AND s.status = true")
    Optional<Student> findByIdAndStatusTrue(@Param("id") Long id);
    @Query("SELECT s FROM Student s WHERE s.status = true AND " +
            "(:search IS NULL OR :search = '' OR UPPER(s.userName) LIKE UPPER(CONCAT('%', :search, '%')) " +
            "OR UPPER(s.phoneNumber) LIKE UPPER(CONCAT('%', :search, '%')) " +
            "OR UPPER(s.email) LIKE UPPER(CONCAT('%', :search, '%')))")
    Page<Student> findBySearchAndStatusTrue(Pageable pageable, String search);
}
