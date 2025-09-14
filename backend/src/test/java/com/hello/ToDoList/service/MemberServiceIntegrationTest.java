package com.hello.ToDoList.service;


import com.hello.ToDoList.dto.MemberDto;
import com.hello.ToDoList.entity.Member;
import com.hello.ToDoList.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Transactional
public class MemberServiceIntegrationTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    void join() {

        //given
        MemberDto memberDto = new MemberDto();
        memberDto.setId("test");
        memberDto.setName("testUser");
        memberDto.setPassword("test1234");
        memberDto.setEmail("testUser@test.com");

        //when
        memberService.join(memberDto);

        //then
        Optional<Member> result = memberRepository.findById("test");
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("test");
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

        //when
        memberService.join(memberDto1);

        //then
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(memberDto2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 ID입니다.");
    }
}
