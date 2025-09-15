package com.hello.ToDoList.repository.member;

import com.hello.ToDoList.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(String id);
    List<Member> findAll();
    Optional<Member> findByEmail(String email);

    boolean existsByEmailExcludingId(String email, String id);
    int updateName(String id, String name);
    int updateEmail(String id, String email);
    int updatePassword(String id, String password);
}
