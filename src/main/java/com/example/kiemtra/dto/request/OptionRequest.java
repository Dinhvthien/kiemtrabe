package com.example.kiemtra.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionRequest {
    private Long questionId;
    private String optionLabel;
    private String content;
    private Boolean isCorrect;
}