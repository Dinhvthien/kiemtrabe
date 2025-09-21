package com.example.kiemtra.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classId;
    @Column(unique = true)
    private String classCode;
    private String className;
    private String description;
    private Boolean isActive;
    private String startDate;
    private String endDate;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    private Boolean isDeleted;
}
