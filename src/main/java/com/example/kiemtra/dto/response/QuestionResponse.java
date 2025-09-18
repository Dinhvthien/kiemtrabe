package com.example.kiemtra.dto.response;

import com.example.kiemtra.entity.Option;
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
    private Long examId;
}