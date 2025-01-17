package com.example.ticketing.service.coupon;

import com.example.ticketing.exception.CouponException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponTemplate;
import com.example.ticketing.model.coupon.CouponTemplateCreateRequest;
import com.example.ticketing.model.coupon.CouponTemplateDTO;
import com.example.ticketing.repository.coupon.CouponEventRepository;
import com.example.ticketing.repository.coupon.CouponRedisRepository;
import com.example.ticketing.repository.coupon.CouponTemplateRepository;
import com.example.ticketing.repository.user.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponTemplateServiceImpl implements CouponTemplateService {
    private final CouponTemplateRepository couponTemplateRepository;
    private final CouponEventRepository couponEventRepository;
    private final CouponRedisRepository couponRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserCouponRepository userCouponRepository;

    @Override
    public CouponTemplateDTO createCouponTemplate(Long couponEventId, CouponTemplateCreateRequest dto) {
        CouponEvent couponEvent = couponEventRepository.findById(couponEventId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_EVENT_NOT_FOUND));

        validateCouponTemplateCreation(dto);

        // 쿠폰 템플릿 생성 및 저장
        CouponTemplate couponTemplate = couponTemplateRepository.save(dto.toEntity(couponEvent));

        // Redis에 쿠폰 수량 초기화
        couponRedisRepository.initializeCouponCount(couponTemplate.getId(), couponTemplate.getTotalQuantity());

        return CouponTemplateDTO.from(couponTemplate, false); // 생성 시 보유 여부는 false
    }

    @Override
    public CouponTemplateDTO getCouponTemplate(Long templateId, Long userId) {
        CouponTemplate template = couponTemplateRepository.findById(templateId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_TEMPLATE_NOT_FOUND));

        // Redis 수량 확인 및 초기화
        String countKey = "COUPON:TEMPLATE:COUNT:" + templateId;
        if (redisTemplate.opsForValue().get(countKey) == null) {
            couponRedisRepository.initializeCouponCount(template.getId(), template.getRemaining());
        }

        // 사용자 보유 여부 확인
        boolean isOwned = userId != null &&
                userCouponRepository.existsByUserIdAndCouponTemplateId(userId, templateId);

        return CouponTemplateDTO.from(template, isOwned);
    }

    @Override
    public List<CouponTemplateDTO> getCouponTemplatesByCouponEvent(Long couponEventId, Long userId) {
        if (!couponEventRepository.existsById(couponEventId)) {
            throw new CouponException(ErrorCode.COUPON_EVENT_NOT_FOUND);
        }

        List<CouponTemplate> templates = couponTemplateRepository.findByCouponEventId(couponEventId);

        // 모든 템플릿의 Redis 수량 확인 및 초기화
        templates.forEach(template -> {
            String countKey = "COUPON:TEMPLATE:COUNT:" + template.getId();
            if (redisTemplate.opsForValue().get(countKey) == null) {
                couponRedisRepository.initializeCouponCount(template.getId(), template.getRemaining());
            }
        });

        // 사용자 보유 여부 포함하여 DTO 변환
        return templates.stream()
                .map(template -> {
                    boolean isOwned = userId != null &&
                            userCouponRepository.existsByUserIdAndCouponTemplateId(userId, template.getId());
                    return CouponTemplateDTO.from(template, isOwned);
                })
                .collect(Collectors.toList());
    }

    private void validateCouponTemplateCreation(CouponTemplateCreateRequest dto) {
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
