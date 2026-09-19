package com.joshjewellery.backend.service.impl;

import com.joshjewellery.backend.dto.ProductDTOs;
import com.joshjewellery.backend.entity.Product;
import com.joshjewellery.backend.entity.User;
import com.joshjewellery.backend.entity.Wishlist;
import com.joshjewellery.backend.exception.ResourceNotFoundException;
import com.joshjewellery.backend.repository.ProductRepository;
import com.joshjewellery.backend.repository.UserRepository;
import com.joshjewellery.backend.repository.WishlistRepository;
import com.joshjewellery.backend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTOs.ProductResponse> getUserWishlist(UUID userId) {
        return wishlistRepository.findByUserId(userId).stream()
                .map(w -> mapProductResponse(w.getProduct()))
                .toList();
    }

    @Override
    @Transactional
    public void addToWishlist(UUID userId, UUID productId) {
        if (wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();

        wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(UUID userId, UUID productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    private ProductDTOs.ProductResponse mapProductResponse(Product product) {
        return ProductDTOs.ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .slug(product.getSlug())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .images(product.getImages().stream().map(i -> i.getImageUrl()).toList())
                .build();
    }
}
