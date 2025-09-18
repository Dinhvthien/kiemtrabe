package com.example.kiemtra.controller;

import com.example.kiemtra.dto.ApiResponse;
import com.example.kiemtra.dto.request.ExamSubmissionRequest;
import com.example.kiemtra.dto.response.ExamSubmissionResponse;
import com.example.kiemtra.service.ExamSubmissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam-submissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamSubmissionController {

    ExamSubmissionService examSubmissionService;

    @PostMapping
    public ApiResponse<ExamSubmissionResponse> submitExam(@RequestBody ExamSubmissionRequest request) {
        ExamSubmissionResponse response = examSubmissionService.submitExam(request);
        return ApiResponse.<ExamSubmissionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Exam submitted successfully")
                .result(response)
                .build();
    }
}
