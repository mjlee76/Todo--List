package com.hello.ToDoList.dto;

public record ProfilePatchRequest(String name, String email) {
    // 이름/이메일 부분 수정 DTO (null 이면 변경 안 함)
}
