package com.hello.ToDoList.service;

import com.hello.ToDoList.dto.MeResponseDto;
import com.hello.ToDoList.entity.Member;
import com.hello.ToDoList.repository.member.MemberRepository;
import com.hello.ToDoList.repository.toDo.ToDoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hello.ToDoList.dto.ProfilePatchRequest;
import com.hello.ToDoList.dto.PasswordChangeRequest;

import java.util.Map;
import java.util.regex.Pattern;

@Service
@Transactional
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ToDoRepository todoRepository;

    public MyPageService(MemberRepository memberRepository, ToDoRepository todoRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.todoRepository = todoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 이메일 형식 검증
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // ====== 프로필 조회 ======
    @Transactional(readOnly = true)
    public MeResponseDto getMe(String id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));
        Map<String, Integer> stats = todoRepository.getStatistics(id);
        return MeResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .totalTodos(stats != null ? stats.getOrDefault("totalTodos", 0) : 0)
                .completedTodos(stats != null ? stats.getOrDefault("completedTodos", 0) : 0)
                .todayTodos(stats != null ? stats.getOrDefault("todayTodos", 0) : 0)
                .completedToday(stats != null ? stats.getOrDefault("completedToday", 0) : 0)
                .build();
    }

    // ====== 이름/이메일 변경 ======
    public void patchProfile(String id, ProfilePatchRequest req) {
        if (req == null) throw new IllegalArgumentException("요청이 비어있습니다.");

        // 현재 값 조회 (변경 감지/비교 용)
        Member meber = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

        // 이름 변경 (req.name() -> 사용자가 입력한 새 이름)
        if (req.name() != null) {
            String newName = req.name().trim();
            if (newName.isEmpty()) throw new IllegalArgumentException("이름을 입력하세요.");
            if (newName.length() > 21) throw new IllegalArgumentException("이름은 20자 이하여야 합니다.");
            if (!newName.equals(meber.getName())) {
                int updated = memberRepository.updateName(meber.getId(), newName);
                if (updated != 1) throw new IllegalStateException("이름 변경에 실패했습니다.");
            }
        }

        // 이메일 변경
        if (req.email() != null) {
            String newEmail = req.email().trim();
            if (newEmail.isEmpty()) throw new IllegalArgumentException("이메일을 입력하세요.");
            if (!EMAIL_REGEX.matcher(newEmail).matches()) throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
            if (!newEmail.equals(meber.getEmail())) {
                // 자기 자신 제외 중복 체크
                boolean isDuplicated = memberRepository.existsByEmailExcludingId(newEmail, meber.getId());
                if (isDuplicated == true) throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
                int updated = memberRepository.updateEmail(meber.getId(), newEmail);
                if (updated != 1) throw new IllegalStateException("이메일 변경에 실패했습니다.");
            }
        }
    }

    // ====== 비밀번호 수정 ======
    public void changePassword(String id, PasswordChangeRequest req) {
        if (req == null) throw new IllegalArgumentException("요청이 비어있습니다.");
        if (req.currentPassword().isBlank()) throw new IllegalArgumentException("현재 비밀번호를 입력하세요.");
        if (req.newPassword().isBlank() || req.repeatPassword().isBlank())
            throw new IllegalArgumentException("새 비밀번호와 확인을 입력하세요.");
        if (!req.newPassword().equals(req.repeatPassword()))
            throw new IllegalArgumentException("새 비밀번호가 서로 일치하지 않습니다.");
        if (req.newPassword().length() < 8)
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");

        Member meber = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

        if (!passwordEncoder.matches(req.currentPassword(), meber.getPassword()))
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        if (passwordEncoder.matches(req.newPassword(), meber.getPassword()))
            throw new IllegalArgumentException("기존 비밀번호와 동일하게 변경할 수 없습니다.");

        String encodedNewPassword = passwordEncoder.encode(req.newPassword());
        int updated = memberRepository.updatePassword(meber.getId(), encodedNewPassword);
        if (updated != 1) throw new IllegalStateException("비밀번호 변경에 실패했습니다.");
    }

}
