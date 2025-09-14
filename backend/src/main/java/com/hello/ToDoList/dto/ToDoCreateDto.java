package com.hello.ToDoList.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ToDoCreateDto {

    private String task;
    private LocalDate toDoDate;

//    public ToDoCreateDto(String task, LocalDateTime toDoDate) {
//        this.task = task;
//        this.toDoDate = toDoDate;
//    }


    public String getTask() {
        return task;
    }

    public LocalDate getToDoDate() {
        return toDoDate;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setToDoDate(LocalDate toDoDate) {
        this.toDoDate = toDoDate;
    }
}
