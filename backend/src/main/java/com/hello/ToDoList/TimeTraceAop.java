package com.hello.ToDoList;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeTraceAop {

    @Around("execution(* com.hello.ToDoList..*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        //System.out.println("START: " + joinPoint.toString());
        try {
            return joinPoint.proceed(); //inline 형식으로 변환
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("[APP] " + joinPoint.toString() + " 실행 시간: " + timeMs + "ms");
        }
    }

    @Around("execution(* com.hello.ToDoList.repository..*(..))")
    public Object measureRepositoryTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("[REPO] " + joinPoint.getSignature() + " 실행 시간: " + timeMs + "ms");
        }
    }
}
