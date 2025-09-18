package com.example.kiemtra.dto.request.listquestion;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamQuestionRequest {
    private Long examId;
    private List<QuestionRequest> questions;
}
