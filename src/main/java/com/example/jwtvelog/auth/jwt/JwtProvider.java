package com.example.jwtvelog.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.jwtvelog.common.exception.UnauthorizedException;
import com.example.jwtvelog.model.member.entity.MemberEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    // 엑세스 토큰 유효기간 1일 설정
    private static final int EXP_ACCESS = 1000 * 10;
    // 리프레시 토큰 유효기간 7일 설정
    private static final int EXP_REFRESH = 1000 * 60;
    // 토큰 prefix 설정
    public static final String TOKEN_PREFIX = "Bearer ";
    // 토큰이 담길 헤더
    public static final String HEADER = "Authorization";

    // 토큰 암호화를 위한 시크릿 값 (테스트, 실제로는 이 값은 노출되면 안됨)
    private String SECRET = "24fb2557fad0be76049e6677c3d7fcdb5ebe3cc4483f86751cfd7d4478a6ce6e";

    // login 시 MemberEntity를 입력받아 AccessToken 생성
    public String createToken(MemberEntity member, JwtTokenType tokenType) {
        // 입력된 토큰 타입에 따라 유효기간 설정
        int exp = tokenType.compareTo(JwtTokenType.ACCESS_TOKEN) == 0 ? EXP_ACCESS : EXP_REFRESH;

        // 토큰 생성 후 반환
        return JWT.create()
                .withSubject(member.getIdx().toString()) // 고유값 (주제)
                .withExpiresAt(new Date(System.currentTimeMillis() + exp)) // 만료 시간 설정 (현재 시간 + 유효기간)
                // name을 따로 빼는 게 좋긴 함
                .withClaim("role", member.getRole()) // 역할 claim 설정
                .withClaim("token-type", tokenType.name()) // token-type claim 설정
                .sign(Algorithm.HMAC512(SECRET)); // 시크릿 키를 이용한 암호화(서명)
    }

    public String createToken(Long idx, String role, JwtTokenType tokenType) {
        return JWT.create()
                // 고유값
                .withSubject(idx.toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXP_ACCESS))
                .withClaim("role", role)
                // name을 따로 빼는 게 좋긴 함
                .withClaim("token-type", tokenType.name())
                .sign(Algorithm.HMAC512(SECRET));
    }

    // 토큰 검증 함수
    // 토큰이 유효하면 DecodedJWT 객체를 반환하고, 유효하지 않으면 UnauthorizedException 발생
    public DecodedJWT verify(String jwt) throws UnauthorizedException {
        try {
            // 시크릿 키를 이용해 토큰을 검증한다.
            return JWT.require(Algorithm.HMAC512(SECRET))
                    .build().verify(jwt);
        } catch (Exception e) {
            // 검증 실패 시 예외 발생
            throw new UnauthorizedException("token 값이 잘못되었습니다. " + e.getMessage());
        }
    }
}
