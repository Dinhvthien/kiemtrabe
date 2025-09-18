package com.example.kiemtra.controller;

import com.example.kiemtra.dto.ApiResponse;
import com.example.kiemtra.dto.request.ClassExamRequest;
import com.example.kiemtra.dto.response.ClassExamResponse;
import com.example.kiemtra.service.ClassExamService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classexams")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassExamController {

    ClassExamService classExamService;

    @PostMapping
    public ApiResponse<ClassExamResponse> createClassExam(@RequestBody ClassExamRequest request) {
        ClassExamResponse createdClassExam = classExamService.createClassExam(request);
        return ApiResponse.<ClassExamResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("ClassExam created successfully")
                .result(createdClassExam)
                .build();
    }
    @GetMapping("/class/{classId}")
    public ApiResponse<List<ClassExamResponse>> getAllByClassId(@PathVariable Long classId) {
        List<ClassExamResponse> responses = classExamService.getallbyclassid(classId);
        return ApiResponse.<List<ClassExamResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Get class exams successfully")
                .result(responses)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassExamResponse> getClassExamById(@PathVariable Long id) {
        ClassExamResponse classExamResponse = classExamService.getById(id);
        return ApiResponse.<ClassExamResponse>builder()
                .code(HttpStatus.OK.value())
                .message("ClassExam retrieved successfully")
                .result(classExamResponse)
                .build();
    }
    @GetMapping
    public ApiResponse<List<ClassExamResponse>> getClassExam() {
        List<ClassExamResponse> classExamResponse = classExamService.getall();
        return ApiResponse.<List<ClassExamResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("ClassExam retrieved successfully")
                .result(classExamResponse)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ClassExamResponse> updateClassExam(@PathVariable Long id, @RequestBody ClassExamRequest request) {
        ClassExamResponse updatedClassExam = classExamService.update(id, request);
        return ApiResponse.<ClassExamResponse>builder()
                .code(HttpStatus.OK.value())
                .message("ClassExam updated successfully")
                .result(updatedClassExam)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteClassExam(@PathVariable Long id) {
        classExamService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("ClassExam deleted successfully")
                .build();
    }
}