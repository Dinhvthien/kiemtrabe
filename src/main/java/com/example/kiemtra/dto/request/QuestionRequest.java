package com.example.kiemtra.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequest {
    private String content;
    private Long examId;
}