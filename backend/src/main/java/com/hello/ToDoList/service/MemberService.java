package com.hello.ToDoList.service;

import com.hello.ToDoList.dto.MemberDto;
import com.hello.ToDoList.entity.Member;
import com.hello.ToDoList.repository.member.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    //DI(의존성 주입)
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        System.out.println("repository name: " + memberRepository.getClass().getName());
    }

    //회원가입
    public Member join(MemberDto memberDto) {
        validateDuplicatedId(memberDto.getId());

        //회원 객체 생성
        Member member = new Member();
        //Password 암호화
        String encodedPassword = passwordEncoder.encode(memberDto.getPassword());
        //Dto -> Entity 매핑
        member.setId(memberDto.getId());
        member.setName(memberDto.getName());
        member.setPassword(encodedPassword);
        member.setEmail(memberDto.getEmail());

        return memberRepository.save(member);
    }

    //ID 중복 검사
    public void validateDuplicatedId(String id) {
        if (memberRepository.findById(id).isPresent()) {
            throw new IllegalStateException("이미 존재하는 ID입니다.");
        }
    }
}
