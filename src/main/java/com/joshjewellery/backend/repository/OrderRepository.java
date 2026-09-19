package com.joshjewellery.backend.repository;

import com.joshjewellery.backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByRazorpayOrderId(String razorpayOrderId);
    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.paymentStatus = 'PAID'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = 'PENDING'")
    Long countPendingOrders();

    List<Order> findTop5ByOrderByCreatedAtDesc();

    @Query(value = "SELECT to_char(date_trunc('month', o.created_at), 'Mon YYYY') AS month, " +
            "COALESCE(SUM(o.total_amount), 0) AS sales, COUNT(o.id) AS orders " +
            "FROM orders o WHERE o.payment_status = 'PAID' AND o.created_at >= (CURRENT_DATE - INTERVAL '6 months') " +
            "GROUP BY date_trunc('month', o.created_at) ORDER BY date_trunc('month', o.created_at)",
            nativeQuery = true)
    List<Object[]> findMonthlyRevenue();

    @Query(value = "SELECT c.name AS category, COALESCE(SUM(oi.total_price), 0) AS value " +
            "FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN products p ON oi.product_id = p.id " +
            "JOIN categories c ON p.category_id = c.id " +
            "WHERE o.payment_status = 'PAID' " +
            "GROUP BY c.name ORDER BY value DESC",
            nativeQuery = true)
    List<Object[]> findCategorySalesDistribution();

    @Query(value = "SELECT o.user_id AS userId, COALESCE(SUM(o.total_amount), 0) AS total, COUNT(o.id) AS orders " +
            "FROM orders o WHERE o.payment_status = 'PAID' AND o.user_id IS NOT NULL GROUP BY o.user_id",
            nativeQuery = true)
    List<Object[]> aggregateSpendByUser();
}
