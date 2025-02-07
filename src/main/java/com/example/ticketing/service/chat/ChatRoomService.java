package com.example.ticketing.service.chat;

import com.example.ticketing.exception.ChatException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.model.chat.ChatRoom;
import com.example.ticketing.model.chat.ChatRoomParticipant;
import com.example.ticketing.model.user.User;
import com.example.ticketing.repository.chat.ChatMessageRepository;
import com.example.ticketing.repository.chat.ChatRoomParticipantRepository;
import com.example.ticketing.repository.chat.ChatRoomRepository;
import com.example.ticketing.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final UserService userService;

    public String joinChatRoom(Long roomId, Long userId) {
        User user = userService.findUserById(userId);
        ChatRoom chatRoom = findChatRoomById(roomId);
        if (chatRoomParticipantRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new ChatException(ErrorCode.CHATROOM_ALREADY_PARTICIPATED);
        }

        ChatRoomParticipant participant = ChatRoomParticipant.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();

        chatRoomParticipantRepository.save(participant);
        return user.getUsername();
    }

    public List<ChatRoom> getUserChatRooms(Long userId) {
        return chatRoomParticipantRepository.findAllByUser_Id(userId).stream()
                .map(ChatRoomParticipant::getChatRoom)
                .collect(Collectors.toList());
    }

    public long countUnreadMessages(Long roomId, Long userId) {
        User user = userService.findUserById(userId);
        ChatRoom chatRoom = findChatRoomById(roomId);
        LocalDateTime lastReadAt = chatRoomParticipantRepository.findLastReadAtChatRoomAndUser(chatRoom, user);
        if (lastReadAt == null) {
            throw new ChatException(ErrorCode.UNREAD_COUNT_CALCULATION_FAILED);
        }
        return chatMessageRepository.countByChatRoomAndTimestampAfter(chatRoom, lastReadAt);
    }

    public ChatRoom findChatRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));
    }
}
