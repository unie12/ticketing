package com.example.ticketing.model.recruit;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ParticipantResponseDTO {
    private Long id;
    private String username;
    private String imgUrl;
    private LocalDateTime joinedAt;

    public static ParticipantResponseDTO from(Participant participant) {
        return ParticipantResponseDTO.builder()
                .id(participant.getId())
                .username(participant.getUser().getUsername())
                .imgUrl(participant.getUser().getImgUrl())
                .joinedAt(participant.getJoinedAt())
                .build();
    }
}
