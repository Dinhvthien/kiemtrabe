package com.example.kiemtra.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateStudentRequest {
    private Long studentId;
    private String userName;
    private String phoneNumber;
    private String email;
    private Boolean status;
}
