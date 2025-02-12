package com.example.ticketing.controller.chat;

import com.example.ticketing.model.chat.ChatMessageEvent;
import com.example.ticketing.model.chat.ChatMessageResponseDTO;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.chat.ChatMessageProducer;
import com.example.ticketing.service.chat.ChatPresenceService;
import com.example.ticketing.service.chat.ChatRoomService;
import com.example.ticketing.service.chat.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatPresenceService presenceService;

    private final ChatMessageProducer chatMessageProducer;

    @MessageMapping("/{roomId}/send")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessageEvent sendMessage(
            @DestinationVariable Long roomId,
            @RequestBody String messageContent,
            @Header("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
        String username = jwtTokenProvider.getUsernameFromToken(jwtToken);

        ChatMessageEvent event = ChatMessageEvent.builder()
                .roomId(roomId)
                .senderId(userId)
                .senderName(username)
                .content(messageContent)
                .timestamp(LocalDateTime.now())
                .build();

        chatMessageProducer.sendChatMessage(event);
        return event;
    }

    @MessageMapping("/enter/{roomId}")
    public void enterChatRoom(
            @DestinationVariable Long roomId,
            @Header("Authorization") String token
    ) {
        String jwtToken = token.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
        presenceService.setActiveRoom(userId, roomId);
    }

    @MessageMapping("/leave/{roomId}")
    public void leaveChatRoom(
            @DestinationVariable Long roomId,
            @Header("Authorization") String token
    ) {
        String jwtToken = token.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
        presenceService.clearActiveRoom(userId);
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<String> joinChatRoom(@PathVariable Long roomId, @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String username = chatRoomService.joinChatRoom(roomId, userId);
        return ResponseEntity.ok(username + "님이 채팅방에 참여했습니다.");
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageEvent>> getMessages(
            @PathVariable Long roomId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(messageService.getMessages(roomId, page, size));
    }


//    @DeleteMapping("/{roomId}/leave")
//    public ResponseEntity<String> leaveChatRoom(
//            @PathVariable Long roomId,
//            @RequestHeader("Authorization") String token
//    ) {
//        Long userId = jwtTokenProvider.getUserIdFromToken(token);
//        chatRoomService.leaveChatRoom(roomId, userId);
//        return ResponseEntity.ok("채팅방을 성공적으로 나갔습니다");
//    }

//    @GetMapping("/{roomId}/unread-count")
//    public ResponseEntity<Long> countUnreadMessages(
//            @PathVariable Long roomId,
//            @RequestHeader("Authorization") String token) {
//        String jwtToken = token.replace("Bearer ", "");
//        Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
//        return ResponseEntity.ok(chatRoomService.countUnreadMessages(roomId, userId));
//    }
//
//    @PostMapping("/{roomId}/read")
//    public ResponseEntity<String> markAsRead(
//            @PathVariable Long roomId,
//            @RequestHeader("Authorization") String token
//    ) {
//        Long userId = jwtTokenProvider.getUserIdFromToken(token);
//        chatRoomService.markAsRead(roomId, userId);
//        return ResponseEntity.ok("메시지 읽음 처리");
//    }
}
