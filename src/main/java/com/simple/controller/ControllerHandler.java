package com.simple.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerHandler {

    @ExceptionHandler(Exception.class)
    public <T> ResponseEntity exception(Exception e) {
        ResponseVO<?> res = ResponseVO.builder()
                .msg(e.getMessage())
                .build();
        return new ResponseEntity(res, HttpStatus.BAD_REQUEST);
    }

}
