package com.simple.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodAspect {

    @Pointcut("execution(* com.simple.controller.*.*(..))")
    public void controller() {
    }

    @Around("controller()")
    public Object aaa(ProceedingJoinPoint point) throws Throwable {
        return point.proceed();
    }
}
