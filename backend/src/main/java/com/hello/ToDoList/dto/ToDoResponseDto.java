package com.hello.ToDoList.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ToDoResponseDto {

    private Long id;
    private String task;
    private boolean completed;
    private LocalDate toDoDate;

    public ToDoResponseDto(Long id, String task, boolean completed, LocalDate toDoDate) {
        this.id = id;
        this.task = task;
        this.completed = completed;
        this.toDoDate = toDoDate;
    }

    public Long getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDate getToDoDate() {
        return toDoDate;
    }
}
