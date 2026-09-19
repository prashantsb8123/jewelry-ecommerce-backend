package com.joshjewellery.backend.service;

import com.joshjewellery.backend.dto.ReviewDTOs;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    List<ReviewDTOs.ReviewResponse> getApprovedReviewsForProduct(UUID productId);
    ReviewDTOs.ReviewResponse submitReview(ReviewDTOs.ReviewRequest request);
    List<ReviewDTOs.ReviewResponse> getAllReviewsForAdmin();
    ReviewDTOs.ReviewResponse approveReview(UUID id);
    void deleteReview(UUID id);
}
