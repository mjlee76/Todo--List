package com.hello.ToDoList.controller;

import com.hello.ToDoList.dto.ToDoCreateDto;
import com.hello.ToDoList.dto.ToDoResponseDto;
import com.hello.ToDoList.service.ToDoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ToDoController {

    private final ToDoService todoService;

    public ToDoController(ToDoService todoService) {
        this.todoService = todoService;
    }

    // 날짜별 할 일 조회
    @GetMapping("/todos")
    public String showTodos(@RequestParam(required = false)LocalDate date,
                            @AuthenticationPrincipal(expression = "username") String memberId,
                            Model model) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<ToDoResponseDto> todos = todoService.getTodosByDate(memberId, date);
        model.addAttribute("todos", todos);
        model.addAttribute("selectedDate", date);

        return "todo/todolist";
    }

    // 할 일 추가
    @PostMapping("/todos")
    public String add(@AuthenticationPrincipal(expression = "username") String memberId,
                      @ModelAttribute ToDoCreateDto todoCreateDto) {
        ToDoResponseDto todoResponseDto = todoService.addTodo(memberId, todoCreateDto);

        return "redirect:/todos?date=" + todoResponseDto.getToDoDate();
    }
}
