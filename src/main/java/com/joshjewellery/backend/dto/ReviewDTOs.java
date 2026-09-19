package com.joshjewellery.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReviewDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewRequest {
        @NotNull(message = "Product is required")
        private UUID productId;

        @NotBlank(message = "Name is required")
        private String reviewerName;

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5")
        private Integer rating;

        @NotBlank(message = "Review comment is required")
        private String comment;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewResponse {
        private UUID id;
        private UUID productId;
        private String productTitle;
        private String reviewerName;
        private Integer rating;
        private String comment;
        @JsonProperty("isApproved")
        private boolean isApproved;
        private LocalDateTime createdAt;
    }
}
