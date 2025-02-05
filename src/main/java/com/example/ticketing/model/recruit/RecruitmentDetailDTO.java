package com.example.ticketing.model.recruit;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class RecruitmentDetailDTO {
    private Long id;

    private String storeId;
    private String placeName;

    private String title;
    private String content;
    private int maxParticipants;
    private int currentParticipants;
    private LocalDateTime meetingTime;
    private RecruitmentStatus status;
    private LocalDateTime createdAt;

    private AuthorDTO author;
    private List<ParticipantResponseDTO> participants;

    public static RecruitmentDetailDTO from(RecruitmentPost recruitmentPost) {
        return RecruitmentDetailDTO.builder()
                .id(recruitmentPost.getId())
                .storeId(recruitmentPost.getStore().getId())
                .placeName(recruitmentPost.getStore().getPlaceName())
                // 글 정보
                .title(recruitmentPost.getTitle())
                .content(recruitmentPost.getContent())
                .maxParticipants(recruitmentPost.getMaxParticipants())
                .currentParticipants(recruitmentPost.getCurrentParticipants())
                .meetingTime(recruitmentPost.getMeetingTime())
                .status(recruitmentPost.getStatus())
                // 작성자 정보
                .author(AuthorDTO.builder()
                        .id(recruitmentPost.getAuthor().getId())
                        .username(recruitmentPost.getAuthor().getUsername())
                        .imgUrl(recruitmentPost.getAuthor().getImgUrl())
                        .build())
                // 참여자 목록
                .participants(recruitmentPost.getParticipants().stream()
                        .map(participant -> ParticipantResponseDTO.builder()
                                .id(participant.getUser().getId())
                                .username(participant.getUser().getUsername())
                                .imgUrl(participant.getUser().getImgUrl())
                                .joinedAt(participant.getJoinedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
