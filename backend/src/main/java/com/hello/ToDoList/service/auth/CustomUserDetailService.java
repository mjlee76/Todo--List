package com.hello.ToDoList.service.auth;

import com.hello.ToDoList.entity.Member;
import com.hello.ToDoList.repository.member.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("No such user: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(member.getId())
                .password(member.getPassword())
                .roles("USER")
                .build();
    }
}
