package com.example.kiemtra.controller;

import com.example.kiemtra.dto.*;
import com.example.kiemtra.dto.request.OptionRequest;
import com.example.kiemtra.dto.request.RandomExamByClassRequest;
import com.example.kiemtra.dto.request.UpdateStudentRequest;
import com.example.kiemtra.dto.response.OptionResponse;
import com.example.kiemtra.service.ExamService;
import com.example.kiemtra.service.OptionService;
import com.example.kiemtra.service.StudentService;
import com.example.kiemtra.util.PageResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentController {
    StudentService studentService;
    ExamService examService;
    @PostMapping("/random-by-class")
    public ApiResponse<ExamResponseDTO> getRandomExamByClass(
            @Valid @RequestBody RandomExamByClassRequest request) {
        ExamResponseDTO response = examService.getRandomExamByClass(
                request.getPhoneNumber(), request.getClassCode());
        return ApiResponse.<ExamResponseDTO>builder()
                .result(response)
                .build();
    }
    @PostMapping
    public ApiResponse<StudentDTO> createStudent(@RequestBody CreateStudentRequest request) {
        return studentService.createStudent(request);
    }

    @GetMapping
    public ApiResponse<Page<StudentDTO>> getAllStudents(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return studentService.getAllStudents(search, page, size);
    }

    @PutMapping("/{studentId}/classes")
    public ApiResponse<StudentDTO> updateStudentClasses(
            @PathVariable Long studentId,
            @RequestBody List<Long> classIds) {
        return studentService.updateStudentClasses(studentId, classIds);
    }

    @GetMapping("/{studentId}/with-classes")
    public ApiResponse<StudentWithClassesDTO> getStudentWithClasses(@PathVariable Long studentId) {
        return studentService.getStudentWithClasses(studentId);
    }

    @PutMapping("/{studentId}")
    public ApiResponse<StudentDTO> updateStudent(
            @PathVariable Long studentId,
            @RequestBody UpdateStudentRequest request) {
        request.setStudentId(studentId);
        return studentService.updateStudent(request);
    }

    @DeleteMapping("/{studentId}")
    public ApiResponse<Void> deleteStudent(@PathVariable Long studentId) {
        return studentService.deleteStudent(studentId);
    }

    @GetMapping("/phone/{phoneNumber}")
    public ApiResponse<StudentDTO> getStudentByPhoneNumber(@PathVariable String phoneNumber) {
        return studentService.getStudentByPhoneNumber(phoneNumber);
    }
}