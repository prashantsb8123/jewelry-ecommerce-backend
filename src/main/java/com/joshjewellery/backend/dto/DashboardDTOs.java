package com.joshjewellery.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DashboardDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenuePoint {
        private String month;
        private BigDecimal sales;
        private Long orders;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySalesPoint {
        private String category;
        private BigDecimal value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowStockItem {
        private UUID id;
        private String title;
        private Integer stockQuantity;
        private String image;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentOrderSummary {
        private UUID id;
        private String orderNumber;
        private String customerName;
        private BigDecimal totalAmount;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardSummaryResponse {
        private BigDecimal totalRevenue;
        private Long totalOrders;
        private Long totalCustomers;
        private Long totalProducts;
        private Long pendingOrders;
        private List<MonthlyRevenuePoint> monthlyRevenueChart;
        private List<CategorySalesPoint> categorySalesDistribution;
        private List<LowStockItem> lowStockItems;
        private List<RecentOrderSummary> recentOrders;
    }
}
