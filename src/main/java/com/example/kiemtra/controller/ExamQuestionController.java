package com.example.kiemtra.controller;

import com.example.kiemtra.dto.ApiResponse;

import com.example.kiemtra.dto.request.listquestion.ExamQuestionRequest;
import com.example.kiemtra.dto.request.listquestion.QuestionRequest;
import com.example.kiemtra.dto.response.listquestion.ExamQuestionResponse;
import com.example.kiemtra.dto.response.listquestion.QuestionResponse;
import com.example.kiemtra.service.ExamQuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam-questions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamQuestionController {

    ExamQuestionService examQuestionService;
    @GetMapping("/{examId}")
    public ExamQuestionResponse getExamQuestions(@PathVariable Long examId) {
        return examQuestionService.getQuestionsByExamId(examId);
    }

    @PutMapping("/{questionId}")
    public ExamQuestionResponse updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QuestionRequest request) {
        return examQuestionService.updateQuestion(questionId, request);
    }
    @DeleteMapping("/{questionId}")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long questionId) {
        examQuestionService.deleteQuestion(questionId);
        return ApiResponse.<Void>builder().build();
    }
    @PostMapping
    public ApiResponse<QuestionResponse> createQuestion(@RequestBody QuestionRequest request) {
        QuestionResponse response = examQuestionService.createQuestion(request);
        return ApiResponse.<QuestionResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Question and options created successfully")
                .result(response)
                .build();
    }
    @PostMapping("creates")
    public ApiResponse<ExamQuestionResponse> createExamQuestions(@RequestBody ExamQuestionRequest request) {
        ExamQuestionResponse response = examQuestionService.createExamQuestions(request);
        return ApiResponse.<ExamQuestionResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Exam questions and options created successfully")
                .result(response)
                .build();
    }


}