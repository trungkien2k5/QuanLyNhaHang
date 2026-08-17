package com.kien.restaurant.exception;

import org.springframework.http.HttpStatus;

public class KhongTimThayException extends BusinessException {

    public KhongTimThayException(String message) {
        super(HttpStatus.NOT_FOUND.value(), message);
    }

}
