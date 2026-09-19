package com.joshjewellery.backend.service.impl;

import com.joshjewellery.backend.dto.DashboardDTOs;
import com.joshjewellery.backend.entity.Order;
import com.joshjewellery.backend.entity.Product;
import com.joshjewellery.backend.entity.ProductImage;
import com.joshjewellery.backend.repository.OrderRepository;
import com.joshjewellery.backend.repository.ProductRepository;
import com.joshjewellery.backend.repository.UserRepository;
import com.joshjewellery.backend.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final int LOW_STOCK_THRESHOLD = 5;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardDTOs.DashboardSummaryResponse getDashboardSummary() {
        BigDecimal totalRevenue = orderRepository.calculateTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        Long totalOrders = orderRepository.count();
        Long totalCustomers = userRepository.count();
        Long totalProducts = productRepository.count();
        Long pendingOrders = orderRepository.countPendingOrders();

        List<DashboardDTOs.MonthlyRevenuePoint> monthlyRevenueChart = orderRepository.findMonthlyRevenue().stream()
                .map(row -> DashboardDTOs.MonthlyRevenuePoint.builder()
                        .month((String) row[0])
                        .sales((BigDecimal) row[1])
                        .orders(((Number) row[2]).longValue())
                        .build())
                .toList();

        List<DashboardDTOs.CategorySalesPoint> categorySalesDistribution = orderRepository.findCategorySalesDistribution().stream()
                .map(row -> DashboardDTOs.CategorySalesPoint.builder()
                        .category((String) row[0])
                        .value((BigDecimal) row[1])
                        .build())
                .toList();

        List<DashboardDTOs.LowStockItem> lowStockItems = productRepository
                .findTop10ByIsDeletedFalseAndStockQuantityLessThanOrderByStockQuantityAsc(LOW_STOCK_THRESHOLD)
                .stream()
                .map(this::mapLowStock)
                .toList();

        List<DashboardDTOs.RecentOrderSummary> recentOrders = orderRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(this::mapRecentOrder)
                .toList();

        return DashboardDTOs.DashboardSummaryResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalCustomers(totalCustomers)
                .totalProducts(totalProducts)
                .pendingOrders(pendingOrders)
                .monthlyRevenueChart(monthlyRevenueChart)
                .categorySalesDistribution(categorySalesDistribution)
                .lowStockItems(lowStockItems)
                .recentOrders(recentOrders)
                .build();
    }

    private DashboardDTOs.LowStockItem mapLowStock(Product product) {
        String image = product.getImages() != null && !product.getImages().isEmpty()
                ? product.getImages().get(0).getImageUrl()
                : null;
        return DashboardDTOs.LowStockItem.builder()
                .id(product.getId())
                .title(product.getTitle())
                .stockQuantity(product.getStockQuantity())
                .image(image)
                .build();
    }

    private DashboardDTOs.RecentOrderSummary mapRecentOrder(Order order) {
        return DashboardDTOs.RecentOrderSummary.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomerName())
                .totalAmount(order.getTotalAmount())
                .status(order.getOrderStatus() != null ? order.getOrderStatus().name() : null)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
