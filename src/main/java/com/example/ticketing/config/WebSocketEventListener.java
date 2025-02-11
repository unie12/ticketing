package com.example.ticketing.config;

import com.example.ticketing.model.chat.UserPrincipal;
import com.example.ticketing.service.chat.ChatPresenceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final ChatPresenceService chatPresenceService;
    private final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    /**
     * websocket 연결 시 호출
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        if (user != null) {
            Long userId = ((UserPrincipal) user).getUserId();
            chatPresenceService.updateUserStatus(userId, "ONLINE"); // 사용자 상태 변경
            logger.info("User connected: {}", userId);
        }
    }

    /**
     * websocket 해제 시 호출
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = accessor.getUser();
        if (user != null) {
            Long userId = ((UserPrincipal) user).getUserId();
            chatPresenceService.updateUserStatus(userId, "OFFLINE"); // 사용자 선택 변경
            chatPresenceService.clearActiveRoom(userId); // 활성 채팅방 정보 제거
            logger.info("User disconnected: {}", userId);
        }
    }

}
