package com.simple.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseVO<T> {

    private T data;

    private String msg;
}
