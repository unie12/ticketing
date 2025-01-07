package com.example.ticketing.model.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventDTO {
    private final String name;
    private final int totalSeats;
    private final int remainingSeats;
    private final LocalDateTime createdAt;

    @Builder
    public EventDTO(String name, int totalSeats, int remainingSeats, LocalDateTime createdAt) {
        this.name = name;
        this.totalSeats = totalSeats;
        this.remainingSeats = remainingSeats;
        this.createdAt = createdAt;
    }

    public static EventDTO from(Event event) {
        return new EventDTO(
                event.getName(),
                event.getTotalSeats(),
                event.getRemainingSeats(),
                event.getCreatedAt()
        );
    }
}
