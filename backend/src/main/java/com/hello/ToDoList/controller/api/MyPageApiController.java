package com.hello.ToDoList.controller.api;

import com.hello.ToDoList.dto.ProfilePatchRequest;
import com.hello.ToDoList.dto.PasswordChangeRequest;
import com.hello.ToDoList.dto.MeResponseDto;
import com.hello.ToDoList.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/me")
@Tag(name = "MyPage API", description = "마이페이지(내 정보) 조회 및 수정 관련 API")
public class MyPageApiController {

    private final MyPageService myPageService;

    public MyPageApiController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    // 프로필 조회
    @Operation(
            summary = "내 프로필 조회",
            description = "현재 로그인한 사용자의 기본 프로필 정보를 조회합니다."
    )
    @GetMapping
    public MeResponseDto getMe(
            @Parameter(hidden = true, description = "현재 로그인한 회원의 아이디(username)")
            @AuthenticationPrincipal(expression = "username") String id
    ) {
        return myPageService.getMe(id);
    }

    // 이름/이메일 수정 (둘 중 바뀐 것만 보냄)
    @Operation(
            summary = "프로필 수정",
            description = "이름 또는 이메일 등 변경할 항목만 전달하면, 현재 로그인한 사용자의 프로필 정보를 부분 수정합니다."
    )
    @PatchMapping
    public ResponseEntity<MeResponseDto> patchProfile(
            @Parameter(hidden = true, description = "현재 로그인한 회원의 아이디(username)")
            @AuthenticationPrincipal(expression = "username") String id,
            @Parameter(description = "수정할 프로필 정보(이름, 이메일 등 변경된 필드만 포함)")
            @RequestBody ProfilePatchRequest req
    ) {
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
    @Operation(
            summary = "비밀번호 변경",
            description = "현재 로그인한 사용자의 비밀번호를 변경하고, 보안을 위해 즉시 로그아웃합니다."
    )
    @PostMapping
    public ResponseEntity<Map<String, Object>> changePassword(
            @Parameter(hidden = true, description = "현재 로그인한 회원의 아이디(username)")
            @AuthenticationPrincipal(expression = "username") String id,
            @Parameter(description = "기존 비밀번호와 새 비밀번호 정보를 담은 요청 바디")
            @RequestBody PasswordChangeRequest req,
            @Parameter(hidden = true, description = "내부적으로 사용하는 HttpServletRequest")
            HttpServletRequest request,
            @Parameter(hidden = true, description = "내부적으로 사용하는 HttpServletResponse")
            HttpServletResponse response,
            @Parameter(hidden = true, description = "현재 인증된 사용자 Authentication 객체")
            Authentication authentication) {
        myPageService.changePassword(id, req);

        // ✅ 비번 변경 후 즉시 로그아웃 (세션 무효화 + 컨텍스트 제거 + 쿠키 정리)
        new org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler()
                .logout(request, response, authentication);

        // 프론트가 이 플래그를 보고 /login 으로 보내면 됨
        return ResponseEntity.ok(Map.of("success", true, "relogin", true));
    }
}
