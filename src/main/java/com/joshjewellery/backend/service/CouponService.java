package com.joshjewellery.backend.service;

import com.joshjewellery.backend.dto.CouponDTOs;

import java.util.List;
import java.util.UUID;

public interface CouponService {
    List<CouponDTOs.CouponResponse> getAllCoupons();
    CouponDTOs.CouponResponse createCoupon(CouponDTOs.CouponRequest request);
    CouponDTOs.CouponResponse updateCoupon(UUID id, CouponDTOs.CouponRequest request);
    CouponDTOs.CouponResponse toggleCouponStatus(UUID id);
    void deleteCoupon(UUID id);
    CouponDTOs.CouponValidationResponse validateCoupon(CouponDTOs.ApplyCouponRequest request);
}
