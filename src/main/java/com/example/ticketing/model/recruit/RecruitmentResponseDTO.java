package com.example.ticketing.model.recruit;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RecruitmentResponseDTO {
    private Long id;
    private String storeId;
    private String title;
    private String placeName;
    private int maxParticipants;
    private int currentParticipants;
    private LocalDateTime meetingTime;
    private RecruitmentStatus status;

    public static RecruitmentResponseDTO from(RecruitmentPost post) {
        return RecruitmentResponseDTO.builder()
                .id(post.getId())
                .storeId(post.getStore().getId())
                .title(post.getTitle())
                .placeName(post.getStore().getPlaceName())
                .maxParticipants(post.getMaxParticipants())
                .currentParticipants(post.getCurrentParticipants())
                .meetingTime(post.getMeetingTime())
                .status(post.getStatus())
                .build();
    }
}
