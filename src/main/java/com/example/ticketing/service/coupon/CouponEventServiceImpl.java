package com.example.ticketing.service.coupon;

import com.example.ticketing.exception.CouponException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.exception.EventException;
import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponEventCreateRequest;
import com.example.ticketing.model.coupon.CouponEventResponse;
import com.example.ticketing.model.event.Event;
import com.example.ticketing.repository.coupon.CouponEventRepository;
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
public class CouponEventServiceImpl implements CouponEventService {
    private final CouponEventRepository couponEventRepository;
    private final EventRepository eventRepository;

    @Override
    public CouponEventResponse createCouponEvent(Long eventId, CouponEventCreateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));

        validateCouponEventCreation(request);

        // 쿠폰 이벤트 생성 및 저장
        CouponEvent couponEvent = couponEventRepository.save(request.toEntity(event));
        return CouponEventResponse.from(couponEvent);
    }

    @Override
    public CouponEventResponse getCouponEvent(Long couponEventId) {
        CouponEvent couponEvent = couponEventRepository.findById(couponEventId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_EVENT_NOT_FOUND));
        return CouponEventResponse.from(couponEvent);
    }

    @Override
    public List<CouponEventResponse> getCouponEventByEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventException(ErrorCode.EVENT_NOT_FOUND);
        }

        List<CouponEvent> couponEvents = couponEventRepository.findByEventId(eventId);
        return couponEvents.stream()
                .map(CouponEventResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponEventResponse> getCouponEvents() {
        List<CouponEvent> couponEvents = couponEventRepository.findAll();
        return couponEvents.stream()
                .map(CouponEventResponse::from)
                .collect(Collectors.toList());
    }

    private void validateCouponEventCreation(CouponEventCreateRequest request) {
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
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
