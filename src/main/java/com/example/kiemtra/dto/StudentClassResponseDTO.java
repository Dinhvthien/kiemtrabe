package com.example.kiemtra.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentClassResponseDTO {
    private Long studentClassId;
    private StudentDTO student;
    private ClassDTO clazz;
    private Boolean isCompletedExam;
}