package com.simple.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestfulController {

    @Autowired
    private SimpMessagingTemplate template;

    @GetMapping("ws/test")
    private void test() {
        template.convertAndSend("/topic/greetings", new Greeting("test"));
    }
}
