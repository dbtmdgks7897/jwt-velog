package com.example.jwtvelog.domain.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.jwtvelog.auth.jwt.JwtProvider;
import com.example.jwtvelog.auth.jwt.JwtToken;
import com.example.jwtvelog.auth.jwt.JwtTokenType;
import com.example.jwtvelog.common.dto.ResDTO;
import com.example.jwtvelog.common.exception.BadRequestException;
import com.example.jwtvelog.common.exception.UnauthorizedException;
import com.example.jwtvelog.domain.auth.dto.ReqLoginApiV1DTO;
import com.example.jwtvelog.domain.auth.dto.ReqReLoginApiV1DTO;
import com.example.jwtvelog.domain.auth.dto.ReqSignUpApiV1DTO;
import com.example.jwtvelog.model.member.entity.MemberEntity;
import com.example.jwtvelog.model.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceApiV1 {

        private final MemberRepository memberRepository;

        private final JwtProvider jwtProvider;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public HttpEntity<?> signUp(ReqSignUpApiV1DTO reqSignUpApiV1DTO) {

                // ReqDTO 기반으로 유저 정보 생성
                MemberEntity memberEntity = MemberEntity.builder()
                        .email(reqSignUpApiV1DTO.getEmail())
                        .password(passwordEncoder.encode(reqSignUpApiV1DTO.getPassword()))
                        .role("ROLE_MEMBER")
                        .build();

                // 유저 정보 저장
                memberRepository.save(memberEntity);

                // 회원가입 성공
                return new ResponseEntity<>(
                        ResDTO.builder()
                                .code(0)
                                .message("회원가입 성공")
                                .build(),
                        HttpStatus.OK);
        }

                @Transactional
                public HttpEntity<?> login(ReqLoginApiV1DTO reqLoginApiV1DTO) {

                        // 존재 여부 검사를 필수로 하기 위해 Optional로 감싸준다.
                        // 이메일로 DB에서 유저를 찾은 후
                        Optional<MemberEntity> memberEntityOptional = memberRepository.findByEmail(reqLoginApiV1DTO.getEmail());

                        // 유저가 존재하지 않으면 BadRequestException 발생
                        if (memberEntityOptional.isEmpty()) {
                                throw new BadRequestException("존재하지 않는 유저입니다.");
                        }

                        // 유저가 존재하면 memberEntity 추출 후
                        MemberEntity memberEntity = memberEntityOptional.get();

                        // passwordEncoder를 사용하여 패스워드가 일치하지 않는지 검사
                        if (!passwordEncoder.matches(reqLoginApiV1DTO.getPassword(), memberEntity.getPassword())) {
                                // 패스워드가 일치하지 않으면 BadRequestException 발생
                                throw new BadRequestException("패스워드가 일치하지 않습니다.");
                        }

                        // 패스워드가 일치하면 jwtProvider를 사용하여 accessToken, refreshToken 생성
                        String accessToken = jwtProvider.createToken(memberEntity, JwtTokenType.ACCESS_TOKEN);
                        String refreshToken = jwtProvider.createToken(memberEntity, JwtTokenType.REFRESH_TOKEN);

                        // accessToken, refreshToken 을 JwtToken 객체에 담아서 반환
                        return new ResponseEntity<>(
                                ResDTO.builder()
                                        .code(0)
                                        .message("로그인에 성공하였습니다.")
                                        .data(JwtToken.builder().accessToken(accessToken)
                                                .refreshToken(refreshToken).build())
                                        .build(),
                                HttpStatus.OK);

                }

        @Transactional
        public HttpEntity<?> reLogin(ReqReLoginApiV1DTO reqReLoginApiV1DTO) {

                // jwt 선언 (try-catch 문에서 선언 시 스코프 바깥에서 사용 불가)
                DecodedJWT decodedJwt = null;
                try {
                        // jwtProvider의 verify 함수를 사용하여 token 검증
                        decodedJwt = jwtProvider.verify(reqReLoginApiV1DTO.getRefreshToken());
                } catch (UnauthorizedException e) {
                        // refreshToken 자체가 만료되었거나, 문제가 있으면 UnauthorizedException 발생
                        throw new UnauthorizedException(e.getMessage());
                }

                // 검증 성공 시 토큰의 타입이 RefreshToken 인지 검사
                if (!decodedJwt.getClaim("token-type").asString().equals(JwtTokenType.REFRESH_TOKEN.name())) {
                        // 토큰 타입이 RefreshToken이 아니면 UnauthorizedException 발생
                        throw new UnauthorizedException("토큰 타입이 잘못되었습니다.");
                }

                // RefreshToken이며, 검증 성공 시 유저 정보 얻어오기
                String accessToken = jwtProvider.createToken(Long.parseLong(decodedJwt.getSubject()),
                        decodedJwt.getClaim("role").asString(), JwtTokenType.ACCESS_TOKEN);
                String refreshToken = jwtProvider.createToken(Long.parseLong(decodedJwt.getSubject()),
                        decodedJwt.getClaim("role").asString(), JwtTokenType.REFRESH_TOKEN);

                // accessToken, refreshToken 을 JwtToken 객체에 담아서 반환
                return new ResponseEntity<>(
                        ResDTO.builder()
                                .code(0)
                                .message("refreshToken 재발급 완료")
                                .data(JwtToken.builder()
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .build())
                                .build(),
                        HttpStatus.OK);

        }
}