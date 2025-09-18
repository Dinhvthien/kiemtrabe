package com.example.kiemtra.repository;

import com.example.kiemtra.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    void deleteByUserId(Long userId);
    List<UserRole> findByUserId(Long userId);
}
