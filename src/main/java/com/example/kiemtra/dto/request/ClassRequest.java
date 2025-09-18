package com.example.kiemtra.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassRequest {
    private String classCode;
    private String className;
    private String description;
    private String startDate;
    private String endDate;
    private String imageUrl;
    // Danh sách examId để gắn với class
    private List<Long> examIds;
}
