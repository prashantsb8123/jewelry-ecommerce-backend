package com.joshjewellery.backend.service;

import com.joshjewellery.backend.dto.ProductDTOs;

import java.util.List;
import java.util.UUID;

public interface WishlistService {
    List<ProductDTOs.ProductResponse> getUserWishlist(UUID userId);
    void addToWishlist(UUID userId, UUID productId);
    void removeFromWishlist(UUID userId, UUID productId);
}
