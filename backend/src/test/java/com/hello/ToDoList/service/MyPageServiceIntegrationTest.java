package com.hello.ToDoList.service;

import com.hello.ToDoList.dto.MeResponseDto;
import com.hello.ToDoList.dto.PasswordChangeRequest;
import com.hello.ToDoList.dto.ProfilePatchRequest;
import com.hello.ToDoList.entity.Member;
import com.hello.ToDoList.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MyPageServiceIntegrationTest {

    @Autowired MyPageService myPageService;
    @Autowired MemberRepository memberRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void 프로필_조회() {
        //given
        Member member = new Member();
        member.setId("user_profile_1");
        member.setName("testUser");
        member.setEmail("testUser@example.com");
        member.setPassword("password1234");
        memberRepository.save(member);

        //when
        MeResponseDto dto = myPageService.getMe(member.getId());

        //then
        assertEquals(member.getId(), dto.getId());
        assertEquals("testUser", dto.getName());
        assertEquals("testUser@example.com", dto.getEmail());
    }

    private Member saveMember(String id, String name, String email, String password) {
        Member m = new Member();
        m.setId(id);
        m.setName(name);
        m.setEmail(email);
        m.setPassword(passwordEncoder.encode(password));
        memberRepository.save(m);
        return m;
    }

    @Test
    void 이름_성공_변경됨() {
        // given
        saveMember("me", "oldName", "me@example.com", "password");

        // when
        myPageService.patchProfile("me", new ProfilePatchRequest("newName", null));

        // then
        MeResponseDto dto = myPageService.getMe("me");
        assertEquals("newName", dto.getName());
        assertEquals("me@example.com", dto.getEmail());
    }

    @Test
    void 이메일_성공_변경됨() {
        // given
        saveMember("me", "name", "old@example.com", "password");

        // when
        myPageService.patchProfile("me", new ProfilePatchRequest(null, "new@example.com"));

        // then
        MeResponseDto dto = myPageService.getMe("me");
        assertEquals("name", dto.getName());
        assertEquals("new@example.com", dto.getEmail());
    }

    @Test
    void 이름_실패_공백() {
        // given
        saveMember("me", "oldName", "me@example.com", "password");

        // when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> myPageService.patchProfile("me", new ProfilePatchRequest("   ", null)));
        assertTrue(ex.getMessage().contains("이름을 입력하세요."));
    }

    @Test
    void 이름_실패_최대길이초과() {
        // given
        saveMember("me", "oldName", "me@example.com", "password");
        String longName = "a".repeat(22); // 21 초과 → 예외

        // when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> myPageService.patchProfile("me", new ProfilePatchRequest(longName, null)));
        assertTrue(ex.getMessage().contains("이름은 20자 이하여야"));
    }

    @Test
    void 이메일_실패_형식오류() {
        // given
        saveMember("me", "name", "me@example.com", "password");

        // when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> myPageService.patchProfile("me", new ProfilePatchRequest(null, "not-an-email")));
        assertTrue(ex.getMessage().contains("올바른 이메일 형식"));
    }

    @Test
    void 이메일_실패_중복() {
        // given
        saveMember("userA", "A", "a@example.com", "password");
        saveMember("userB", "B", "b@example.com", "password");

        // when & then: userB가 userA의 이메일로 변경 시도 → 중복 예외
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> myPageService.patchProfile("userB", new ProfilePatchRequest(null, "a@example.com")));
        assertTrue(ex.getMessage().contains("이미 사용중인 이메일"));
    }

    @Test
    void 둘다_성공_동시변경() {
        // given
        saveMember("me", "oldName", "old@example.com", "password");

        // when
        myPageService.patchProfile("me", new ProfilePatchRequest("newName", "new@example.com"));

        // then
        MeResponseDto dto = myPageService.getMe("me");
        assertEquals("newName", dto.getName());
        assertEquals("new@example.com", dto.getEmail());
    }

    @Test
    void 변경없음_업데이트미실행() {
        // given
        saveMember("me", "same", "same@example.com", "password");

        // when: 동일 값으로 patch
        myPageService.patchProfile("me", new ProfilePatchRequest("same", "same@example.com"));

        // then: 그대로 유지
        MeResponseDto dto = myPageService.getMe("me");
        assertEquals("same", dto.getName());
        assertEquals("same@example.com", dto.getEmail());
    }

    @Test
    void 비밀번호_성공_변경됨() {
        //given
        saveMember("pw_user_id",
                "pw_user_name",
                "pwUser@example.com",
                "oldPassword");
        PasswordChangeRequest req = new PasswordChangeRequest(
                "oldPassword",
                "newPassword",
                "newPassword"
                );
        //when
        myPageService.changePassword("pw_user_id", req);
        //then
        Member updatedMember = memberRepository.findById("pw_user_id").orElseThrow();
        assertTrue(passwordEncoder.matches("newPassword", updatedMember.getPassword()));
        assertFalse(passwordEncoder.matches("oldPassword", updatedMember.getPassword()));
    }

    @Test
    void 비밀번호_실패_현재비밀번호_불일치() {
        //given
        saveMember("pw_user_id",
                "pw_user_name",
                "pwUser@example.com",
                "oldPassword");
        PasswordChangeRequest req = new PasswordChangeRequest(
                "wrongOldPassword",
                "newPassword",
                "newPassword"
        );
        //when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> myPageService.changePassword("pw_user_id", req));
        assertTrue(ex.getMessage().contains("현재 비밀번호가 일치하지 않습니다."));
    }

    @Test
    void 비밀번호_실패_새비밀번호_불일치() {
        //given
        saveMember("pw_user_id",
                "pw_user_name",
                "pwUser@example.com",
                "oldPassword");
        PasswordChangeRequest req = new PasswordChangeRequest(
                "oldPassword",
                "newPassword",
                "NewPassword"
        );
        //when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> myPageService.changePassword("pw_user_id", req));
        assertTrue(ex.getMessage().contains("새 비밀번호가 서로 일치하지 않습니다."));
    }

    @Test
    void 비밀번호_실패_기존과동일() {//given
        saveMember("pw_user_id",
                "pw_user_name",
                "pwUser@example.com",
                "oldPassword");
        PasswordChangeRequest req = new PasswordChangeRequest(
                "oldPassword",
                "oldPassword",
                "oldPassword"
        );
        //when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> myPageService.changePassword("pw_user_id", req));
        assertTrue(ex.getMessage().contains("기존 비밀번호와 동일하게 변경할 수 없습니다."));
    }

    @Test
    void 비밀번호_실패_길이미달() {
        saveMember("pw_user_id",
                "pw_user_name",
                "pwUser@example.com",
                "oldPassword");
        PasswordChangeRequest req = new PasswordChangeRequest(
                "oldPassword",
                "newPass",
                "newPass"
        );
        //when & then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> myPageService.changePassword("pw_user_id", req));
        assertTrue(ex.getMessage().contains("비밀번호는 8자 이상이어야 합니다."));
    }

    @Test
    void 비밀번호_실패_입력공백() {
        //given - case1
        saveMember("pw_user_id1",
                "pw_user_name1",
                "pwUser1@example.com",
                "oldPassword1");
        PasswordChangeRequest req1 = new PasswordChangeRequest(
                " ",
                "newPass",
                "newPass"
        );
        //when & then - case1
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> myPageService.changePassword("pw_user_id1", req1));
        assertTrue(ex1.getMessage().contains("현재 비밀번호를 입력하세요."));

        //given - case2
        saveMember("pw_user_id2",
                "pw_user_name2",
                "pwUser2@example.com",
                "oldPassword2");
        PasswordChangeRequest req2 = new PasswordChangeRequest(
                "oldPassword2",
                " ",
                " "
        );
        //when & then - case1
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> myPageService.changePassword("pw_user_id2", req2));
        assertTrue(ex2.getMessage().contains("새 비밀번호와 확인을 입력하세요."));
    }

    @Test
    void 비밀번호_실패_존재하지않는사용자() {
        // given
        PasswordChangeRequest req = new PasswordChangeRequest(
                "oldPassword",
                "NewPassword1",
                "NewPassword1"
        );

        // when & then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> myPageService.changePassword("no_such_user", req));
        assertTrue(ex.getMessage().contains("회원이 존재하지 않습니다."));
    }
}
