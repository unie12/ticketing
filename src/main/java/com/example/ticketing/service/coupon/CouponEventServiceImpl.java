package com.example.ticketing.service.coupon;

import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponEventCreateRequest;
import com.example.ticketing.model.event.Event;
import com.example.ticketing.repository.coupon.CouponEventRepository;
import com.example.ticketing.repository.event.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponEventServiceImpl implements CouponEventService{
    private final CouponEventRepository couponEventRepository;
    private final EventRepository eventRepository;

//    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public CouponEvent createCouponEvent(Long eventId, CouponEventCreateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event is not found of eventID: " + eventId));

        validateCouponEventCreation(request);

        return couponEventRepository.save(request.toEntity(event));
    }

    @Override
    public CouponEvent getCouponEvent(Long couponEventId) {
        return couponEventRepository.findById(couponEventId)
                .orElseThrow(() -> new EntityNotFoundException("CouponEvent is not found of couponEventId: " + couponEventId));
    }

    @Override
    public List<CouponEvent> getCouponEventByEvent(Long eventId) {
        return couponEventRepository.findByEventId(eventId);
    }

    private void validateCouponEventCreation(CouponEventCreateRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (request.getEndTime().isAfter(request.getValidityEndTime())) {
            throw new IllegalArgumentException("End time must be before validity end time");
        }
    }

}
