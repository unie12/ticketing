package com.example.ticketing.config;

import com.example.ticketing.model.chat.UserPrincipal;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.chat.ChatPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker // websocket 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 메시지 브로커 설정
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // topic -> 일대다 메시징을 위한 topic 기반 메시지 브로딩캐스팅
        // queue -> 일대일 메시징을 위한 큐 기반 메시지 전달
        // heartbeat -> 클라이언트-서버 간 연결 상태 확인 (10초)
        config.enableSimpleBroker("/topic", "/queue")
                .setTaskScheduler(heartBeatScheduler())
                .setHeartbeatValue(new long[] {10000, 10000});

        config.setApplicationDestinationPrefixes("/app"); // 클라이언트 메시지 전송 경로
        config.setUserDestinationPrefix("/user"); // 특정 사용자에게 메시지 보낼 때 사용할 prefix
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // WebSocket 엔드포인트
    }

    /**
     * Websocket 전송 관련 설정
     */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration
                .setSendTimeLimit(15 * 1000) // 메시지 전송 제한 시간 15초
                .setSendBufferSizeLimit(512 * 1024) // 버퍼 크기 512KB
                .setMessageSizeLimit(128 * 1024); // 메시지 크기 128KB
    }

    /**
     * heartbeat 처리를 위한 스케줄러 설정
     */
    @Bean
    public ThreadPoolTaskScheduler heartBeatScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1); // 단일 스레드
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        return scheduler;
    }

    // 클라이언트 연결 시 jwt 토큰 검증 및 사용자 정보 설정
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//
//                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                    String token = accessor.getFirstNativeHeader("Authorization");
//                    if (token != null && token.startsWith("Bearer ")) {
//                        String jwtToken = token.substring(7);
//                        Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
//                        accessor.setUser(new UserPrincipal(userId));
//                    }
//                }
//
//                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
//                    String destination = accessor.getDestination();
//                    if (destination != null && destination.startsWith("/topic/chat/")) {
//                        Long roomId = extractRoomId(destination);
//                        Long userId = ((UserPrincipal) accessor.getUser()).getUserId();
//                        presenceService.enterChatRoom(userId, roomId);
//                    }
//                } else if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand())) {
//                    String destination = accessor.getDestination();
//                    if (destination != null && destination.startsWith("/topic/chat/")) {
//                        Long roomId = extractRoomId(destination);
//                        Long userId = ((UserPrincipal) accessor.getUser()).getUserId();
//                        presenceService.leaveChatRoom(userId, roomId);
//                    }
//                }
//                return message;
//            }
//        });
//    }
//
//    private Long extractRoomId(String destination) {
//        // /topic/chat/123
//        return Long.parseLong(destination.split("/")[3]);
//    }
}
