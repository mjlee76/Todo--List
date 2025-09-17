package com.hello.ToDoList.dto;

public class MeResponseDto {

    private String id;
    private String name;
    private String email;

    public MeResponseDto(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
