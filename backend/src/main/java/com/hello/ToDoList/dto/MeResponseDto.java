package com.hello.ToDoList.dto;

import java.time.LocalDateTime;

public class MeResponseDto {

    private String id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    //통계 필드
    private int totalTodos;
    private int completedTodos;
    private int todayTodos;
    private int completedToday;

    public MeResponseDto(String id, String name, String email, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getTotalTodos() {
        return totalTodos;
    }

    public void setTotalTodos(int totalTodos) {
        this.totalTodos = totalTodos;
    }

    public int getCompletedTodos() {
        return completedTodos;
    }

    public void setCompletedTodos(int completedTodos) {
        this.completedTodos = completedTodos;
    }

    public int getTodayTodos() {
        return todayTodos;
    }

    public void setTodayTodos(int todayTodos) {
        this.todayTodos = todayTodos;
    }

    public int getCompletedToday() {
        return completedToday;
    }

    public void setCompletedToday(int completedToday) {
        this.completedToday = completedToday;
    }

    // 정적 팩토리 메서드: 회원 정보 + 통계를 한 번에 조립
    public static MeResponseDto of(String id, String name, String email, LocalDateTime createdAt,
                                   java.util.Map<String, Integer> stats) {
        MeResponseDto dto = new MeResponseDto(id, name, email, createdAt);
        if (stats != null) {
            dto.setTotalTodos(stats.getOrDefault("totalTodos", 0));
            dto.setCompletedTodos(stats.getOrDefault("completedTodos", 0));
            dto.setTodayTodos(stats.getOrDefault("todayTodos", 0));
            dto.setCompletedToday(stats.getOrDefault("completedToday", 0));
        }
        return dto;
    }
}
