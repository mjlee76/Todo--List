package com.hello.ToDoList.repository.member;

import com.hello.ToDoList.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(String id);
    List<Member> findAll();
}
