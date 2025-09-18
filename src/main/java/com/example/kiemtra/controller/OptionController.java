package com.example.kiemtra.controller;

import com.example.kiemtra.dto.ApiResponse;
import com.example.kiemtra.dto.request.OptionRequest;
import com.example.kiemtra.dto.response.OptionResponse;
import com.example.kiemtra.service.OptionService;
import com.example.kiemtra.util.PageResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/options")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OptionController {

    OptionService optionService;

    @PostMapping
    public ApiResponse<OptionResponse> createOption(@RequestBody OptionRequest optionRequest) {
        OptionResponse createdOption = optionService.createOption(optionRequest);
        return ApiResponse.<OptionResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Option created successfully")
                .result(createdOption)
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<OptionResponse>> getOptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        PageResponse<OptionResponse> options = optionService.getOptions(page, size, search);
        return ApiResponse.<PageResponse<OptionResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Options retrieved successfully")
                .result(options)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<OptionResponse> getOptionById(@PathVariable Long id) {
        OptionResponse optionResponse = optionService.getById(id);
        return ApiResponse.<OptionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Option retrieved successfully")
                .result(optionResponse)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<OptionResponse> updateOption(@PathVariable Long id, @RequestBody OptionRequest request) {
        OptionResponse updatedOption = optionService.update(id, request);
        return ApiResponse.<OptionResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Option updated successfully")
                .result(updatedOption)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOption(@PathVariable Long id) {
        optionService.delete(id);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Option deleted successfully")
                .build();
    }
}