package com.hello.ToDoList.controller.api;

import com.hello.ToDoList.service.MyPageService.ProfilePatchRequest;
import com.hello.ToDoList.service.MyPageService.PasswordChangeRequest;
import com.hello.ToDoList.dto.MeResponseDto;
import com.hello.ToDoList.service.MyPageService;
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
    public Map<String, Object> patchProfile(@AuthenticationPrincipal(expression = "username") String id,
                                            @RequestBody ProfilePatchRequest req) {
        myPageService.patchProfile(id, req);
        return Map.of("success", true);
    }

    // 비밀번호 변경
    @PostMapping
    public Map<String, Object> changePassword(@AuthenticationPrincipal(expression = "username") String id,
                                              @RequestBody PasswordChangeRequest req) {
        myPageService.changePassword(id, req);
        return Map.of("success", true);
    }
}
