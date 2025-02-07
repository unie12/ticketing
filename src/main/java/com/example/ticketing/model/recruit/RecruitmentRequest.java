package com.example.ticketing.model.recruit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentRequest {
    private String title;
    private String content;
    private int maxParticipants;
    private LocalDateTime meetingTime;
}
