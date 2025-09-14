package com.hello.ToDoList.service;

import com.hello.ToDoList.dto.MemberDto;
import com.hello.ToDoList.entity.Member;
import com.hello.ToDoList.repository.member.MemoryMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MemberServiceTest {

    MemberService memberService;
    MemoryMemberRepository repository;
    MemberDto memberDto;
    PasswordEncoder passwordEncoder;

    @BeforeEach
    public void beforeEach() {
        repository = new MemoryMemberRepository();
        memberService = new MemberService(repository, passwordEncoder);
    }

    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    void join() {

        //given
        MemberDto memberDto = new MemberDto();
        memberDto.setId("test");
        memberDto.setName("testUser");
        memberDto.setPassword("test1234");
        memberDto.setEmail("testUser@test.com");

        // stub encoding (단위테스트에선 encode가 호출되는지만 확인)
        passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class); //가짜 객체(Mock) 생성
        memberService = new MemberService(repository, passwordEncoder);
        when(passwordEncoder.encode("test1234")).thenReturn("ENC(test1234)"); //Mockito의 stubbing(스텁) 기능을 사용

        //when
        memberService.join(memberDto);

        //then
        Optional<Member> result = repository.findById("test");
        assertTrue(result.isPresent());
        assertEquals("test", result.get().getId());
    }

    @Test
    public void validateDuplicatedId() {

        //given
        MemberDto memberDto1 = new MemberDto();
        memberDto1.setId("DuplicatedId");
        memberDto1.setName("User1");
        memberDto1.setPassword("password1");
        memberDto1.setEmail("user1@example.com");

        MemberDto memberDto2 = new MemberDto();
        memberDto2.setId("DuplicatedId");
        memberDto2.setName("User2");
        memberDto2.setPassword("password2");
        memberDto2.setEmail("user2@example.com");

        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        memberService = new MemberService(repository, passwordEncoder);

        when(passwordEncoder.encode("password1")).thenReturn("ENC(password1)");
        when(passwordEncoder.encode("password2")).thenReturn("ENC(password2)");

        //when
        memberService.join(memberDto1);

        //then
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(memberDto2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 ID입니다.");
    }
}
