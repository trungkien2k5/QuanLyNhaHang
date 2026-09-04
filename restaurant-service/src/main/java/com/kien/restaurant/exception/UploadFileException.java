package com.kien.restaurant.exception;

public class UploadFileException extends BadRequestException {

    public UploadFileException(String message) {
        super(message);
    }
}