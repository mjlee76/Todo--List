package com.hello.ToDoList.repository.member;

import com.hello.ToDoList.entity.Member;
import org.springframework.stereotype.Repository;

import java.util.*;

//@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static Map<String, Member> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Member save(Member member) {
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public boolean existsByEmailExcludingId(String email, String id) {
        return false;
    }

    @Override
    public int updateName(String id, String name) {
        return 0;
    }

    @Override
    public int updateEmail(String id, String email) {
        return 0;
    }

    @Override
    public int updatePassword(String id, String password) {
        return 0;
    }

    public void clearStore() {
        store.clear();
    }
}
