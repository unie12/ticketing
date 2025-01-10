package com.example.ticketing.service.coupon;

import com.example.ticketing.exception.CouponException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponTemplate;
import com.example.ticketing.model.coupon.CouponTemplateDTO;
import com.example.ticketing.repository.coupon.CouponEventRepository;
import com.example.ticketing.repository.coupon.CouponTemplateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponTemplateServiceImpl implements CouponTemplateService{
    private final CouponTemplateRepository couponTemplateRepository;
    private final CouponEventRepository couponEventRepository;

    @Override
    public CouponTemplate createCouponTemplate(Long couponEventId, CouponTemplateDTO dto) {
        CouponEvent couponEvent = couponEventRepository.findById(couponEventId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_EVENT_NOT_FOUND));

        validateCouponTemplateCreation(dto);

        return couponTemplateRepository.save(dto.toEntity(couponEvent));

    }

    @Override
    public CouponTemplate getCouponTemplate(Long templateId) {
        return couponTemplateRepository.findById(templateId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_TEMPLATE_NOT_FOUND));
    }

    @Override
    public List<CouponTemplate> getCouponTemplatesByCouponEvent(Long couponEventId) {
        if (!couponEventRepository.existsById(couponEventId)) {
            throw new CouponException(ErrorCode.COUPON_EVENT_NOT_FOUND);
        }
        return couponTemplateRepository.findByCouponEventId(couponEventId);
    }

    private void validateCouponTemplateCreation(CouponTemplateDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new CouponException(ErrorCode.COUPON_TEMPLATE_NAME_EMPTY);
        }
        if (dto.getWeight() <= 0) {
            throw new CouponException(ErrorCode.INVALID_COUPON_WEIGHT);
        }
        if (dto.getTotalQuantity() <= 0) {
            throw new CouponException(ErrorCode.INVALID_COUPON_QUANTITY);
        }
        if (dto.getDiscountAmount() <= 0) {
            throw new CouponException(ErrorCode.INVALID_DISCOUNT_AMOUNT);
        }
    }
}
