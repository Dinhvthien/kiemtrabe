package com.example.kiemtra.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamRequest {
    private String examCode;
    private String title;
    private Integer duration;
    private String description;
}