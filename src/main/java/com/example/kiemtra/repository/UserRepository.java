package com.example.kiemtra.repository;

import com.example.kiemtra.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String name);
    Optional<User> findByFullName(String name);
    boolean existsByUserName(String username);
}
