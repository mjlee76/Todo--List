package com.hello.ToDoList.repository.toDo;

import com.hello.ToDoList.entity.ToDo;

import java.time.LocalDate;
import java.util.List;

public interface ToDoRepository {

    //새로운 할 일 추가
    ToDo save(ToDo todo);

    //특정 회원 + 날짜 기준으로 할 일 조회
    List<ToDo> findByMemberAndDate(String memberId, LocalDate date);

    //회원 + 날짜별 할 일 개수 조회 (하루 10개 제한)
    int countByMemberAndDate(String memberId, LocalDate date);

    //완료 상태 토글
    boolean toggleCompleted(String memberId, Long todoId);

    //할 일 삭제
    int delete(String memberId, Long todoId);
}
