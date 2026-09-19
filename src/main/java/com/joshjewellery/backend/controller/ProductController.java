package com.joshjewellery.backend.controller;

import com.joshjewellery.backend.dto.ProductDTOs;
import com.joshjewellery.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Public catalog API for products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get filtered list of products with pagination")
    public ResponseEntity<Page<ProductDTOs.ProductResponse>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getAllProducts(category, search, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details by UUID")
    public ResponseEntity<ProductDTOs.ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product details by URL slug")
    public ResponseEntity<ProductDTOs.ProductResponse> getProductBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getProductBySlug(slug));
    }

    @GetMapping("/bestsellers")
    @Operation(summary = "Get best selling products")
    public ResponseEntity<List<ProductDTOs.ProductResponse>> getBestSellers() {
        return ResponseEntity.ok(productService.getBestSellers());
    }

    @GetMapping("/new-arrivals")
    @Operation(summary = "Get newly arrived products")
    public ResponseEntity<List<ProductDTOs.ProductResponse>> getNewArrivals() {
        return ResponseEntity.ok(productService.getNewArrivals());
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured product collections")
    public ResponseEntity<List<ProductDTOs.ProductResponse>> getFeaturedProducts() {
        return ResponseEntity.ok(productService.getFeaturedProducts());
    }
}
