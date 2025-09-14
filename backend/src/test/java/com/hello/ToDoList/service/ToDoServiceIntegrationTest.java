package com.hello.ToDoList.service;

import com.hello.ToDoList.dto.ToDoCreateDto;
import com.hello.ToDoList.dto.ToDoResponseDto;
import com.hello.ToDoList.dto.TodoUpdateDto;
import com.hello.ToDoList.entity.ToDo;
import com.hello.ToDoList.repository.toDo.ToDoRepository;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional //DB롤백
public class ToDoServiceIntegrationTest {

    @Autowired ToDoService todoService;
    @Autowired ToDoRepository todoRepository;

    @Test
    void 할_일_추가() {
        //given
        ToDoCreateDto dto = new ToDoCreateDto();
        dto.setTask("Sample Task");
        dto.setToDoDate(LocalDate.parse("2025-09-04"));

        //10개 이하인지 확인
        todoRepository.countByMemberAndDate("testUser", LocalDate.parse("2025-09-04"));

        //when
        ToDoResponseDto resDto = todoService.addTodo("testUser", dto);

        //1)then: 반환 Dto랑 기댓값 검증
        assertNotNull(resDto);
        assertEquals("Sample Task", resDto.getTask());
        assertEquals(false, resDto.isCompleted());
        assertEquals(LocalDate.parse("2025-09-04"), resDto.getToDoDate());

        //2)then: DB랑 기대값 검증
        //갯수로 검증
        int count = todoRepository.countByMemberAndDate("testUser", LocalDate.parse("2025-09-04"));
        assertEquals(1, count);
        //값 직접 검증
        List<ToDo> reslist = todoRepository.findByMemberAndDate("testUser", LocalDate.parse("2025-09-04"));
        ToDo res = reslist.get(0);
        assertEquals("testUser", res.getMemberId());
        assertEquals("Sample Task", res.getTask());
        assertEquals(false, res.isCompleted());
        assertEquals(LocalDate.parse("2025-09-04"), res.getToDoDate());
    }

    @Test
    void 할_일_조회() {
        //given
        ToDoCreateDto dto = new ToDoCreateDto();
        dto.setTask("Study Spring Security");
        dto.setToDoDate(LocalDate.parse("2025-09-05"));
        todoService.addTodo("testUser", dto);

        //when
        List<ToDoResponseDto> reslist = todoService.getTodosByDate("testUser", LocalDate.parse("2025-09-05"));

        //then
        assertEquals(1, reslist.size());
        ToDoResponseDto res = reslist.get(0);
        assertEquals("Study Spring Security", res.getTask());
        assertEquals(false, res.isCompleted());
        assertEquals(LocalDate.parse("2025-09-05"), res.getToDoDate());
    }

    @Test
    void 상태_토글() {
        //given
        ToDoCreateDto dto = new ToDoCreateDto();
        dto.setTask("Study Spring MVC");
        dto.setToDoDate(LocalDate.parse("2025-09-06"));
        ToDoResponseDto saved = todoService.addTodo("testUser", dto);

        //when
        TodoUpdateDto updateDto = new TodoUpdateDto();
        updateDto.setId(saved.getId());
        boolean res = todoService.toggleCompleted("testUser", updateDto);

        //then
        assertEquals(true, res);

        List<ToDo> reslist = todoRepository.findByMemberAndDate("testUser", LocalDate.parse("2025-09-06"));
        assertEquals(1, reslist.size());
        assertEquals(true, reslist.get(0).isCompleted());
    }

    @Test
    void 할_일_삭제() {
        //given
        ToDoCreateDto dto = new ToDoCreateDto();
        dto.setTask("Task need to delete");
        dto.setToDoDate(LocalDate.parse("2025-09-07"));
        ToDoResponseDto saved = todoService.addTodo("testUser", dto);

        //when
        boolean deleted = todoService.deleteTodo("testUser", saved.getId());

        //then
        assertEquals(true, deleted);
        int count = todoRepository.countByMemberAndDate("testUser", LocalDate.parse("2025-09-07"));
        assertEquals(0, count);
    }

    @Test
    void 삭제_존재하지않는Id() {
        assertThrows(IllegalStateException.class,
                () -> todoService.deleteTodo("testUser", 99999L));
    }

    @Test
    void 할_일_추가_10개초과시_예외() {
        //given
        for (int i = 0; i < 10; i++) {
            ToDoCreateDto dto = new ToDoCreateDto();
            dto.setTask("Task " + i);
            dto.setToDoDate(LocalDate.parse("2025-09-08"));
            todoService.addTodo("testUser", dto);
        }

        //when & then
        ToDoCreateDto dto = new ToDoCreateDto();
        dto.setTask("Task 11");
        dto.setToDoDate(LocalDate.parse("2025-09-08"));

        assertThrows(IllegalStateException.class,
                () -> todoService.addTodo("testUser", dto));
    }
}
