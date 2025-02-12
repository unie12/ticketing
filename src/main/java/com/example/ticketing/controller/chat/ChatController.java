package com.example.ticketing.controller.chat;

import com.example.ticketing.model.chat.ChatMessageRequest;
import com.example.ticketing.model.chat.ChatMessageResponseDTO;
import com.example.ticketing.model.chat.UserPrincipal;
import com.example.ticketing.security.JwtTokenProvider;
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

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatPresenceService presenceService;

    @MessageMapping("/{roomId}/send")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessageResponseDTO sendMessage(
            @DestinationVariable Long roomId,
            ChatMessageRequest request,
            @Header("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserIdFromToken(jwtToken);
        return messageService.sendMessage(roomId, userId, request.getContent());
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
    public ResponseEntity<List<ChatMessageResponseDTO>> getMessages(
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
