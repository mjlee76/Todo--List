package com.hello.ToDoList.controller.api;

import com.hello.ToDoList.dto.ProfilePatchRequest;
import com.hello.ToDoList.dto.PasswordChangeRequest;
import com.hello.ToDoList.dto.MeResponseDto;
import com.hello.ToDoList.service.MyPageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/me")
public class MyPageApiController {

    private final MyPageService myPageService;

    public MyPageApiController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    // 프로필 조회
    @GetMapping
    public MeResponseDto getMe(@AuthenticationPrincipal(expression = "username") String id) {
        return myPageService.getMe(id);
    }

    // 이름/이메일 수정 (둘 중 바뀐 것만 보냄)
    @PatchMapping
    public ResponseEntity<MeResponseDto> patchProfile(@AuthenticationPrincipal(expression = "username") String id,
                                            @RequestBody ProfilePatchRequest req) {
        // 부분 수정 수행
        myPageService.patchProfile(id, req);
        // 최신 상태 재조회
        MeResponseDto me = myPageService.getMe(id);
        // 캐시 무효화/ETag 등 헤더 제어
        // 프로필/비밀번호 등 개인정보 보안상 캐시 무효와는 기본 설정
        return ResponseEntity
                .ok().header("Cache-Control", "no-store") // 캐시하지 말고 매번 서버에서 가져오기
                .body(me);
    }

    // 비밀번호 변경
    @PostMapping
    public ResponseEntity<Map<String, Object>> changePassword(@AuthenticationPrincipal(expression = "username") String id,
                                              @RequestBody PasswordChangeRequest req,
                                              HttpServletRequest request,
                                              HttpServletResponse response,
                                              Authentication authentication) {
        myPageService.changePassword(id, req);

        // ✅ 비번 변경 후 즉시 로그아웃 (세션 무효화 + 컨텍스트 제거 + 쿠키 정리)
        new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler()
                .logout(request, response, authentication);

        // 프론트가 이 플래그를 보고 /login 으로 보내면 됨
        return ResponseEntity.ok(Map.of("success", true, "relogin", true));
    }
}
