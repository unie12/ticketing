package com.example.ticketing.service.chat;

import com.example.ticketing.model.chat.PresenceChangeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatPresenceService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String USER_STATUS_KEY = "chat:user:status:"; // 사용자 전역 상태
    private static final String USER_ACTIVE_ROOM_KEY = "chat:user:active:"; // 현재 활성화된 채팅방
    private static final String ROOM_UNREAD_KEY = "chat:room:unread:"; // 채팅방별 안읽은 메시지
    private static final String USER_ROOMS_KEY = "chat:user:rooms:"; // 사용자가 참여한 채팅방 목록

    private static final int PRESENCE_TIMEOUT = 30;

    /**
     * 사용자 상태 업데이트 (전역)
     */
    public void updateUserStatus(Long userId, String status) {
        String statusKey = USER_STATUS_KEY + userId;
        redisTemplate.opsForValue().set(statusKey, status, PRESENCE_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * 채팅방 활성화 (현재 보고 있는 채팅방)
     */
    public void setActiveRoom(Long userId, Long roomId) {
        String activeRoomKey = USER_ACTIVE_ROOM_KEY + userId;
        redisTemplate.opsForValue().set(activeRoomKey, roomId.toString());

        cleanUnreadCount(userId, roomId);
        notifyRoomPresenceChange(roomId, userId, "ACTIVE");
    }

    public void clearActiveRoom(Long userId) {
        String activeRoomKey = USER_ACTIVE_ROOM_KEY + userId;
        String currentRoomId = (String) redisTemplate.opsForValue().get(activeRoomKey);

        if (currentRoomId != null) {
            notifyRoomPresenceChange(Long.parseLong(currentRoomId), userId, "INACTIVE");
            redisTemplate.delete(activeRoomKey);
        }
    }

    /**
     * 현재 활성화된 채팅방 확인
     */
    public Long getActiveRoom(Long userId) {
        String activeRoomKey = USER_ACTIVE_ROOM_KEY + userId;
        String roomId = (String) redisTemplate.opsForValue().get(activeRoomKey);
        return roomId != null ? Long.parseLong(roomId) : null;
    }

    /**
     * 안읽은 메시지 수 증가
     */
    public void incrementUnreadCount(Long roomId, Long userId) {
        String unreadKey = ROOM_UNREAD_KEY + roomId + ":" + userId;
        redisTemplate.opsForValue().increment(unreadKey);
    }

    /**
     * 안읽은 메시지 초기화
     */
    public void cleanUnreadCount(Long userId, Long roomId) {
        String unreadKey = ROOM_UNREAD_KEY + roomId + ":";
        redisTemplate.delete(unreadKey);
    }

    private void notifyRoomPresenceChange(Long roomId, Long userId, String status) {
        messagingTemplate.convertAndSend(
                "/topic/chat/" + roomId + "/presence",
                new PresenceChangeDTO(userId, status)
        );
    }

//
//
//    /**
//     * 사용자 채팅방 입장
//     */
//    public void enterChatRoom(Long userId, Long roomId) {
//        String roomKey = ROOM_PRESENCE_KEY + roomId;
//        String userKey = USER_PRESENCE_KEY + userId;
//
//        redisTemplate.opsForHash().put(roomKey, userKey.toString(), "ACTIVE");
//        redisTemplate.opsForHash().put(userKey, "currentRoom", roomId.toString());
//
//        redisTemplate.expire(roomKey, PRESENCE_TIMEOUT, TimeUnit.SECONDS);
//        redisTemplate.expire(userKey, PRESENCE_TIMEOUT, TimeUnit.SECONDS);
//    }
//
//    /**
//     * 사용자 채팅방 퇴장
//     */
//    public void leaveChatRoom(Long userId, Long roomId) {
//        String roomKey = ROOM_PRESENCE_KEY + roomId;
//        String userKey = USER_PRESENCE_KEY + userId;
//
//        redisTemplate.opsForHash().delete(roomKey, userKey.toString());
//        redisTemplate.opsForHash().delete(userKey, "currentRoom");
//    }
//
//    /**
//     * 사용자 활성 상태 갱신 (heartbeat)
//     */
//    public void updateUserActivity(Long userId) {
//        String userKey = USER_PRESENCE_KEY + userId;
//        String currentRoom = (String) redisTemplate.opsForHash().get(userKey, "currentRoom");
//
//        if (currentRoom != null) {
//            String roomKey = ROOM_PRESENCE_KEY + currentRoom;
//            redisTemplate.opsForHash().put(roomKey, userKey.toString(), "ACTIVE");
//            redisTemplate.expire(roomKey, PRESENCE_TIMEOUT, TimeUnit.SECONDS);
//        }
//
//        redisTemplate.expire(userKey, PRESENCE_TIMEOUT, TimeUnit.SECONDS);
//    }
//
//    /**
//     * 채팅방의 활성 사용자 목록 조회
//     */
//    public Set<Long> getActiveUsersInRoom(Long roomId) {
//        String roomKey = ROOM_PRESENCE_KEY + roomId;
//        Map<Object, Object> entries = redisTemplate.opsForHash().entries(roomKey);
//
//        return entries.entrySet().stream()
//                .filter(e -> "ACTIVE".equals(e.getValue()))
//                .map(e -> Long.parseLong(e.getKey().toString()))
//                .collect(Collectors.toSet());
//    }
}
