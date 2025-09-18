package com.example.kiemtra.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubmissionResponse {
    private Long resultId;
    private String studentName;
    private String classCode;
    private Long examId;
    private Integer totalScore;
    private Integer totalCorrect;
    private Integer totalQuestions;
    private String message;
}
