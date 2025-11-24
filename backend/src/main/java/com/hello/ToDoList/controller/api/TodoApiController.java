package com.hello.ToDoList.controller.api;

import com.hello.ToDoList.dto.ToDoCreateDto;
import com.hello.ToDoList.dto.ToDoResponseDto;
import com.hello.ToDoList.service.ToDoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
@Tag(name = "Todo API", description = "할 일 조회, 생성, 수정, 삭제 API")
public class TodoApiController {

    private final ToDoService todoService;

    @Autowired
    public TodoApiController(ToDoService todoService) {
        this.todoService = todoService;
    }

    @Operation(
            summary = "특정 날짜의 할 일 조회",
            description = "요청한 날짜에 해당하는 로그인 사용자의 할 일 목록을 반환합니다."
    )
    @GetMapping
    public ResponseEntity<List<ToDoResponseDto>> list(@Parameter(description = "조회할 날짜 (yyyy-MM-dd)") @RequestParam LocalDate date,
                                                      @Parameter(hidden = true, description = "현재 로그인한 회원의 인증 정보") Principal principal) {
        String memberId = principal.getName();
        List<ToDoResponseDto> todos = todoService.getTodosByDate(memberId, date);
        return ResponseEntity.ok(todos);
    }

    @Operation(
            summary = "새로운 할 일 등록",
            description = "RequestBody에 할 일 내용과 날짜를 담아 보내면, 해당 회원의 일정에 새로운 할 일을 생성합니다."
    )
    @PostMapping
    public ResponseEntity<ToDoResponseDto> add(
            @Parameter(description = "생성할 할 일 정보(내용, 날짜)를 담은 RequestBody")
            @Valid @RequestBody ToDoCreateDto todoCreateDto,
            @Parameter(hidden = true, description = "현재 로그인한 회원의 인증 정보")
            Principal principal
    ) {
        String memberId = principal.getName();
        ToDoResponseDto savedTodo = todoService.addTodo(memberId, todoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTodo);
    }

    @Operation(
            summary = "할 일의 상태 토글(완료, 미완료)",
            description = "경로에 전달될 할 일 ID를 기준으로, 해당 회원의 할 일 상태를 완료 <=> 미완료로 변경합니다."
    )
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggle(
            @Parameter(description = "상태를 변경할 할 일의 ID", example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true, description = "현재 로그인한 회원의 인증 정보")
            Principal principal
    ) {
        String memberId = principal.getName();
        todoService.toggleCompleted(memberId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "할 일 삭제",
            description = "경로에 전달된 할 일 ID를 기준으로 해당 할 일을 삭제합니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 할 일의 ID", example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true, description = "현재 로그인한 회원의 인증 정보")
            Principal principal
    ) {
        String memberId = principal.getName();
        todoService.deleteTodo(memberId, id);
        return ResponseEntity.noContent().build();
    }
}
