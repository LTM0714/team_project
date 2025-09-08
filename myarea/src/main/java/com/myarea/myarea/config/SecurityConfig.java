package com.myarea.myarea.config;

import com.myarea.myarea.entity.UserRole;
import com.myarea.myarea.jwt.JwtAuthenticationFilter;
import com.myarea.myarea.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // @PreAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;  // 커스터마이징한 UserDetailService 필요

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // csrf 비활성화(JWT는 세션을 사용하지 않기 때문에)
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> {});

        // 세션을 Stateless 방식으로 설정(서버에서 세션을 저장하지 않음)
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 경로별 인가
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/login", "/api/users/signup", "/api/users/refresh",
                        "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole(UserRole.ADMIN.name())
                .requestMatchers("/api/user/**").hasAnyRole(UserRole.USER.name(), UserRole.ADMIN.name())
                .anyRequest().authenticated()
        );

        // JWT 인증 필터 등록
        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService),
                UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> res.sendError(401, "Unauthorized"))
                .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden"))
        );

        return http.build();
    }

    // 비밀번호 암호화에 사용
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
