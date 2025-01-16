package com.example.ticketing.service.event;

import com.example.ticketing.model.event.Event;
import com.example.ticketing.model.event.EventCreateDTO;
import com.example.ticketing.model.event.EventDTO;
import com.example.ticketing.repository.event.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    @Nested
    @DisplayName("이벤트 목록 조회 테스트")
    class GetEventsTest {
        @Test
        @DisplayName("전체 이벤트 목록을 정상적으로 조회한다")
        void getEventsSuccess() {
            // given
            List<Event> events = Arrays.asList(
                    createEvent(1L, "콘서트", 100),
                    createEvent(2L, "연극", 50)
            );
            when(eventRepository.findAll()).thenReturn(events);

            // when
            List<EventDTO> result = eventService.getEvents();

            // then
            assertThat(result).hasSize(2);
//            verify(eventRepository).findAll();
            verify(eventRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("단일 이벤트 조회 테스트")
    class GetEventTest {
        @Test
        @DisplayName("ID로 이벤트를 정상적으로 조회한다")
        void getEventSuccess() {
            // given
            Event event = createEvent(1L, "콘서트", 100);
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

            // when
            EventDTO result = eventService.getEvent(1L);

            // then
            assertThat(result.getName()).isEqualTo("콘서트");
//            verify(eventRepository).findById(1L);
            verify(eventRepository, times(1)).findById(1L);

        }

        @Test
        @DisplayName("존재하지 않는 이벤트 조회시 예외가 발생한다")
        void getEventNotFound() {
            // given
            when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when & then
            assertThrows(EntityNotFoundException.class,
                    () -> eventService.getEvent(1L));
        }
    }

    @Nested
    @DisplayName("이벤트 생성 테스트")
    class CreateEventTest {
        @Test
        @DisplayName("유효한 데이터로 이벤트를 생성한다")
        void createEventSuccess() {
            // given
            EventCreateDTO dto = EventCreateDTO.builder()
                    .name("콘서트")
                    .totalSeats(100)
                    .build();
            Event event = createEvent(1L, "콘서트", 100);

            when(eventRepository.save(any(Event.class))).thenReturn(event);

            // when
            EventDTO result = eventService.createEvent(dto);

            // then
            assertThat(result.getName()).isEqualTo("콘서트");
            assertThat(result.getTotalSeats()).isEqualTo(100);
//            verify(eventRepository).save(any(Event.class));
            verify(eventRepository, times(1)).save(any(Event.class));

        }

        @Test
        @DisplayName("이벤트 이름이 없으면 예외가 발생한다")
        void createEventWithEmptyName() {
            // given
            EventCreateDTO dto = EventCreateDTO.builder()
                    .name("")
                    .totalSeats(100)
                    .build();

            // when & then
            assertThrows(IllegalArgumentException.class,
                    () -> eventService.createEvent(dto));
        }

        @Test
        @DisplayName("좌석 수가 0이하면 예외가 발생한다")
        void createEventWithInvalidSeats() {
            // given
            EventCreateDTO dto = EventCreateDTO.builder()
                    .name("콘서트")
                    .totalSeats(0)
                    .build();

            // when & then
            assertThrows(IllegalArgumentException.class,
                    () -> eventService.createEvent(dto));
        }
    }

    private Event createEvent(Long id, String name, int totalSeats) {
        return Event.builder()
                .name(name)
                .totalSeats(totalSeats)
                .build();
    }
}
