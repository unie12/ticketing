package com.example.ticketing.controller.event;

import com.example.ticketing.model.event.Event;
import com.example.ticketing.model.event.EventCreateDTO;
import com.example.ticketing.model.event.EventDTO;
import com.example.ticketing.service.event.EventService;
import jakarta.validation.Valid;
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
        return ResponseEntity.ok(eventService.getEvent(eventId));
    }

    @GetMapping
    public ResponseEntity<List<EventDTO>> getEvents() {
        return ResponseEntity.ok(eventService.getEvents());
    }

    @PreAuthorize("hasAnyRole('EVENT_MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(dto));
    }
}
