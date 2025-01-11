package com.example.ticketing.service.coupon;

import com.example.ticketing.exception.CouponException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.exception.EventException;
import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponEventCreateRequest;
import com.example.ticketing.model.event.Event;
import com.example.ticketing.repository.coupon.CouponEventRepository;
import com.example.ticketing.repository.event.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponEventServiceImpl implements CouponEventService{
    private final CouponEventRepository couponEventRepository;
    private final EventRepository eventRepository;

    @Override
    public CouponEvent createCouponEvent(Long eventId, CouponEventCreateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));

        validateCouponEventCreation(request);

        return couponEventRepository.save(request.toEntity(event));
    }

    @Override
    public CouponEvent getCouponEvent(Long couponEventId) {
        return couponEventRepository.findById(couponEventId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_EVENT_NOT_FOUND));
    }

    @Override
    public List<CouponEvent> getCouponEventByEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventException(ErrorCode.EVENT_NOT_FOUND);
        }
        return couponEventRepository.findByEventId(eventId);
    }

    @Override
    public List<CouponEvent> getCouponEvents() {
        return couponEventRepository.findAll();
    }

    private void validateCouponEventCreation(CouponEventCreateRequest request) {
        LocalDateTime now = LocalDateTime.now();

        if (request.getStartTime().isBefore(now)) {
            throw new CouponException(ErrorCode.INVALID_START_TIME);
        }

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new CouponException(ErrorCode.INVALID_EVENT_TIME_RANGE);
        }
        if (request.getEndTime().isAfter(request.getValidityEndTime())) {
            throw new CouponException(ErrorCode.INVALID_VALIDITY_TIME_RANGE);
        }
    }

}
