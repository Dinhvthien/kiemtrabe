package com.example.kiemtra.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamResultDTO {
    private String examId;
    private String examName;
    private String className;
    private List<QuestionResultDTO> questions;
}