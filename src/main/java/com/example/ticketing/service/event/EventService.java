package com.example.ticketing.service.event;

import com.example.ticketing.model.event.Event;
import com.example.ticketing.model.event.EventCreateDTO;
import com.example.ticketing.model.event.EventDTO;

import java.util.List;

public interface EventService {
    EventDTO createEvent(EventCreateDTO dto);
    EventDTO getEvent(Long eventId);
    List<EventDTO> getEvents();
}
