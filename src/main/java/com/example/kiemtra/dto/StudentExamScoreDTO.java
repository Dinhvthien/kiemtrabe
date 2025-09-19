
package com.example.kiemtra.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentExamScoreDTO {
    private String phoneNumber;
    private String fullName;
    private String email;
    private String className;
    private String examName;
    private Double score; // Điểm số thang 100 (null nếu chưa thi)
    private String grade; // Xuất sắc, Giỏi, Khá, Trung bình, Yếu, Chưa thi
    private Integer correctAnswers;
    private Integer totalQuestions;
    private String examStatus; // "Đã thi", "Chưa thi"

    // Helper method để kiểm tra trạng thái thi
    public String getExamStatus() {
        return score != null ? "Đã thi" : "Chưa thi";
    }
}