package com.joshjewellery.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CartDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddToCartRequest {
        @NotNull(message = "Product ID is required")
        private UUID productId;
        private Integer quantity = 1;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponse {
        private UUID id;
        private ProductDTOs.ProductResponse product;
        private Integer quantity;
        private BigDecimal itemTotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartResponse {
        private UUID id;
        private List<CartItemResponse> items;
        private BigDecimal subtotal;
        private BigDecimal estimatedGst;
        private BigDecimal grandTotal;
    }
}
