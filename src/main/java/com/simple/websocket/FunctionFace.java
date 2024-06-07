package com.simple.websocket;

@FunctionalInterface
public interface FunctionFace {

    void handle(String a, String b);

    default void handle(String a, String b, String c) {
        handle(a, b);
    }
}
