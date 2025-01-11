package com.example.ticketing.model.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventCreateDTO {
    @NotBlank(message = "이벤트 이름은 필수입니다")
    private final String name;

    @Positive(message = "좌석 수는 0보다 커야 합니다")
    private final int totalSeats;

    @NotNull(message = "시작 시간은 필수입니다")
    private final LocalDateTime startTime;

    @NotNull(message = "종료 시간은 필수입니다")
    private final LocalDateTime endTime;

    @Builder
    public EventCreateDTO(String name, int totalSeats, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.totalSeats = totalSeats;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
