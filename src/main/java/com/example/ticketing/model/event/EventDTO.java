package com.example.ticketing.model.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventDTO {
    private final Long id;
    private final String name;
    private final int totalSeats;
    private final int remainingSeats;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    @Builder
    public EventDTO(Long id, String name, int totalSeats, int remainingSeats, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.totalSeats = totalSeats;
        this.remainingSeats = remainingSeats;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static EventDTO from(Event event) {
        return new EventDTO(
                event.getId(), event.getName(),
                event.getTotalSeats(),
                event.getRemainingSeats(),
                event.getStartTime(),
                event.getEndTime()
        );
    }
}
