package com.joshjewellery.backend.controller;

import com.joshjewellery.backend.dto.*;
import com.joshjewellery.backend.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "Protected administrative operations (ROLE_ADMIN only)")
public class AdminController {

    private final AdminDashboardService adminDashboardService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final CouponService couponService;
    private final ReviewService reviewService;
    private final CustomerAdminService customerAdminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard statistics and metrics")
    public ResponseEntity<DashboardDTOs.DashboardSummaryResponse> getDashboardSummary() {
        return ResponseEntity.ok(adminDashboardService.getDashboardSummary());
    }

    // Product Admin APIs
    @PostMapping("/products")
    @Operation(summary = "Create new product in catalog")
    public ResponseEntity<ProductDTOs.ProductResponse> createProduct(
            @RequestParam("product") String productJson,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {
        ProductDTOs.ProductRequest request = parseJson(productJson, ProductDTOs.ProductRequest.class);
        return ResponseEntity.ok(productService.createProduct(request, image));
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Update product details")
    public ResponseEntity<ProductDTOs.ProductResponse> updateProduct(
            @PathVariable UUID id,
            @RequestParam("product") String productJson,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {
        ProductDTOs.ProductRequest request = parseJson(productJson, ProductDTOs.ProductRequest.class);
        return ResponseEntity.ok(productService.updateProduct(id, request, image));
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Soft delete product")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    // Category Admin APIs
    @PostMapping("/categories")
    @Operation(summary = "Create new category")
    public ResponseEntity<CategoryDTOs.CategoryResponse> createCategory(
            @RequestParam("category") String categoryJson,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {
        CategoryDTOs.CategoryRequest request = parseJson(categoryJson, CategoryDTOs.CategoryRequest.class);
        return ResponseEntity.ok(categoryService.createCategory(request, image));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<CategoryDTOs.CategoryResponse> updateCategory(
            @PathVariable UUID id,
            @RequestParam("category") String categoryJson,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {
        CategoryDTOs.CategoryRequest request = parseJson(categoryJson, CategoryDTOs.CategoryRequest.class);
        return ResponseEntity.ok(categoryService.updateCategory(id, request, image));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }

    // Order Admin APIs
    @GetMapping("/orders")
    @Operation(summary = "List all customer orders for admin review")
    public ResponseEntity<List<OrderDTOs.OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }

    @PutMapping("/orders/{id}/status")
    @Operation(summary = "Update order status (e.g. CONFIRMED, SHIPPED, DELIVERED)")
    public ResponseEntity<OrderDTOs.OrderResponse> updateOrderStatus(@PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    // Coupon Admin APIs
    @GetMapping("/coupons")
    @Operation(summary = "List all discount coupons")
    public ResponseEntity<List<CouponDTOs.CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @PostMapping("/coupons")
    @Operation(summary = "Create new discount coupon")
    public ResponseEntity<CouponDTOs.CouponResponse> createCoupon(@Valid @RequestBody CouponDTOs.CouponRequest request) {
        return ResponseEntity.ok(couponService.createCoupon(request));
    }

    @PutMapping("/coupons/{id}")
    @Operation(summary = "Update discount coupon")
    public ResponseEntity<CouponDTOs.CouponResponse> updateCoupon(@PathVariable UUID id, @Valid @RequestBody CouponDTOs.CouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(id, request));
    }

    @PutMapping("/coupons/{id}/toggle-status")
    @Operation(summary = "Toggle coupon active status")
    public ResponseEntity<CouponDTOs.CouponResponse> toggleCouponStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(couponService.toggleCouponStatus(id));
    }

    @DeleteMapping("/coupons/{id}")
    @Operation(summary = "Delete discount coupon")
    public ResponseEntity<String> deleteCoupon(@PathVariable UUID id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok("Coupon deleted successfully");
    }

    // Review Admin APIs
    @GetMapping("/reviews")
    @Operation(summary = "List all customer reviews for moderation")
    public ResponseEntity<List<ReviewDTOs.ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviewsForAdmin());
    }

    @PutMapping("/reviews/{id}/approve")
    @Operation(summary = "Toggle review approval status")
    public ResponseEntity<ReviewDTOs.ReviewResponse> approveReview(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewService.approveReview(id));
    }

    @DeleteMapping("/reviews/{id}")
    @Operation(summary = "Delete a customer review")
    public ResponseEntity<String> deleteReview(@PathVariable UUID id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review deleted successfully");
    }

    // Customer Admin APIs
    @GetMapping("/customers")
    @Operation(summary = "List all registered customers with spend and loyalty summary")
    public ResponseEntity<List<CustomerDTOs.CustomerSummary>> getAllCustomers() {
        return ResponseEntity.ok(customerAdminService.getAllCustomers());
    }

    private <T> T parseJson(String json, Class<T> clazz) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, clazz);
        } catch (Exception e) {
            throw new com.joshjewellery.backend.exception.BadRequestException("Invalid request data: " + e.getMessage());
        }
    }
}
