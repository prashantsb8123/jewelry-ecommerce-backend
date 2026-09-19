package com.joshjewellery.backend.service.impl;

import com.joshjewellery.backend.dto.CartDTOs;
import com.joshjewellery.backend.dto.ProductDTOs;
import com.joshjewellery.backend.entity.Cart;
import com.joshjewellery.backend.entity.CartItem;
import com.joshjewellery.backend.entity.Product;
import com.joshjewellery.backend.entity.User;
import com.joshjewellery.backend.exception.ResourceNotFoundException;
import com.joshjewellery.backend.repository.CartRepository;
import com.joshjewellery.backend.repository.ProductRepository;
import com.joshjewellery.backend.repository.UserRepository;
import com.joshjewellery.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartDTOs.CartResponse getCartForUser(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartDTOs.CartResponse addToCart(UUID userId, CartDTOs.AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart updated = cartRepository.save(cart);
        return mapToCartResponse(updated);
    }

    @Override
    @Transactional
    public CartDTOs.CartResponse updateCartItemQuantity(UUID userId, UUID itemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(item -> {
            if (item.getId().equals(itemId)) {
                if (quantity <= 0) return true;
                item.setQuantity(quantity);
            }
            return false;
        });
        Cart updated = cartRepository.save(cart);
        return mapToCartResponse(updated);
    }

    @Override
    @Transactional
    public CartDTOs.CartResponse removeCartItem(UUID userId, UUID itemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        Cart updated = cartRepository.save(cart);
        return mapToCartResponse(updated);
    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    return cartRepository.save(Cart.builder().user(user).items(new ArrayList<>()).build());
                });
    }

    private CartDTOs.CartResponse mapToCartResponse(Cart cart) {
        List<CartDTOs.CartItemResponse> itemResponses = cart.getItems().stream().map(item -> {
            BigDecimal unitPrice = effectivePrice(item.getProduct());
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            return CartDTOs.CartItemResponse.builder()
                    .id(item.getId())
                    .product(mapProductResponse(item.getProduct()))
                    .quantity(item.getQuantity())
                    .itemTotal(itemTotal)
                    .build();
        }).toList();

        BigDecimal subtotal = itemResponses.stream()
                .map(CartDTOs.CartItemResponse::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal estimatedGst = subtotal.multiply(new BigDecimal("0.03"));
        BigDecimal grandTotal = subtotal.add(estimatedGst);

        return CartDTOs.CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .subtotal(subtotal)
                .estimatedGst(estimatedGst)
                .grandTotal(grandTotal)
                .build();
    }

    private BigDecimal effectivePrice(Product product) {
        return (product.getSalePrice() != null && product.getSalePrice().compareTo(product.getPrice()) < 0)
                ? product.getSalePrice()
                : product.getPrice();
    }

    private ProductDTOs.ProductResponse mapProductResponse(Product product) {
        return ProductDTOs.ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .slug(product.getSlug())
                .sku(product.getSku())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .images(product.getImages().stream().map(i -> i.getImageUrl()).toList())
                .build();
    }
}
