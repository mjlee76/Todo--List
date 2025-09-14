package com.hello.ToDoList.controller.api;

import com.hello.ToDoList.dto.MemberDto;
import com.hello.ToDoList.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * Frontend auth.js와 연동되는 API 컨트롤러
 * - POST /api/auth/signup  : 회원가입(JSON)
 * - GET  /api/auth/me      : 로그인 상태 확인(JSON)
 * (로그인/로그아웃은 SecurityConfig의 formLogin/logout 핸들러가 처리)
 */
@RestController
@RequestMapping("/api/auth")
public class MemberApiController {

    private final MemberService memberService;

    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 회원가입 (auth.js: signup)
     * 요청: POST /api/auth/signup
     * Body(JSON): {"id":"user1","password":"pw","name":"홍길동","email":"a@b.c"}
     * 응답: 201 {"success":true}
     * 실패: 400 {"success":false,"message":"..."}
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody MemberDto memberDto) {
        try {
            memberService.join(memberDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "SIGNUP_FAILED"));
        }
    }

    /**
     * 로그인 상태 확인 (auth.js: fetchMe)
     * 요청: GET /api/auth/me (JSESSIONID 쿠키 필요)
     * 응답: 200 {authenticated:true, username:"..."} / 401 {authenticated:false}
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticated", false));
        }
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "username", principal.getName()
        ));
    }
}