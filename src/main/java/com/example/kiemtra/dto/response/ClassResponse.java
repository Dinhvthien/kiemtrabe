package com.example.kiemtra.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassResponse {
    private Long classId;
    private String classCode;
    private String className;
    private String description;
    private String startDate;
    private String endDate;
    private String imageUrl;
}
