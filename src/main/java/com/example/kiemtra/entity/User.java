package com.example.kiemtra.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String fullName;
    private String userName;
    private String password;
    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(unique = true, nullable = true)
    private String email;

    private Boolean status = true;


}
