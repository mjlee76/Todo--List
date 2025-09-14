package com.hello.ToDoList.dto;

public class TodoUpdateDto {

    private Long id;
    private boolean completed;

//    public TodoUpdateDto(Long id, String task, boolean completed) {
//        this.id = id;
//        this.task = task;
//    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
