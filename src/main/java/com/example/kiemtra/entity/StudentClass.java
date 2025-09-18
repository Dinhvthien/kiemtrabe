package com.example.kiemtra.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_class")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentClassId;
    private Long studentId;   // chỉ lưu ID
    private Long classId;  // chỉ lưu ID
    private Boolean isCompletedExam = false;
}
