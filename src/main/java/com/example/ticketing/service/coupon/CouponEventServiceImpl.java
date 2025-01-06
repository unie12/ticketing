package com.example.ticketing.service.coupon;

import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponEventCreateDTO;
import com.example.ticketing.model.event.Event;
import com.example.ticketing.model.user.UserCoupon;
import com.example.ticketing.repository.coupon.CouponEventRepository;
import com.example.ticketing.repository.coupon.CouponTemplateRepository;
import com.example.ticketing.repository.event.EventRepository;
import com.example.ticketing.repository.user.UserCouponRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponEventServiceImpl implements CouponEventService{
    private final CouponEventRepository couponEventRepository;
    private final CouponTemplateRepository couponTemplateRepository;
    private final UserCouponRepository userCouponRepository;
    private final EventRepository eventRepository;

//    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public CouponEvent createCouponEvent(Long eventId, CouponEventCreateDTO dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event is not found of eventID: " + eventId));

        validateCouponEventCreation(dto);

        CouponEvent couponEvent = CouponEvent.builder()
                .eventName(dto.getEventName())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .validityEndTime(dto.getValidityEndTime())
                .event(event)
                .build();

        return couponEventRepository.save(couponEvent);
    }

    @Override
    public CouponEvent getCouponEvent(Long couponEventId) {
        return couponEventRepository.findById(couponEventId)
                .orElseThrow(() -> new EntityNotFoundException("CouponEvent is not found of couponEventId: " + couponEventId));
    }

    @Override
    public List<CouponEvent> getCouponEventByEvent(Long eventId) {
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new EntityNotFoundException("Event is not found of eventID: " + eventId));

        return couponEventRepository.findByEventId(eventId);
    }


    private void validateCouponEventCreation(CouponEventCreateDTO dto) {
        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (dto.getEndTime().isAfter(dto.getValidityEndTime())) {
            throw new IllegalArgumentException("End time must be before validity end time");
        }
    }
}
