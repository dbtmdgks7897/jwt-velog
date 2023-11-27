package com.example.jwtvelog.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqReLoginApiV1DTO {
    @NotBlank(message = "refresh 토큰을 입력해주세요.")
    private String refreshToken;
}
