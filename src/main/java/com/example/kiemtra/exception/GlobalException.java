package com.example.kiemtra.exception;

import com.example.kiemtra.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse> runtimeExceptionHandler(Exception ex) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(ErrorCode.UNAUTHORIZED_ERROR.getMessage());
        apiResponse.setCode(ErrorCode.UNAUTHORIZED_ERROR.getCode());
        return ResponseEntity.internalServerError().body(apiResponse);
    }
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> appExceptionHandler(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(errorCode.getMessage());
        apiResponse.setCode(errorCode.getCode());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> methodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        String enumkey = e.getFieldError().getDefaultMessage();
        ApiResponse apiResponse = new ApiResponse();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try {
            errorCode = ErrorCode.valueOf(enumkey);
        }catch (IllegalArgumentException ex){

        }
        apiResponse.setMessage(errorCode.getMessage());
        apiResponse.setCode(errorCode.getCode());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<ApiResponse> noResourceFoundExceptionHandler(NoResourceFoundException e) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }
}
