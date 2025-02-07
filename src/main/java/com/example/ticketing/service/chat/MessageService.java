package com.example.ticketing.service.chat;

import com.example.ticketing.model.chat.ChatMessage;
import com.example.ticketing.model.chat.ChatMessageResponseDTO;
import com.example.ticketing.model.chat.ChatRoom;
import com.example.ticketing.model.user.User;

import java.util.List;

public interface MessageService {
    ChatMessageResponseDTO sendMessage(Long roomId, Long senderId, String content);

    public List<ChatMessageResponseDTO> getMessages(Long roomId, int page, int size);
}
