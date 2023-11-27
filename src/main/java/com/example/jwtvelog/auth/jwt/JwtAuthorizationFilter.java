package com.example.jwtvelog.auth.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.jwtvelog.auth.session.CustomUserByIdxService;
import com.example.jwtvelog.auth.session.CustomUserDetails;
import com.example.jwtvelog.common.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthorizationFilter extends GenericFilterBean {

    // idx를 이용해 CustomUserDetails를 가져오는 서비스
    private final CustomUserByIdxService customUserByIdxService;

    // 생성자
    @Autowired
    public JwtAuthorizationFilter(CustomUserByIdxService customUserByIdxService) {
        this.customUserByIdxService = customUserByIdxService;
    }

    // 필터 로직 구현
    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain
    )
            throws IOException, ServletException {
        // HttpServletRequest 객체로 서블릿 요청 객체를 캐스팅
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        // HttpServletResponse 객체로 서블릿 응답 객체를 캐스팅
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        // HTTP 요청의 헤더에서 JWT 토큰을 가져온다.
        // JwtProvider.HEADER는 토큰을 저장하는 데 사용되는 HTTP 헤더의 이름
        String prefixJwt = httpRequest.getHeader(JwtProvider.HEADER);

        // 토큰이 없으면 다음 필터로 넘긴다.
        if (prefixJwt == null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 토큰 접두사 "Bearer"을 제거한다.
        String jwt = prefixJwt.replace(JwtProvider.TOKEN_PREFIX, "");
        try {
            // jwtProvider 객체를 생성하고
            JwtProvider jwtProvider = new JwtProvider();

            // jwtProvider 객체의 verify 메서드를 사용해 토큰을 검증한다.
            DecodedJWT decodedJWT = jwtProvider.verify(jwt);

            // 토큰에서 idx를 가져온다. (우리가 idx로 설정 해놓은 주제, 즉 subject)
            Long idx = Long.parseLong(decodedJWT.getSubject());

            // idx를 이용해 CustomUserDetails를 가져온다.
            CustomUserDetails customUserDetails = customUserByIdxService.loadUserByIdx(idx);

            // 가져온 CustomUserDetails를 사용해
            // Authentication에 담길 UsernamePasswordAuthenticationToken 객체를 생성한다.
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    customUserDetails.getPassword(),
                    customUserDetails.getAuthorities());

            // SecurityContext에 Authentication 객체를 저장한다.
            // 이로써 Spring Security가 인증된 사용자라고 인식하고,
            // 컨트롤러에서 @AuthenticationPrincipal 어노테이션을 사용해 사용자 정보를 가져올 수 있다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UnauthorizedException e) {
            // json 형태로 401 응답
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태 코드 설정
            httpResponse.setContentType("application/json;charset=UTF-8"); // 컨텐츠 타입 설정

            // JSON으로 오류 메시지 생성
            Map<String, Object> data = new HashMap<>();
            data.put("code", -1);
            data.put("message", "인증 오류: " + e.getMessage());
            data.put("data", "Unauthorized");
            data.put("status", HttpServletResponse.SC_UNAUTHORIZED);

            // ObjectMapper를 사용해 Map을 JSON으로 변환
            String json = new ObjectMapper().writeValueAsString(data);

            // 응답에 JSON 쓰기
            httpResponse.getWriter().write(json);
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
            return;
        } finally {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}