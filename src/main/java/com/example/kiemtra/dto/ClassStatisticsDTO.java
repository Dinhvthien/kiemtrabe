package com.example.kiemtra.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassStatisticsDTO {
    private Long classId;
    private String className;
    private Integer totalStudents;
    private Integer completedExams;
    private Integer notCompletedExams;
    private Double averageScore;
    private Map<String, Long> gradeDistribution; // Phân bố theo xếp loại
}