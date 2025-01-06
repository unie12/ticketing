package com.example.ticketing.service.event;

import com.example.ticketing.model.event.Event;
import com.example.ticketing.model.event.EventCreateDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EventServiceImplTest {
    @Autowired
    private EventService eventService;

    @Test
    @DisplayName("이벤트 생성 테스트")
    void createEvent() {
        // given
        EventCreateDTO dto = new EventCreateDTO("테스트 이벤트1", 100);

        // when
        Event created = eventService.createEvent(dto);

        // then
        assertNotNull(created.getId());
        assertEquals("테스트 이벤트1", created.getName());
        assertEquals(100, created.getTotalSeats());
        assertEquals(100, created.getRemainingSeats());
    }

    @Test
    @DisplayName("이벤트 조회 테스트")
    void getEvent() {
        // given
        EventCreateDTO dto = new EventCreateDTO("테스트 이벤트1", 100);
        Event savedEvent = eventService.createEvent(dto);

        // when
        Event foundEvent = eventService.getEvent(savedEvent.getId());

        // then
        assertNotNull(foundEvent);
        assertEquals(savedEvent.getId(), foundEvent.getId());
        assertEquals("테스트 이벤트1", foundEvent.getName());
        assertEquals(100, foundEvent.getTotalSeats());
    }

    @Test
    @DisplayName("존재하지 않는 이벤트 조회시 예외 발생")
    void getEvent_NotFound() {
        // given
        Long nonExistentId = 999L;

        // when & then
        assertThrows(EntityNotFoundException.class, () -> {
            eventService.getEvent(nonExistentId);
        });
    }

    @Test
    @DisplayName("이벤트 생성시 좌석수 검증")
    void createEvent_InvalidSeats() {
        // given
        EventCreateDTO dto = new EventCreateDTO("테스트 이벤트1", 0);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(dto);
        });
    }
}