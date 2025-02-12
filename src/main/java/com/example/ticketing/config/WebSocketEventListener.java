package com.example.ticketing.config;

import com.example.ticketing.model.chat.UserPrincipal;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.chat.ChatPresenceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final ChatPresenceService chatPresenceService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    /**
     * websocket 연결 시 호출
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
            chatPresenceService.updateUserStatus(userId, "ONLINE");
            logger.info("User connected: {}", userId);
        }
    }

    /**
     * websocket 해제 시 호출
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        String token = accessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
            chatPresenceService.updateUserStatus(userId, "OFFLINE");
            chatPresenceService.clearActiveRoom(userId);
            logger.info("User disconnected: {}", userId);
        }
    }

}
