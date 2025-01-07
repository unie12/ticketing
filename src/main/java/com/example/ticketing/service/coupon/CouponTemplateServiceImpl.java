package com.example.ticketing.service.coupon;

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
                .orElseThrow(() -> new EntityNotFoundException("There is no CouponEvent of couponEventId: " + couponEventId));

        validateCouponTemplateCreation(dto);

        return couponTemplateRepository.save(dto.toEntity(couponEvent));

    }

    @Override
    public CouponTemplate getCouponTemplate(Long templateId) {
        return couponTemplateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("There is no CouponTemplate of id: " + templateId));
    }

    @Override
    public List<CouponTemplate> getCouponTemplatesByCouponEvent(Long couponEventId) {
        return couponTemplateRepository.findByCouponEventId(couponEventId);
    }

    private void validateCouponTemplateCreation(CouponTemplateDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon template name cannot be empty");
        }
        if (dto.getWeight() <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
        if (dto.getTotalQuantity() <= 0) {
            throw new IllegalArgumentException("Total quantity must be greater than 0");
        }
        if (dto.getDiscountAmount() <= 0) {
            throw new IllegalArgumentException("Discount amount must be greater than 0");
        }
    }
}
