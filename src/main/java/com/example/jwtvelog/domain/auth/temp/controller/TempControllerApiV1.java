package com.example.jwtvelog.domain.auth.temp.controller;

import com.example.jwtvelog.auth.jwt.JwtToken;
import com.example.jwtvelog.auth.session.CustomUserDetails;
import com.example.jwtvelog.common.dto.ResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/temp")
public class TempControllerApiV1 {

    @GetMapping
    public HttpEntity<?> temp(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        // customUserDetails에는 로그인한 유저의 정보가 들어있다.
        return new ResponseEntity<>(
                ResDTO.builder()
                        .code(0)
                        .message("인증 성공")
                        .data(customUserDetails.getUsername())
                        .build(),
                HttpStatus.OK);
    }
}
