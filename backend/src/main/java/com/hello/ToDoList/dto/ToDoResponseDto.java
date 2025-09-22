package com.hello.ToDoList.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ToDoResponseDto {

    private final Long id;
    private final String task;
    private final boolean completed;
    private final LocalDate toDoDate;
}
