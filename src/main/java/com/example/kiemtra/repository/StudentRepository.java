package com.example.kiemtra.repository;

import com.example.kiemtra.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT s FROM Student s WHERE LOWER(s.userName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Student> findByUserNameContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(String search, String search1, Pageable pageable);
    Optional<Student> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
