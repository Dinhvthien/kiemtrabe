package com.example.kiemtra.controller;

import com.example.kiemtra.dto.ApiResponse;
import com.example.kiemtra.dto.request.ExamRequest;
import com.example.kiemtra.dto.response.ExamResponse;
import com.example.kiemtra.service.ExamService;
import com.example.kiemtra.util.PageResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamController {

    ExamService examService;

    @PostMapping
    public ApiResponse<ExamResponse> createExam(@RequestBody ExamRequest examRequest) {
        ExamResponse createdExam = examService.createExam(examRequest);
        return ApiResponse.<ExamResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Exam created successfully")
                .result(createdExam)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ExamResponse>> getExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        PageResponse<ExamResponse> exams = examService.getExams(page, size, search);
        return ApiResponse.<PageResponse<ExamResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Exams retrieved successfully")
                .result(exams)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ExamResponse> getExamById(@PathVariable Long id) {
        ExamResponse examResponse = examService.getById(id);
        return ApiResponse.<ExamResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Exam retrieved successfully")
                .result(examResponse)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ExamResponse> updateExam(@PathVariable Long id, @RequestBody ExamRequest request) {
        ExamResponse updatedExam = examService.update(id, request);
        return ApiResponse.<ExamResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Exam updated successfully")
                .result(updatedExam)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteExam(@PathVariable Long id) {
        examService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Exam deleted successfully")
                .build();
    }
}