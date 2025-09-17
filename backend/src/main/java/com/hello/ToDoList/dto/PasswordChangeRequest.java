package com.hello.ToDoList.dto;

public record PasswordChangeRequest(String currentPassword, String newPassword, String repeatPassword) {
    // 비밀번호 수정 DTO
}
