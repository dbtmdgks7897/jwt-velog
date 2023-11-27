package com.example.jwtvelog.domain.auth.controller;

import com.example.jwtvelog.common.exception.BadRequestException;
import com.example.jwtvelog.domain.auth.dto.ReqLoginApiV1DTO;
import com.example.jwtvelog.domain.auth.dto.ReqReLoginApiV1DTO;
import com.example.jwtvelog.domain.auth.dto.ReqSignUpApiV1DTO;
import com.example.jwtvelog.domain.auth.service.AuthServiceApiV1;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthControllerApiV1 {

    private final AuthServiceApiV1 authServiceApiV1;

    // 회원가입
    @PostMapping("/sign-up")
    public HttpEntity<?> signUp(@RequestBody @Valid ReqSignUpApiV1DTO reqSignUpApiV1DTO, Errors error) {
        // Validation 중 에러 발생 시, BadRequestException 발생
        if (error.hasErrors()) {
            throw new BadRequestException(error.getAllErrors().get(0).getDefaultMessage());
        }
        return authServiceApiV1.signUp(reqSignUpApiV1DTO);
    }

    // 로그인
    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody @Valid ReqLoginApiV1DTO reqLoginApiV1DTO, Errors error) {
        if (error.hasErrors()) {
            throw new BadRequestException(error.getAllErrors().get(0).getDefaultMessage());
        }
        return authServiceApiV1.login(reqLoginApiV1DTO);
    }

    // 재 로그인 (AccessToken 만료 시 RefreshToken으로 재발급, RefreshToken 만료 시 UnauthorizedException 발생)
    @PostMapping("/relogin")
    public HttpEntity<?> reLogin(@RequestBody @Valid ReqReLoginApiV1DTO reqReLoginApiV1DTO, Errors error) {
        if (error.hasErrors()) {
            throw new BadRequestException(error.getAllErrors().get(0).getDefaultMessage());
        }
        return authServiceApiV1.reLogin(reqReLoginApiV1DTO);
    }

}
