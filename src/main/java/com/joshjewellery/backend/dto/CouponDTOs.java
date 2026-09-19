package com.joshjewellery.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CouponDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponRequest {
        @NotBlank(message = "Coupon code is required")
        private String code;

        @NotNull(message = "Discount percentage is required")
        @DecimalMin(value = "0.01", message = "Discount percentage must be greater than 0")
        private BigDecimal discountPercentage;

        private BigDecimal maxDiscount;

        private BigDecimal minOrderValue = BigDecimal.ZERO;

        @NotNull(message = "Expiry date is required")
        private LocalDateTime expiryDate;

        private Integer usageLimit = 1000;

        @JsonProperty("isActive")
        private boolean isActive = true;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplyCouponRequest {
        @NotBlank(message = "Coupon code is required")
        private String code;

        @NotNull(message = "Order amount is required")
        private BigDecimal orderAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponResponse {
        private UUID id;
        private String code;
        private BigDecimal discountPercentage;
        private BigDecimal maxDiscount;
        private BigDecimal minOrderValue;
        private LocalDateTime expiryDate;
        @JsonProperty("isActive")
        private boolean isActive;
        private Integer usageLimit;
        private Integer timesUsed;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponValidationResponse {
        private boolean valid;
        private String message;
        private BigDecimal discountAmount;
        private CouponResponse coupon;
    }
}
