package com.example.kiemtra.dto.response.listquestion;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionResponse {
    private Long optionId;
    private String optionLabel;
    private String content;
    private Boolean isCorrect;
}
