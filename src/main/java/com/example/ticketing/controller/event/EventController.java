package com.example.ticketing.controller.event;

import com.example.ticketing.model.event.Event;
import com.example.ticketing.model.event.EventCreateDTO;
import com.example.ticketing.model.event.EventDTO;
import com.example.ticketing.service.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable Long eventId) {
        Event event = eventService.getEvent(eventId);
        return ResponseEntity.ok(EventDTO.from(event));
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getEvents() {
        List<Event> events = eventService.getEvents();
        List<EventDTO> eventDTOs = events.stream()
                .map(EventDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventDTOs);
    }

    @PreAuthorize("hasAnyRole('EVENT_MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventCreateDTO dto) {
        Event event = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EventDTO.from(event));
    }
}
