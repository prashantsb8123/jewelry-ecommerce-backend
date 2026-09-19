package com.joshjewellery.backend.controller;

import com.joshjewellery.backend.dto.ReviewDTOs;
import com.joshjewellery.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Public product review submission and listing")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get approved reviews for a product")
    public ResponseEntity<List<ReviewDTOs.ReviewResponse>> getReviewsForProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(reviewService.getApprovedReviewsForProduct(productId));
    }

    @PostMapping
    @Operation(summary = "Submit a new product review (pending admin approval)")
    public ResponseEntity<ReviewDTOs.ReviewResponse> submitReview(@Valid @RequestBody ReviewDTOs.ReviewRequest request) {
        return ResponseEntity.ok(reviewService.submitReview(request));
    }
}
