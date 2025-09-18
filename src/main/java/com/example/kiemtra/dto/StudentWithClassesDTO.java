package com.example.kiemtra.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentWithClassesDTO {
    private StudentDTO student;
    private List<ClassDTO> classes;
}