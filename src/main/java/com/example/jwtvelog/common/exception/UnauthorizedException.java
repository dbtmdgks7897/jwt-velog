package com.example.jwtvelog.common.exception;

public class UnauthorizedException  extends RuntimeException{

    public UnauthorizedException(String message) {
        super(message);
    }
}