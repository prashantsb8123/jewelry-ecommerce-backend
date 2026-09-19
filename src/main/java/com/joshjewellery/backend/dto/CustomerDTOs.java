package com.joshjewellery.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerSummary {
        private UUID id;
        private String name;
        private String email;
        private String phone;
        private Integer rewardPoints;
        private BigDecimal totalSpent;
        private Long ordersCount;
        private LocalDateTime joinedDate;
    }
}
