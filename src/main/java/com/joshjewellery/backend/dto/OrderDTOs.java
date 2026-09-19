package com.joshjewellery.backend.dto;

import com.joshjewellery.backend.constant.OrderStatus;
import com.joshjewellery.backend.constant.PaymentMethod;
import com.joshjewellery.backend.constant.PaymentStatus;
import com.joshjewellery.backend.constant.ReturnType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private UUID productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnRequest {
        @NotNull(message = "Return type is required")
        private ReturnType returnType;

        @NotBlank(message = "Please tell us the reason")
        private String reason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyPaymentRequest {
        @NotBlank(message = "Razorpay order ID is required")
        private String razorpayOrderId;

        @NotBlank(message = "Razorpay payment ID is required")
        private String razorpayPaymentId;

        @NotBlank(message = "Razorpay signature is required")
        private String razorpaySignature;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderRequest {
        @NotBlank(message = "Customer name is required")
        private String customerName;

        @Email(message = "A valid email is required")
        private String customerEmail;

        @NotBlank(message = "Customer phone is required")
        private String customerPhone;

        @NotBlank(message = "Address line is required")
        private String addressLine1;

        private String addressLine2;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "Pincode is required")
        private String pincode;

        private PaymentMethod paymentMethod = PaymentMethod.RAZORPAY;
        private String couponCode;

        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        private List<OrderItemRequest> items;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private UUID id;
        private UUID productId;
        private String title;
        private String image;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal totalPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderResponse {
        private UUID id;
        private String orderNumber;
        private UUID userId;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String pincode;
        private BigDecimal subtotal;
        private BigDecimal discountAmount;
        private BigDecimal taxAmount;
        private BigDecimal shippingAmount;
        private BigDecimal totalAmount;
        private OrderStatus orderStatus;
        private PaymentStatus paymentStatus;
        private PaymentMethod paymentMethod;
        private String trackingNumber;
        private String razorpayOrderId;
        private String razorpayKeyId;
        private ReturnType returnType;
        private String returnReason;
        private LocalDateTime createdAt;
        private List<OrderItemResponse> items;
    }
}
