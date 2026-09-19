package com.joshjewellery.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ProductDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductRequest {
        @NotBlank(message = "Title is required")
        private String title;

        private String sku;

        private String description;

        private UUID categoryId;

        @NotNull(message = "Price is required")
        private BigDecimal price;

        private BigDecimal salePrice;

        private Integer stockQuantity = 10;

        @JsonProperty("isBestSeller")
        private boolean isBestSeller = false;
        @JsonProperty("isNewArrival")
        private boolean isNewArrival = false;
        @JsonProperty("isFeatured")
        private boolean isFeatured = false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductResponse {
        private UUID id;
        private String title;
        private String slug;
        private String sku;
        private String description;
        private UUID categoryId;
        private String categoryName;
        private BigDecimal price;
        private BigDecimal salePrice;
        private BigDecimal rating;
        private Integer reviewCount;
        private Integer stockQuantity;
        @JsonProperty("isBestSeller")
        private boolean isBestSeller;
        @JsonProperty("isNewArrival")
        private boolean isNewArrival;
        @JsonProperty("isFeatured")
        private boolean isFeatured;
        private List<String> images;
    }
}
