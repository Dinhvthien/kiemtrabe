package com.example.kiemtra.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentDTO {
    private Long studentId;
    private String userName;
    private String phoneNumber;
    private String email;
    private Boolean status;
}