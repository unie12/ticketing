package com.example.ticketing.repository.chat;

import com.example.ticketing.model.chat.ChatRoom;
import com.example.ticketing.model.chat.ChatRoomParticipant;
import com.example.ticketing.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {
    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);

    Optional<ChatRoomParticipant> findByChatRoomAndUser(ChatRoom chatRoom, User user);

    @Query("SELECT p.lastReadAt FROM ChatRoomParticipant p WHERE p.chatRoom = :chatRoom And p.user = :user")
    LocalDateTime findLastReadAtChatRoomAndUser(@Param("chatRoom") ChatRoom chatRoom, @Param("user") User user);

    List<ChatRoomParticipant> findAllByUser_Id(Long userId);

    boolean existsByChatRoom_IdAndUser_Id(Long roomId, Long userId);

    @Query("SELECT p.user.id FROM ChatRoomParticipant p WHERE p.chatRoom.id = :roomId")
    List<Long> findParticipantIdsByChatRoom(@Param("roomId") Long roomId);
}
