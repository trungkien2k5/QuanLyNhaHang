package com.kien.restaurant.exception;

import org.springframework.http.HttpStatus;

public class UploadFileException extends BusinessException {

    public UploadFileException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }
}