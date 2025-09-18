package com.example.kiemtra.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateStudentRequest {
    private String userName;
    private String phoneNumber;
    private String email;
    private List<Long> classIds;
}