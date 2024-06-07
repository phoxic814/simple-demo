package com.simple.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket").setAllowedOriginPatterns("*");
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
////        registration.interceptors(new ChannelInterceptor() {
////            @Override
////            public Message<?> preSend(Message<?> message, MessageChannel channel) {
////                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
////                if (Objects.isNull(accessor) || Objects.isNull(accessor.getCommand()))
////                    return message;
////
////                // 若要取header資料，可使用StompHeaderAccessor.getNativeHeader
////                handlerChecker.check(accessor);
////                return message;
////            }
////        });
//        registration.taskExecutor().corePoolSize(48);
//    }

}
