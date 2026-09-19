package com.joshjewellery.backend.controller;

import com.joshjewellery.backend.dto.CouponDTOs;
import com.joshjewellery.backend.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Public coupon validation for cart/checkout")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/validate")
    @Operation(summary = "Validate a coupon code against an order amount")
    public ResponseEntity<CouponDTOs.CouponValidationResponse> validateCoupon(@Valid @RequestBody CouponDTOs.ApplyCouponRequest request) {
        return ResponseEntity.ok(couponService.validateCoupon(request));
    }
}
