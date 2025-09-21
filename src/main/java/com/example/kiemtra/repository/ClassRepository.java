package com.example.kiemtra.repository;

import com.example.kiemtra.entity.Class;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    boolean existsByClassCode(String classCode);
    Optional<Class> findByClassCode(String classCode);

    @Query("SELECT c FROM Class c WHERE c.isDeleted = false AND " +
            "(:search IS NULL OR :search = '' OR UPPER(c.className) LIKE UPPER(CONCAT('%', :search, '%')) " +
            "OR UPPER(c.classCode) LIKE UPPER(CONCAT('%', :search, '%')))")
    Page<Class> findBySearchAndIsDeletedFalse(Pageable pageable, @Param("search") String search);
    @Query("SELECT c FROM Class c WHERE c.classId = :id AND c.isDeleted = false")
    Optional<Class> findByIdAndIsDeletedFalse(@Param("id") Long id);

}
