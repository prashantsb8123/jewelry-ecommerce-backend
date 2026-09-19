package com.joshjewellery.backend.repository;

import com.joshjewellery.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByProductIdAndIsApprovedTrueOrderByCreatedAtDesc(UUID productId);
    List<Review> findByIsApprovedFalse();
}
