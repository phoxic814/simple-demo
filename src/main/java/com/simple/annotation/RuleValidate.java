package com.simple.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RuleValidate {

    Rule rule() default Rule.NONE;

    Rule[] rules() default {};

    enum Rule {

        NONE(-1),
        MIN(0),
        MAX(1),
        ;

        int ruleType;

        Rule(int i) {
        }
    }
}
