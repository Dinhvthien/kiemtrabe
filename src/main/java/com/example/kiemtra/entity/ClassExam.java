package com.example.kiemtra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "class_exam")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classExamId;
    private Long classId;  // chỉ lưu ID
    private Long examId;   // chỉ lưu ID
    private String examDate;
}
