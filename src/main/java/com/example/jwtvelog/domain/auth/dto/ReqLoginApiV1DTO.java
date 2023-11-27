package com.example.jwtvelog.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqLoginApiV1DTO {

    @NotBlank(message = "이메일이 정확하지 않습니다.")
    @Email
    private String email;

    @NotBlank(message = "패스워드가 정확하지 않습니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*_,.?~]).{8,15}$")
    private String password;
}
