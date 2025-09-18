package com.example.kiemtra.repository;

import com.example.kiemtra.entity.Option;
import com.example.kiemtra.entity.Question;
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
public interface OptionRepository extends JpaRepository<Option, Long> {
    boolean existsByQuestionIdAndOptionLabel(Long questionId, String optionLabel);

    @Query("SELECT o FROM Option o WHERE " +
            "(:search IS NULL OR :search = '' OR UPPER(o.content) LIKE UPPER(CONCAT('%', :search, '%')) " +
            "OR UPPER(o.optionLabel) LIKE UPPER(CONCAT('%', :search, '%')))")
    Page<Option> findBySearch(Pageable pageable, @Param("search") String search);
    @Transactional
    @Modifying
    void deleteByQuestionId(Long questionId);

    List<Option> findByQuestionId(Long questionId);
}
