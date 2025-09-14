package com.hello.ToDoList;

import com.hello.ToDoList.service.auth.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailService userDetailService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailService userDetailService, PasswordEncoder passwordEncoder) {
        this.userDetailService = userDetailService;
        this.passwordEncoder = passwordEncoder;
    }

    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") //인텔리J 경고 끄기
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth //작성된 경로 외에는 로그인 필요
                    .requestMatchers("/", "/members/signup", "/members/createMemberForm", "/css/**", "/js/**", "/images/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(login -> login
                    .loginPage("/members/login")  //커스텀 로그인 페이지
                    .loginProcessingUrl("/login")  //POST /login을 시큐리티가 가로채서 인증처리
                    .defaultSuccessUrl("/todos", true)  //로그인 성공시 todolist로 이동
                    .failureUrl("/members/login?error=true") //
                    .permitAll()  //로그인 페이지 접근 자체는 누구나 가능
            )
            .logout(logout -> logout
                    .logoutUrl("/logout").logoutSuccessUrl("/")
                    .permitAll()) //POST /logout (CSRF 활성 시 GET은 차단됨)
            .authenticationProvider(authenticationProvider()); //아래에서 정의한 DaoAuthenticationProvider를 이 체인에 사용하도록 등록.
        return http.build();
    }

    //DAO 기반 인증 공급자를 등록
    //UserDetailService + PasswordEncoder를 이용해 아이디/비번 검증
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
