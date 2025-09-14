package com.hello.ToDoList;

import com.hello.ToDoList.service.auth.CustomUserDetailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailService userDetailService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailService userDetailService,
                          PasswordEncoder passwordEncoder) {
        this.userDetailService = userDetailService;
        this.passwordEncoder = passwordEncoder;
    }

    /** DaoAuthenticationProvider (세션 + BCrypt) */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailService);
        p.setPasswordEncoder(passwordEncoder);
        return p;
    }

    /** CORS: 개발 중 React(5173) 허용 + 쿠키 허용 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return (HttpServletRequest req) -> {
            CorsConfiguration c = new CorsConfiguration();
            c.setAllowedOrigins(List.of("http://localhost:5173"));
            c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
            c.setAllowedHeaders(List.of("Content-Type","X-Requested-With","Accept","Origin"));
            c.setAllowCredentials(true); // JSESSIONID 쿠키 전달
            return c;
        };
    }

    /** API 전용 체인: /api/** 만 시큐리티 적용, 나머지는 통과 */
    @Bean
    public SecurityFilterChain api(HttpSecurity http) throws Exception {
        http
                // 이 체인은 /api/** 에만 적용됨 (웹 페이지/정적리소스는 시큐리티 미적용)
                .securityMatcher("/api/**")
                .cors(cors -> {}) // 위 CORS 설정 사용
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // 개발 편의: API는 CSRF 제외
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 세션 기반(JSESSIONID)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // 회원가입/로그인/로그아웃/상태조회
                        .anyRequest().authenticated()                // 그 외 /api/** 는 인증 필요
                )
                // 로그인: /api/auth/login (시큐리티가 처리) → JSON 응답
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler((req, res, auth) -> {
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"success\":true}");
                        })
                        .failureHandler((req, res, ex) -> {
                            res.setStatus(401);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"success\":false,\"message\":\"invalid credentials\"}");
                        })
                        .permitAll()
                )
                // 로그아웃: /api/auth/logout → JSON 응답
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((req, res, auth) -> {
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"success\":true}");
                        })
                        .permitAll()
                )
                // 인증/인가 실패를 JSON으로 (401/403)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> { // 401
                            res.setStatus(401);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"authenticated\":false}");
                        })
                        .accessDeniedHandler((req, res, e) -> { // 403
                            res.setStatus(403);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"success\":false,\"message\":\"forbidden\"}");
                        })
                )
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}