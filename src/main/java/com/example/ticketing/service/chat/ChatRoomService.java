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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final UserService userService;

    @Transactional
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

    public ChatRoom findChatRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_FOUND));
    }

    public boolean isParticipant(Long roomId, Long userId) {
        return chatRoomParticipantRepository.existsByChatRoom_IdAndUser_Id(roomId, userId);
    }

    public ChatRoomParticipant findByChatRoomAndUser(ChatRoom chatRoom, User user) {
        return chatRoomParticipantRepository.findByChatRoomAndUser(chatRoom, user)
                .orElseThrow(() -> new ChatException(ErrorCode.CHATROOM_NOT_PARTICIPANT));
    }

    public Set<Long> getRoomParticipants(Long roomId) {
        return new HashSet<>(
                chatRoomParticipantRepository.findParticipantIdsByChatRoom(roomId));
//        ChatRoom room = findChatRoomById(roomId);
//        return room.getChatRoomParticipants().stream()
//                .map(participant -> participant.getUser().getId())
//                .collect(Collectors.toSet());

    }

    @Transactional
    public void leaveChatRoom(Long roomId, Long userId) {
        User user = userService.findUserById(userId);
        ChatRoom chatRoom = findChatRoomById(roomId);

        ChatRoomParticipant participant = findByChatRoomAndUser(chatRoom, user);
        chatRoomParticipantRepository.delete(participant);
    }

    @Transactional
    public void markAsRead(Long roomId, Long userId) {
        User user = userService.findUserById(userId);
        ChatRoom chatRoom = findChatRoomById(roomId);

        ChatRoomParticipant participant = findByChatRoomAndUser(chatRoom, user);
        participant.updateLastReadAt();
        chatRoomParticipantRepository.save(participant);
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


}
