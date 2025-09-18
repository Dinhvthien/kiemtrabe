package com.example.kiemtra.dto.response.listquestion;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse {
    private Long questionId;
    private String content;
    private List<OptionResponse> options;
}
