package com.example.kiemtra.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamResponse {
    private Long examId;
    private String examCode;
    private String title;
    private Integer duration;
    private String description;
}