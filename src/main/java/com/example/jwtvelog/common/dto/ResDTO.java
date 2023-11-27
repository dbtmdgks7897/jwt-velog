package com.example.jwtvelog.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResDTO<T> {
    // 응답 코드
    private Integer code;
    // 응답 메시지
    private String message;
    // 응답 데이터
    private T data;
}
