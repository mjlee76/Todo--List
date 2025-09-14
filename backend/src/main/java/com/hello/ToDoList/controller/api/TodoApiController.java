package com.hello.ToDoList.controller.api;

import com.hello.ToDoList.dto.ToDoCreateDto;
import com.hello.ToDoList.dto.ToDoResponseDto;
import com.hello.ToDoList.service.ToDoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoApiController {

    private final ToDoService todoService;

    public TodoApiController(ToDoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<ToDoResponseDto>> list(@RequestParam LocalDate date,
                                                      Principal principal) {
        String memberId = principal.getName();
        List<ToDoResponseDto> todos = todoService.getTodosByDate(memberId, date);
        return ResponseEntity.ok(todos);
    }

    @PostMapping
    public ResponseEntity<ToDoResponseDto> add(@Valid @RequestBody ToDoCreateDto todoCreateDto,
                                               Principal principal) {
        String memberId = principal.getName();
        ToDoResponseDto savedTodo = todoService.addTodo(memberId, todoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTodo);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggle(@PathVariable Long id,
                                       Principal principal) {
        String memberId = principal.getName();
        todoService.toggleCompleted(memberId, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       Principal principal) {
        String memberId = principal.getName();
        todoService.deleteTodo(memberId, id);
        return ResponseEntity.noContent().build();
    }
}
