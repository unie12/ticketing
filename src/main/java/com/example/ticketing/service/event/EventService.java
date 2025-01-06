package com.example.ticketing.service.event;

import com.example.ticketing.model.event.Event;
import com.example.ticketing.model.event.EventCreateDTO;

public interface EventService {
    Event createEvent(EventCreateDTO dto);
    Event getEvent(Long eventId);
}
