package com.example.ticketing.service.event;

import com.example.ticketing.model.event.Event;
import com.example.ticketing.model.event.EventCreateDTO;
import com.example.ticketing.repository.event.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public Event createEvent(EventCreateDTO dto) {
        validateEventCreation(dto);

        Event event = Event.builder()
                .name(dto.getName())
                .totalSeats(dto.getTotalSeats())
                .build();

        return eventRepository.save(event);
    }

    @Override
    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
    }

    @Override
    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    private void validateEventCreation(EventCreateDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty())  {
            throw new IllegalArgumentException("Event name cannot be empty");
        }
        if (dto.getTotalSeats() <= 0) {
            throw new IllegalArgumentException("Total seats must be greater than 0");
        }
    }
}
