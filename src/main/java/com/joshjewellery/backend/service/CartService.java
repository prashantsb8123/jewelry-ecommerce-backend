package com.joshjewellery.backend.service;

import com.joshjewellery.backend.dto.CartDTOs;

import java.util.UUID;

public interface CartService {
    CartDTOs.CartResponse getCartForUser(UUID userId);
    CartDTOs.CartResponse addToCart(UUID userId, CartDTOs.AddToCartRequest request);
    CartDTOs.CartResponse updateCartItemQuantity(UUID userId, UUID itemId, Integer quantity);
    CartDTOs.CartResponse removeCartItem(UUID userId, UUID itemId);
    void clearCart(UUID userId);
}
