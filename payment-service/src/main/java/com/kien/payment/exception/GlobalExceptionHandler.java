package com.kien.payment.exception;

import com.kien.payment.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<Void> handleNotFound(
            ResourceNotFoundException ex) {

        return ApiResponse.<Void>builder()
                .success(false)
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(BadRequestException.class)
    public ApiResponse<Void> handleBadRequest(
            BadRequestException ex) {

        return ApiResponse.<Void>builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    public ApiResponse<Void> handleConflict(
            ConflictException ex) {

        return ApiResponse.<Void>builder()
                .success(false)
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {

        log.error("Unhandled exception", ex);

        return ApiResponse.<Void>builder()
                .success(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Có lỗi xảy ra trên hệ thống")
                .data(null)
                .build();
    }
}