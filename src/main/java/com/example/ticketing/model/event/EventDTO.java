package com.example.ticketing.model.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventDTO {
    private final Long id;
    private final String name;
    private final int totalSeats;
    private final int remainingSeats;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public static EventDTO from(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .totalSeats(event.getTotalSeats())
                .remainingSeats(event.getRemainingSeats())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .build();
    }
}
