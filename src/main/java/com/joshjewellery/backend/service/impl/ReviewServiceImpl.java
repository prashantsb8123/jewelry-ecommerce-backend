package com.joshjewellery.backend.service.impl;

import com.joshjewellery.backend.dto.ReviewDTOs;
import com.joshjewellery.backend.entity.Product;
import com.joshjewellery.backend.entity.Review;
import com.joshjewellery.backend.exception.ResourceNotFoundException;
import com.joshjewellery.backend.repository.ProductRepository;
import com.joshjewellery.backend.repository.ReviewRepository;
import com.joshjewellery.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTOs.ReviewResponse> getApprovedReviewsForProduct(UUID productId) {
        return reviewRepository.findByProductIdAndIsApprovedTrueOrderByCreatedAtDesc(productId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public ReviewDTOs.ReviewResponse submitReview(ReviewDTOs.ReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        Review review = Review.builder()
                .product(product)
                .reviewerName(request.getReviewerName().trim())
                .rating(request.getRating())
                .comment(request.getComment())
                .isApproved(false)
                .build();
        Review saved = reviewRepository.save(review);

        product.setReviewCount((product.getReviewCount() != null ? product.getReviewCount() : 0));
        productRepository.save(product);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTOs.ReviewResponse> getAllReviewsForAdmin() {
        return reviewRepository.findAll().stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReviewDTOs.ReviewResponse approveReview(UUID id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        review.setApproved(!review.isApproved());
        Review saved = reviewRepository.save(review);
        recalculateProductRating(saved.getProduct().getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteReview(UUID id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        UUID productId = review.getProduct().getId();
        reviewRepository.deleteById(id);
        recalculateProductRating(productId);
    }

    private void recalculateProductRating(UUID productId) {
        List<Review> approved = reviewRepository.findByProductIdAndIsApprovedTrueOrderByCreatedAtDesc(productId);
        productRepository.findById(productId).ifPresent(product -> {
            product.setReviewCount(approved.size());
            if (!approved.isEmpty()) {
                double avg = approved.stream().mapToInt(Review::getRating).average().orElse(product.getRating().doubleValue());
                product.setRating(java.math.BigDecimal.valueOf(avg).setScale(2, java.math.RoundingMode.HALF_UP));
            }
            productRepository.save(product);
        });
    }

    private ReviewDTOs.ReviewResponse mapToResponse(Review review) {
        return ReviewDTOs.ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                .productTitle(review.getProduct() != null ? review.getProduct().getTitle() : null)
                .reviewerName(review.getReviewerName())
                .rating(review.getRating())
                .comment(review.getComment())
                .isApproved(review.isApproved())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
