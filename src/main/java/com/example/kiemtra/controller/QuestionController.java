package com.example.kiemtra.controller;

import com.example.kiemtra.dto.ApiResponse;
import com.example.kiemtra.dto.request.QuestionRequest;
import com.example.kiemtra.dto.response.QuestionResponse;
import com.example.kiemtra.service.QuestionService;
import com.example.kiemtra.util.PageResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionController {

    QuestionService questionService;

    @PostMapping
    public ApiResponse<QuestionResponse> createQuestion(@RequestBody QuestionRequest questionRequest) {
        QuestionResponse createdQuestion = questionService.createQuestion(questionRequest);
        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Question created successfully")
                .result(createdQuestion)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<QuestionResponse>> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        PageResponse<QuestionResponse> questions = questionService.getQuestions(page, size, search);
        return ApiResponse.<PageResponse<QuestionResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Questions retrieved successfully")
                .result(questions)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<QuestionResponse> getQuestionById(@PathVariable Long id) {
        QuestionResponse questionResponse = questionService.getById(id);
        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Question retrieved successfully")
                .result(questionResponse)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<QuestionResponse> updateQuestion(@PathVariable Long id, @RequestBody QuestionRequest request) {
        QuestionResponse updatedQuestion = questionService.update(id, request);
        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Question updated successfully")
                .result(updatedQuestion)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long id) {
        questionService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Question deleted successfully")
                .build();
    }
}