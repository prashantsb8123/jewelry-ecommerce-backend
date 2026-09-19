package com.joshjewellery.backend.service;

import com.joshjewellery.backend.dto.OrderDTOs;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDTOs.OrderResponse createOrder(UUID userId, OrderDTOs.CreateOrderRequest request);
    List<OrderDTOs.OrderResponse> getUserOrders(UUID userId);
    OrderDTOs.OrderResponse getOrderById(UUID userId, UUID orderId);
    List<OrderDTOs.OrderResponse> getAllOrdersForAdmin();
    OrderDTOs.OrderResponse updateOrderStatus(UUID orderId, String status);
    OrderDTOs.OrderResponse verifyPayment(UUID orderId, OrderDTOs.VerifyPaymentRequest request);
    void handleRazorpayWebhook(String payload, String signature);
    OrderDTOs.OrderResponse requestReturn(UUID userId, UUID orderId, OrderDTOs.ReturnRequest request);
}
