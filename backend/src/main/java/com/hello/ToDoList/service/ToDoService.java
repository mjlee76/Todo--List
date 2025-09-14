package com.hello.ToDoList.service;

import com.hello.ToDoList.dto.ToDoCreateDto;
import com.hello.ToDoList.dto.ToDoResponseDto;
import com.hello.ToDoList.dto.ToDoResponseDto;
import com.hello.ToDoList.dto.TodoUpdateDto;
import com.hello.ToDoList.entity.ToDo;
import com.hello.ToDoList.repository.toDo.ToDoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ToDoService {

    private final ToDoRepository toDoRepository;

    public ToDoService(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    //할 일 추가
    public ToDoResponseDto addTodo(String memberId, ToDoCreateDto toDoCreateDto) {
        LocalDate todoDate = toDoCreateDto.getToDoDate();
        if(toDoRepository.countByMemberAndDate(memberId, todoDate) >= 10) {
            throw new IllegalStateException("하루에 최대 10개의 할 일만 등록할 수 있습니다.");
        }
        //엔티티에 값 세팅
        ToDo todo = new ToDo();
        todo.setMemberId(memberId);
        todo.setTask(toDoCreateDto.getTask());
        todo.setCompleted(false);
        todo.setToDoDate(toDoCreateDto.getToDoDate());
        //저장
        ToDo saved = toDoRepository.save(todo);
        //응답 DTO로 반환
        return new ToDoResponseDto(
                saved.getId(),
                saved.getTask(),
                saved.isCompleted(),
                saved.getToDoDate()
        );
    }

    //할 일 조회
    public List<ToDoResponseDto> getTodosByDate(String memberId, LocalDate date) {
        //조회한 내용 DTO로 변환
        return toDoRepository.findByMemberAndDate(memberId, date).stream()
                .map(todo -> new ToDoResponseDto(
                        todo.getId(),
                        todo.getTask(),
                        todo.isCompleted(),
                        todo.getToDoDate()
                ))
                .toList();
    }

    //할 일 상태 토글
    public boolean toggleCompleted(String memberId, TodoUpdateDto todoUpdateDto) {
        // repository에서 토글 실행
        boolean updated = toDoRepository.toggleCompleted(memberId, todoUpdateDto.getId());
        if(!updated) {
            throw new IllegalStateException("해당 할 일이 존재하지 않거나 접근 권한이 없습니다.");
        }
        return true;
    }

    //할 일 삭제
    public boolean deleteTodo(String memberId, Long id) {
        int delete = toDoRepository.delete(memberId, id);
        if(delete == 0) {
            throw new IllegalStateException("해당 할 일이 존재하지 않거나 접근 권한이 없습니다.");
        }
        return true;
    }
}
