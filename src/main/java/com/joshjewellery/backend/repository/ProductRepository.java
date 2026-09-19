package com.joshjewellery.backend.repository;

import com.joshjewellery.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findBySlugAndIsDeletedFalse(String slug);
    Page<Product> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND " +
           "(cast(:category as string) IS NULL OR LOWER(p.category.slug) = LOWER(cast(:category as string))) AND " +
           "(cast(:search as string) IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', cast(:search as string), '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', cast(:search as string), '%')))")
    Page<Product> filterProducts(
        @Param("category") String category,
        @Param("search") String search,
        Pageable pageable
    );

    List<Product> findTop10ByIsDeletedFalseAndStockQuantityLessThanOrderByStockQuantityAsc(Integer threshold);

    List<Product> findByIsBestSellerTrueAndIsDeletedFalse();
    List<Product> findByIsNewArrivalTrueAndIsDeletedFalse();
    List<Product> findByIsFeaturedTrueAndIsDeletedFalse();
}
