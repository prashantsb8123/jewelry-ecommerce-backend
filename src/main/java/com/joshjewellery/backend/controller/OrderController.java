package com.joshjewellery.backend.controller;

import com.joshjewellery.backend.dto.OrderDTOs;
import com.joshjewellery.backend.exception.BadRequestException;
import com.joshjewellery.backend.security.UserPrincipal;
import com.joshjewellery.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and order tracking endpoints (supports guest checkout)")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order (works for both signed-in customers and guests)")
    public ResponseEntity<OrderDTOs.OrderResponse> createOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody OrderDTOs.CreateOrderRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get signed-in user's order history")
    public ResponseEntity<List<OrderDTOs.OrderResponse>> getUserOrders(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new BadRequestException("Authentication required to view order history");
        }
        return ResponseEntity.ok(orderService.getUserOrders(principal.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific order details (used for order confirmation, incl. guest orders)")
    public ResponseEntity<OrderDTOs.OrderResponse> getOrderById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {
        UUID userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(orderService.getOrderById(userId, id));
    }

    @PostMapping("/{id}/verify-payment")
    @Operation(summary = "Verify a Razorpay payment signature after checkout completes (supports guest checkout)")
    public ResponseEntity<OrderDTOs.OrderResponse> verifyPayment(
            @PathVariable UUID id,
            @Valid @RequestBody OrderDTOs.VerifyPaymentRequest request) {
        return ResponseEntity.ok(orderService.verifyPayment(id, request));
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Request a return or exchange for a delivered order (supports guest checkout)")
    public ResponseEntity<OrderDTOs.OrderResponse> requestReturn(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody OrderDTOs.ReturnRequest request) {
        UUID userId = principal != null ? principal.getId() : null;
        return ResponseEntity.ok(orderService.requestReturn(userId, id, request));
    }
}
