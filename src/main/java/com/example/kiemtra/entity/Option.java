package com.example.kiemtra.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    private Long questionId;  // chỉ lưu ID

    private String optionLabel; // A, B, C, D
    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean isCorrect = false;
}
