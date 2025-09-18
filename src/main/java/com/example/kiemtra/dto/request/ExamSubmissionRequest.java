package com.example.kiemtra.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSubmissionRequest {
    private String studentName;
    private String phoneNumber;
    private String classCode;
    private Long examId;
    private LocalDateTime submissionTime;
    private List<AnswerSubmission> answers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerSubmission {
        private Long questionId;
        private Long selectedOptionId;
    }
}
