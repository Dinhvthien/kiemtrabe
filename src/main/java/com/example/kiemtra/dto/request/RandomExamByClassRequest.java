package com.example.kiemtra.dto.request;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RandomExamByClassRequest {
    private String phoneNumber;
    private String classCode;
}
