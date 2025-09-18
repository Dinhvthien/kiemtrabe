package com.example.kiemtra.controller;

import com.example.kiemtra.dto.*;
import com.example.kiemtra.dto.request.StudentClassRequestDTO;
import com.example.kiemtra.service.StudentClassService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student-class")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentClassController {
    StudentClassService studentClassService;

    @PostMapping("/register")
    public ApiResponse<StudentClassResponseDTO> registerStudentToClass(@RequestBody StudentClassRequestDTO request) {
        return studentClassService.registerStudentToClass(request);
    }

    @GetMapping("/class/{classId}/students")
    public ApiResponse<List<StudentClassResponseDTO>> getStudentsByClass(@PathVariable Long classId) {
        return studentClassService.getStudentsByClass(classId);
    }
}