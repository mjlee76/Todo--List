package com.hello.ToDoList;

import com.hello.ToDoList.service.ToDoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class ToDoPerformanceTest {
    
    @Autowired ToDoService todoService;
    
    @Test
    void measureGetTodosByDate_100_times() {

        String memberId = "user1";
        LocalDate date = LocalDate.of(2025, 10, 20);

        int repeat = 100;
        long totalNs = 0;
        for (int i = 0; i < repeat; i++) {
            long start = System.nanoTime();
            todoService.getTodosByDate(memberId, date);
            long end = System.nanoTime();

            long duration = end - start;
            totalNs += duration;
        }
        // 1ms = 1,000,000ns
        double avgMs = totalNs / 1_000_000.0 / repeat;
        System.out.println("user1의 2025-10-20 할 일 " + repeat + "번 조회 평균 실행 시간: " + avgMs + "ms");
    }
}
