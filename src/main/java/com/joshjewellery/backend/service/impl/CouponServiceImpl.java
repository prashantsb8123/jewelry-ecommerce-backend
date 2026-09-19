package com.joshjewellery.backend.service.impl;

import com.joshjewellery.backend.dto.CouponDTOs;
import com.joshjewellery.backend.entity.Coupon;
import com.joshjewellery.backend.exception.BadRequestException;
import com.joshjewellery.backend.exception.ResourceNotFoundException;
import com.joshjewellery.backend.repository.CouponRepository;
import com.joshjewellery.backend.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CouponDTOs.CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public CouponDTOs.CouponResponse createCoupon(CouponDTOs.CouponRequest request) {
        Coupon coupon = Coupon.builder()
                .code(request.getCode().trim().toUpperCase())
                .discountPercentage(request.getDiscountPercentage())
                .maxDiscount(request.getMaxDiscount())
                .minOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : BigDecimal.ZERO)
                .expiryDate(request.getExpiryDate())
                .usageLimit(request.getUsageLimit() != null ? request.getUsageLimit() : 1000)
                .isActive(request.isActive())
                .timesUsed(0)
                .build();
        return mapToResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public CouponDTOs.CouponResponse updateCoupon(UUID id, CouponDTOs.CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        coupon.setCode(request.getCode().trim().toUpperCase());
        coupon.setDiscountPercentage(request.getDiscountPercentage());
        coupon.setMaxDiscount(request.getMaxDiscount());
        coupon.setMinOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : BigDecimal.ZERO);
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setUsageLimit(request.getUsageLimit() != null ? request.getUsageLimit() : coupon.getUsageLimit());
        coupon.setActive(request.isActive());
        return mapToResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public CouponDTOs.CouponResponse toggleCouponStatus(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id: " + id));
        coupon.setActive(!coupon.isActive());
        return mapToResponse(couponRepository.save(coupon));
    }

    @Override
    @Transactional
    public void deleteCoupon(UUID id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found with id: " + id);
        }
        couponRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CouponDTOs.CouponValidationResponse validateCoupon(CouponDTOs.ApplyCouponRequest request) {
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new BadRequestException("Coupon code is required");
        }
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(request.getCode().trim().toUpperCase())
                .orElse(null);

        if (coupon == null) {
            return CouponDTOs.CouponValidationResponse.builder()
                    .valid(false)
                    .message("Invalid or inactive coupon code")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }
        if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
            return CouponDTOs.CouponValidationResponse.builder()
                    .valid(false)
                    .message("This coupon has expired")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }
        if (coupon.getUsageLimit() != null && coupon.getTimesUsed() != null && coupon.getTimesUsed() >= coupon.getUsageLimit()) {
            return CouponDTOs.CouponValidationResponse.builder()
                    .valid(false)
                    .message("This coupon has reached its usage limit")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }
        BigDecimal orderAmount = request.getOrderAmount() != null ? request.getOrderAmount() : BigDecimal.ZERO;
        if (coupon.getMinOrderValue() != null && orderAmount.compareTo(coupon.getMinOrderValue()) < 0) {
            return CouponDTOs.CouponValidationResponse.builder()
                    .valid(false)
                    .message("Minimum order value of " + coupon.getMinOrderValue() + " required for this coupon")
                    .discountAmount(BigDecimal.ZERO)
                    .build();
        }

        BigDecimal discount = orderAmount
                .multiply(coupon.getDiscountPercentage())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
            discount = coupon.getMaxDiscount();
        }

        return CouponDTOs.CouponValidationResponse.builder()
                .valid(true)
                .message("Coupon applied successfully")
                .discountAmount(discount)
                .coupon(mapToResponse(coupon))
                .build();
    }

    private CouponDTOs.CouponResponse mapToResponse(Coupon coupon) {
        return CouponDTOs.CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .discountPercentage(coupon.getDiscountPercentage())
                .maxDiscount(coupon.getMaxDiscount())
                .minOrderValue(coupon.getMinOrderValue())
                .expiryDate(coupon.getExpiryDate())
                .isActive(coupon.isActive())
                .usageLimit(coupon.getUsageLimit())
                .timesUsed(coupon.getTimesUsed())
                .build();
    }
}
