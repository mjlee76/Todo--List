package com.hello.ToDoList.repository;

import com.hello.ToDoList.entity.Member;
import com.hello.ToDoList.repository.member.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class MemoryMemberRepositoryTest {

    MemoryMemberRepository repository = new MemoryMemberRepository();

    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    public void save() {
        Member member = new Member();
        member.setId("test");
        member.setName("testUser");
        member.setPassword("test1234");
        member.setEmail("testUser@gmail.com");

        repository.save(member);

        //검증
        Member result = repository.findById(member.getId()).get();
        Assertions.assertThat(member).isEqualTo(result);

        repository.findAll().forEach(m -> {
            System.out.println("id=" + m.getId()
                    + ", name=" + m.getName()
                    + ", email=" + m.getEmail()
                    + ", password=******");
        });
    }
}
