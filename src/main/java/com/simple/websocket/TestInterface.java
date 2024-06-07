package com.simple.websocket;

public interface TestInterface extends FunctionFace {

    default void handle(String a, String b, String c) {
        handle(a, b);
    }
}
