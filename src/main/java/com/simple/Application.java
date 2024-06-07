package com.simple;

import com.simple.controller.AopDemoController;
import com.simple.controller.AopDemoControllerInterface;
import com.simple.service.RuleValidateService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
// 開啟aspectj
//@EnableLoadTimeWeaving
//@EnableSpringConfigured
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}