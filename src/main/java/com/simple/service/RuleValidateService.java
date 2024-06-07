package com.simple.service;


import com.simple.annotation.RuleValidate;
import org.springframework.stereotype.Service;

@Service
public class RuleValidateService implements RuleValidateInterface {

    @RuleValidate(rule = RuleValidate.Rule.MIN)
    public void addPrice(String name, int price) {
        System.out.println("execute addPrice");
    }

    @RuleValidate(rule = RuleValidate.Rule.MIN)
    public void updatePrice(String name, int price) {
        System.out.println("execute updatePrice");
    }
}
