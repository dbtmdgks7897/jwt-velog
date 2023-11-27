package com.example.jwtvelog.config;

import com.example.jwtvelog.auth.jwt.JwtAuthorizationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {



    // DB 드라이버 클래스 이름 (h2 사용 시 security 충돌 해결 위해)
    @Value("${spring.datasource.driver-class-name}")
    private String springDatasourceDriverClassName;

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    // custom Security Filter Manager 적용
    public static class CustomSecurityFilterManager
            extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        private final JwtAuthorizationFilter jwtAuthorizationFilter;

        public CustomSecurityFilterManager(JwtAuthorizationFilter jwtAuthorizationFilter) {
            this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        }

        // jwt 필터를 UsernamePasswordAuthenticationFilter 전에 등록
        @Override
        public void configure(HttpSecurity http) {
            http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, MvcRequestMatcher.Builder mvc,
                                                   JwtAuthorizationFilter jwtAuthorizationFilter)
            throws Exception {

        // api 서버로 사용하기 때문에 csrf 해제 (jwt로 대체)
        httpSecurity.csrf(config -> config.disable());

        // 로그인 인증창이 뜨지 않게 비활성화
        httpSecurity.httpBasic(config -> config.disable());

        // form 로그인 해제
        httpSecurity.formLogin(config -> config.disable());

        // jSessionId 사용 거부
        httpSecurity.sessionManagement(config -> config
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 커스텀 필터 적용 (시큐리티 필터 교환)
        httpSecurity.apply(new CustomSecurityFilterManager(jwtAuthorizationFilter));

        // 인증, 권한 필터 설정
        httpSecurity.authorizeHttpRequests(config -> config
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers(
                        mvc.pattern("/"),
                        mvc.pattern("/auth/**")
                ).permitAll()
                .requestMatchers(mvc.pattern("/api/v1/auth/**")).permitAll()
                .anyRequest().authenticated());

        // DB 드라이버 클래스 이름이 h2일 경우, h2 관련 옵션 추가
        if (springDatasourceDriverClassName.equals("org.h2.Driver")) {
            // h2 관련 옵션
            httpSecurity.headers(config -> config.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()));
        }

        return httpSecurity.getOrBuild();
    }
}
