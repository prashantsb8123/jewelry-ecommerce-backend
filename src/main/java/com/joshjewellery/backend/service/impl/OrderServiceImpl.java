package com.joshjewellery.backend.service.impl;

import com.joshjewellery.backend.constant.OrderStatus;
import com.joshjewellery.backend.constant.PaymentMethod;
import com.joshjewellery.backend.constant.PaymentStatus;
import com.joshjewellery.backend.constant.ReturnType;
import com.joshjewellery.backend.dto.OrderDTOs;
import com.joshjewellery.backend.entity.*;
import com.joshjewellery.backend.exception.BadRequestException;
import com.joshjewellery.backend.exception.ResourceNotFoundException;
import com.joshjewellery.backend.repository.*;
import com.joshjewellery.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final com.razorpay.RazorpayClient razorpayClient;

    @org.springframework.beans.factory.annotation.Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @org.springframework.beans.factory.annotation.Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    @org.springframework.beans.factory.annotation.Value("${razorpay.webhook-secret:}")
    private String razorpayWebhookSecret;

    @Override
    @Transactional
    public OrderDTOs.OrderResponse createOrder(UUID userId, OrderDTOs.CreateOrderRequest request) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderDTOs.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .filter(p -> !p.isDeleted())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemRequest.getProductId()));

            if (product.getStockQuantity() == null || product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new BadRequestException("Insufficient stock for \"" + product.getTitle() + "\"");
            }

            BigDecimal unitPrice = (product.getSalePrice() != null && product.getSalePrice().compareTo(product.getPrice()) < 0)
                    ? product.getSalePrice()
                    : product.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            String imageUrl = (product.getImages() != null && !product.getImages().isEmpty())
                    ? product.getImages().get(0).getImageUrl() : null;

            orderItems.add(OrderItem.builder()
                    .product(product)
                    .title(product.getTitle())
                    .imageUrl(imageUrl)
                    .unitPrice(unitPrice)
                    .quantity(itemRequest.getQuantity())
                    .totalPrice(lineTotal)
                    .build());
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(request.getCouponCode()).orElse(null);
            if (coupon != null && (coupon.getMinOrderValue() == null || subtotal.compareTo(coupon.getMinOrderValue()) >= 0)) {
                discountAmount = subtotal.multiply(coupon.getDiscountPercentage()).divide(new BigDecimal("100"));
                if (coupon.getMaxDiscount() != null && discountAmount.compareTo(coupon.getMaxDiscount()) > 0) {
                    discountAmount = coupon.getMaxDiscount();
                }
            }
        }

        BigDecimal taxAmount = subtotal.subtract(discountAmount).multiply(new BigDecimal("0.03"));
        BigDecimal shippingAmount = BigDecimal.ZERO; // Free shipping
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(taxAmount).add(shippingAmount);

        String orderNumber = "ORD-" + System.currentTimeMillis();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .user(user)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .taxAmount(taxAmount)
                .shippingAmount(shippingAmount)
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.RAZORPAY)
                .items(new ArrayList<>())
                .build();

        for (OrderItem item : orderItems) {
            item.setOrder(order);
            order.getItems().add(item);
        }

        Order savedOrder = orderRepository.save(order);

        if (savedOrder.getPaymentMethod() == PaymentMethod.RAZORPAY) {
            try {
                org.json.JSONObject razorpayOrderRequest = new org.json.JSONObject();
                long amountInPaise = savedOrder.getTotalAmount()
                        .multiply(new BigDecimal("100"))
                        .setScale(0, java.math.RoundingMode.HALF_UP)
                        .longValueExact();
                razorpayOrderRequest.put("amount", amountInPaise);
                razorpayOrderRequest.put("currency", "INR");
                razorpayOrderRequest.put("receipt", savedOrder.getOrderNumber());
                com.razorpay.Order razorpayOrder = razorpayClient.orders.create(razorpayOrderRequest);
                savedOrder.setRazorpayOrderId(razorpayOrder.get("id"));
                savedOrder = orderRepository.save(savedOrder);
            } catch (Exception e) {
                // Razorpay order creation failed (e.g. real API keys not configured yet).
                // The order still exists as PENDING; the frontend surfaces a friendly message
                // and the customer can retry or switch to Cash on Delivery instead.
                e.printStackTrace();
            }
        }

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderDTOs.OrderResponse verifyPayment(UUID orderId, OrderDTOs.VerifyPaymentRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        boolean signatureValid = false;
        try {
            String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(
                    razorpayKeySecret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            signatureValid = hex.toString().equals(request.getRazorpaySignature());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!signatureValid) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);
            throw new BadRequestException("Payment verification failed. Please try again or contact support.");
        }

        order.setRazorpayPaymentId(request.getRazorpayPaymentId());
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.CONFIRMED);
        Order updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    @Override
    @Transactional
    public void handleRazorpayWebhook(String payload, String signature) {
        if (razorpayWebhookSecret == null || razorpayWebhookSecret.isBlank()) {
            return; // Webhook secret not configured yet; ignore until RAZORPAY_WEBHOOK_SECRET is set.
        }
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(
                    razorpayWebhookSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            if (signature == null || !hex.toString().equals(signature)) {
                return; // Invalid signature; ignore silently.
            }

            org.json.JSONObject event = new org.json.JSONObject(payload);
            String eventType = event.optString("event", "");
            org.json.JSONObject payloadObj = event.optJSONObject("payload");
            org.json.JSONObject paymentWrapper = payloadObj != null ? payloadObj.optJSONObject("payment") : null;
            org.json.JSONObject paymentEntity = paymentWrapper != null ? paymentWrapper.optJSONObject("entity") : null;
            if (paymentEntity == null) {
                return;
            }
            String razorpayOrderId = paymentEntity.optString("order_id", null);
            String razorpayPaymentId = paymentEntity.optString("id", null);
            if (razorpayOrderId == null) {
                return;
            }

            orderRepository.findByRazorpayOrderId(razorpayOrderId).ifPresent(order -> {
                if ("payment.captured".equals(eventType)) {
                    order.setPaymentStatus(PaymentStatus.PAID);
                    order.setOrderStatus(OrderStatus.CONFIRMED);
                    order.setRazorpayPaymentId(razorpayPaymentId);
                    orderRepository.save(order);
                } else if ("payment.failed".equals(eventType)) {
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    orderRepository.save(order);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTOs.OrderResponse> getUserOrders(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTOs.OrderResponse getOrderById(UUID userId, UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTOs.OrderResponse> getAllOrdersForAdmin() {
        return orderRepository.findAll().stream().map(this::mapToOrderResponse).toList();
    }

    @Override
    @Transactional
    public OrderDTOs.OrderResponse updateOrderStatus(UUID orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        order.setOrderStatus(newStatus);
        if ("DELIVERED".equalsIgnoreCase(status)) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        if (newStatus == OrderStatus.RETURNED) {
            // Restock every item in the order now that it has been physically returned.
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product != null) {
                    int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
                    product.setStockQuantity(currentStock + item.getQuantity());
                    productRepository.save(product);
                }
            }
            if (order.getReturnType() == ReturnType.RETURN) {
                order.setPaymentStatus(PaymentStatus.REFUNDED);
            }
        }

        Order updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    @Override
    @Transactional
    public OrderDTOs.OrderResponse requestReturn(UUID userId, UUID orderId, OrderDTOs.ReturnRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("Only delivered orders are eligible for a return or exchange");
        }

        order.setOrderStatus(OrderStatus.RETURN_REQUESTED);
        order.setReturnType(request.getReturnType());
        order.setReturnReason(request.getReason());

        Order updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    private OrderDTOs.OrderResponse mapToOrderResponse(Order order) {
        List<OrderDTOs.OrderItemResponse> itemResponses = order.getItems().stream().map(item ->
                OrderDTOs.OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .title(item.getTitle())
                        .image(item.getImageUrl())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getTotalPrice())
                        .build()
        ).toList();

        return OrderDTOs.OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .addressLine1(order.getAddressLine1())
                .addressLine2(order.getAddressLine2())
                .city(order.getCity())
                .state(order.getState())
                .pincode(order.getPincode())
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .taxAmount(order.getTaxAmount())
                .shippingAmount(order.getShippingAmount())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .trackingNumber(order.getTrackingNumber())
                .razorpayOrderId(order.getRazorpayOrderId())
                .razorpayKeyId(order.getPaymentMethod() == PaymentMethod.RAZORPAY ? razorpayKeyId : null)
                .returnType(order.getReturnType())
                .returnReason(order.getReturnReason())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }
}
