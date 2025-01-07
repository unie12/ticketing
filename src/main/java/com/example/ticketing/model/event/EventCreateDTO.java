package com.example.ticketing.model.event;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EventCreateDTO {
    private final String name;
    private final int totalSeats;

    @Builder
    private EventCreateDTO(String name, int totalSeats) {
        this.name = name;
        this.totalSeats = totalSeats;
    }
}
