package com.example.kiemtra.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamResponseDTO {
    private Long examId;
    private Integer duration; // In minutes
    private List<QuestionResponseDTO> questions;
}