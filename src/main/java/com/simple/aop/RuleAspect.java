package com.simple.aop;

import com.simple.annotation.RuleValidate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class RuleAspect {

    private int MIN_VALUE = 10;
    private int MAX_VALUE = 100;

    @Around("@annotation(ruleValidate)")
    public Object check(ProceedingJoinPoint proceedingJoinPoint, RuleValidate ruleValidate) throws Throwable {
        System.out.println("execute aspect start");
        Object[] s = proceedingJoinPoint.getArgs();
        int price = (int) Arrays.stream(s).filter(Integer.class::isInstance).findFirst().get();
        check(ruleValidate.rule(), price);
        return proceedingJoinPoint.proceed();
    }

    private void check(RuleValidate.Rule rule, int price) throws Exception {
        if (isRange(rule, price)) {
            throw new IllegalArgumentException("range is error");
        }
    }

    private boolean isRange(RuleValidate.Rule rule, int price) {
        switch (rule) {
            case MIN -> {
                return price > MIN_VALUE;
            }
            case MAX -> {
                return price < MAX_VALUE;
            }
            default -> {
                return false;
            }
        }
    }

}
