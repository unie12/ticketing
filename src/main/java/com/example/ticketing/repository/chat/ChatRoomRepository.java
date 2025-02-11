package com.example.ticketing.repository.chat;

import com.example.ticketing.model.chat.ChatRoom;
import com.example.ticketing.model.chat.ChatRoomParticipant;
import com.example.ticketing.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
//    Optional<ChatRoomParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);
}
