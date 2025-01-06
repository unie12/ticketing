package com.example.ticketing.service.coupon;

import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponTemplate;
import com.example.ticketing.model.coupon.CouponTemplateCreateDTO;
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
    public CouponTemplate createCouponTemplate(Long couponEventId, CouponTemplateCreateDTO dto) {
        CouponEvent couponEvent = couponEventRepository.findById(couponEventId)
                .orElseThrow(() -> new EntityNotFoundException("There is no CouponEvent of couponEventId: " + couponEventId));

        validateCouponTemplateCreation(dto);

        CouponTemplate couponTemplate = CouponTemplate.builder()
                .name(dto.getName())
                .weight(dto.getWeight())
                .totalQuantity(dto.getTotalQuantity())
                .discountAmount(dto.getDiscountAmount())
                .discountType(dto.getDiscountType())
                .couponEvent(couponEvent)
                .build();

        return couponTemplateRepository.save(couponTemplate);
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

    private void validateCouponTemplateCreation(CouponTemplateCreateDTO dto) {
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
