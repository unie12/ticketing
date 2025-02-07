package com.example.ticketing.repository.chat;

import com.example.ticketing.model.chat.ChatMessage;
import com.example.ticketing.model.chat.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.net.ContentHandler;
import java.time.LocalDateTime;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    long countByChatRoomAndTimestampAfter(ChatRoom chatRoom, LocalDateTime lasReadAt);

    Page<ChatMessage> findByChatRoom_Id(Long roomId, Pageable pageable);
}
