package com.example.ticketing.service.event;

import com.example.ticketing.model.event.Event;
import com.example.ticketing.model.event.EventCreateDTO;

import java.util.List;

public interface EventService {
    Event createEvent(EventCreateDTO dto);
    Event getEvent(Long eventId);
    List<Event> getEvents();
}
