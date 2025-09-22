package com.hello.ToDoList.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class MeResponseDto {

    private final String id;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;

    //통계 필드
    private final int totalTodos;
    private final int completedTodos;
    private final int todayTodos;
    private final int completedToday;
}

