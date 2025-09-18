package com.example.kiemtra.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassDTO {
    private Long classId;
    private String classCode;
    private String className;
    private String description;
    private Boolean isActive;
    private String startDate;
    private String endDate;
    private String imageUrl;
}