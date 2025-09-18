package com.example.kiemtra.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentAnswerId;
    private Long studentId;
    private Long classId;
    private Long examId;
    private Long questionId;
    private Long optionId;
}
