package com.joshjewellery.backend.controller;

import com.joshjewellery.backend.dto.ProductDTOs;
import com.joshjewellery.backend.security.UserPrincipal;
import com.joshjewellery.backend.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Customer wishlist management")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get user wishlist")
    public ResponseEntity<List<ProductDTOs.ProductResponse>> getWishlist(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(wishlistService.getUserWishlist(principal.getId()));
    }

    @PostMapping("/{productId}")
    @Operation(summary = "Add product to wishlist")
    public ResponseEntity<String> addToWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID productId) {
        wishlistService.addToWishlist(principal.getId(), productId);
        return ResponseEntity.ok("Added to wishlist");
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove product from wishlist")
    public ResponseEntity<String> removeFromWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID productId) {
        wishlistService.removeFromWishlist(principal.getId(), productId);
        return ResponseEntity.ok("Removed from wishlist");
    }
}
