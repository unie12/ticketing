package com.example.ticketing.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PresenceChangeDTO {
    private Long userId;
    private String status;
}
