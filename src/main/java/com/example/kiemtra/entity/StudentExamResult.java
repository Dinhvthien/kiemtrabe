package com.example.kiemtra.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_exam_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;
    private Long studentId;       // Học sinh nào
    private Long classId;         // Lớp nào
    private Long examId;          // Bài kiểm tra nào
    private Integer totalScore;   // Điểm tổng
    private Integer totalCorrect; // Số câu đúng (nếu cần)
    private Integer totalQuestions; // Tổng số câu trong đề
    private LocalDateTime submissionTime; // Thời gian nộp
}
