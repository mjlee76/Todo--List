package com.hello.ToDoList.controller.api;

import com.hello.ToDoList.dto.MemberDto;
import com.hello.ToDoList.service.MemberService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth API", description = "회원가입 및 로그인 상태 확인용 인증 API")
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
    @Operation(
            summary = "회원가입",
            description = "새로운 회원 정보를 JSON으로 전달하면, 중복 여부를 검증한 후 계정을 생성합니다."
    )
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @Parameter(description = "회원가입에 필요한 정보(id, 비밀번호, 이름, 이메일)를 담은 JSON 바디")
            @Valid @RequestBody MemberDto memberDto) {
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
    @Operation(
            summary = "로그인 상태 확인",
            description = "현재 요청에 포함된 세션(JSESSIONID) 기준으로 로그인 여부를 확인하고, 로그인된 경우 사용자 아이디를 반환합니다."
    )
    @GetMapping("/me")
    public ResponseEntity<?> me(
            @Parameter(hidden = true, description = "현재 인증된 사용자의 Principal 정보 (username은 로그인 아이디)")
            Principal principal) {
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