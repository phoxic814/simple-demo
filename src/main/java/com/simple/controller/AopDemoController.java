package com.simple.controller;

import com.simple.service.RuleValidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AopDemoController implements AopDemoControllerInterface {

    @Autowired
    private RuleValidateService ruleValidateService;

    @GetMapping("rule/add")
    public void add(@RequestParam Integer price) {
        ruleValidateService.addPrice("test", price);
    }

    @GetMapping("rule/update")
    public void update(@RequestParam Integer price) {
        ruleValidateService.updatePrice("test", price);
    }
}
