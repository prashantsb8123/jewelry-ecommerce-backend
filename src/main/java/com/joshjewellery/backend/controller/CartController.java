package com.joshjewellery.backend.controller;

import com.joshjewellery.backend.dto.CartDTOs;
import com.joshjewellery.backend.security.UserPrincipal;
import com.joshjewellery.backend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart operations for authenticated customers")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get user shopping cart")
    public ResponseEntity<CartDTOs.CartResponse> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(cartService.getCartForUser(principal.getId()));
    }

    @PostMapping
    @Operation(summary = "Add jewellery product to shopping cart")
    public ResponseEntity<CartDTOs.CartResponse> addToCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CartDTOs.AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(principal.getId(), request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<CartDTOs.CartResponse> updateQuantity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(principal.getId(), itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<CartDTOs.CartResponse> removeItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID itemId) {
        return ResponseEntity.ok(cartService.removeCartItem(principal.getId(), itemId));
    }

    @DeleteMapping
    @Operation(summary = "Clear shopping cart")
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clearCart(principal.getId());
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
