package com.example.kiemtra.dto.request.listquestion;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionRequest {
    private String optionLabel;
    private String content;
    private Boolean isCorrect;
}
