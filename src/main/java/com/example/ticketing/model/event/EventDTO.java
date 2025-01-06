package com.example.ticketing.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String name;
    private int totalSeats;
    private int remainingSeats;
    private LocalDateTime createdAt;

    public static EventDTO from(Event event) {
        return new EventDTO(
                event.getName(),
                event.getTotalSeats(),
                event.getRemainingSeats(),
                event.getCreatedAt()
        );
    }
}
