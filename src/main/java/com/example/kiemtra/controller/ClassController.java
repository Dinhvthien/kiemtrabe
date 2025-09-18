package com.example.kiemtra.controller;

import com.example.kiemtra.dto.ApiResponse;
import com.example.kiemtra.dto.request.ClassRequest;
import com.example.kiemtra.dto.response.ClassResponse;
import com.example.kiemtra.service.ClassService;
import com.example.kiemtra.util.PageResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassController {

    ClassService classService;

    @PostMapping
    public ApiResponse<ClassResponse> createClass(@RequestBody ClassRequest classRequest) {
        ClassResponse createdClass = classService.createClass(classRequest);
        return ApiResponse.<ClassResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Class created successfully")
                .result(createdClass)
                .build();
    }



    @GetMapping
    public ApiResponse<PageResponse<ClassResponse>> getClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        PageResponse<ClassResponse> classes = classService.getClasses(page, size, search);
        return ApiResponse.<PageResponse<ClassResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Classes retrieved successfully")
                .result(classes)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassResponse> getClassById(@PathVariable Long id) {
        ClassResponse classResponse = classService.getById(id);
        return ApiResponse.<ClassResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Class retrieved successfully")
                .result(classResponse)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ClassResponse> updateClass(@PathVariable Long id, @RequestBody ClassRequest dto) {
        ClassResponse updatedClass = classService.update(id, dto);
        return ApiResponse.<ClassResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Class updated successfully")
                .result(updatedClass)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteClass(@PathVariable Long id) {
        classService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Class deleted successfully")
                .build();
    }
}