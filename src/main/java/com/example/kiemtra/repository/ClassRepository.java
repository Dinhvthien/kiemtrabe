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
    @Query("SELECT c FROM Class c WHERE " +
            "(:search IS NULL OR :search = '' OR UPPER(c.className) LIKE UPPER(CONCAT('%', :search, '%')) " +
            "OR UPPER(c.classCode) LIKE UPPER(CONCAT('%', :search, '%')))")
    Page<Class> findBySearch(Pageable pageable, @Param("search") String search);

    Optional<Class> findByClassCode(String classCode);

}
