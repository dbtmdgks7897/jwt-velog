package com.example.jwtvelog.common.exception.handler;

import com.example.jwtvelog.common.dto.ResDTO;
import com.example.jwtvelog.common.exception.BadRequestException;
import com.example.jwtvelog.common.exception.UnauthorizedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionHandler {

        @ExceptionHandler(BadRequestException.class)
        public HttpEntity<?> handleBadRequestException(BadRequestException e) {
                return new ResponseEntity<>(
                                ResDTO.builder()
                                                .code(-1)
                                                .message(e.getMessage())
                                                .build(),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(UnauthorizedException.class)
        public HttpEntity<?> handleUnauthorizedException(UnauthorizedException e) {
                return new ResponseEntity<>(
                        ResDTO.builder()
                                .code(-2)
                                .message(e.getMessage())
                                .build(),
                        HttpStatus.UNAUTHORIZED);
        }
}
