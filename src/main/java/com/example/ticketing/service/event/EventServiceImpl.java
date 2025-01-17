package com.example.ticketing.service.event;

import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.exception.EventException;
import com.example.ticketing.model.event.Event;
import com.example.ticketing.model.event.EventCreateDTO;
import com.example.ticketing.model.event.EventDTO;
import com.example.ticketing.repository.event.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public EventDTO createEvent(EventCreateDTO dto) {
        validateEventCreation(dto);

        // 이벤트 생성 및 저장
        Event event = Event.builder()
                .name(dto.getName())
                .totalSeats(dto.getTotalSeats())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();

        return EventDTO.from(eventRepository.save(event));
    }

    @Override
    public EventDTO getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));
        return EventDTO.from(event);
    }

    @Override
    public List<EventDTO> getEvents() {
        return eventRepository.findAll().stream()
                .map(EventDTO::from)
                .collect(Collectors.toList());
    }

    private void validateEventCreation(EventCreateDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty())  {
            throw new EventException(ErrorCode.EVENT_NAME_EMPTY);
        }
        if (dto.getTotalSeats() <= 0) {
            throw new EventException(ErrorCode.EVENT_SEAT_AMOUNT);
        }
        validateEventPeriod(dto.getStartTime(), dto.getEndTime());
    }

    private void validateEventPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();

        if (startTime == null || endTime == null) {
            throw new EventException(ErrorCode.INVALID_EVENT_TIME_RANGE);
        }

        if (startTime.isBefore(now)) {
            throw new EventException(ErrorCode.INVALID_START_TIME);
        }

        if (endTime.isBefore(startTime)) {
            throw new EventException(ErrorCode.INVALID_EVENT_TIME_RANGE);
        }
    }
}
