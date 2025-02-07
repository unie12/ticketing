package com.example.ticketing.controller.chat;

import com.example.ticketing.model.chat.ChatMessage;
import com.example.ticketing.model.chat.ChatMessageRequest;
import com.example.ticketing.model.chat.ChatMessageResponseDTO;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.chat.ChatRoomService;
import com.example.ticketing.service.chat.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final JwtTokenProvider jwtTokenProvider;

    @MessageMapping("/{roomId}/send")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageRequest request) {
        messageService.sendMessage(request.getChatRoomId(), request.getSenderId(), request.getContent());
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<String> joinChatRoom(@PathVariable Long roomId, @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        String username = chatRoomService.joinChatRoom(roomId, userId);
        return ResponseEntity.ok(username + "님이 채팅방에 참여했습니다.");
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDTO>> getMessages(
            @PathVariable Long roomId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(messageService.getMessages(roomId, page, size));
    }

    @GetMapping("/{roomId}/unread-count")
    public ResponseEntity<Long> countUnreadMessages(@PathVariable Long roomId, @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        return ResponseEntity.ok(chatRoomService.countUnreadMessages(roomId, userId));
    }
}
