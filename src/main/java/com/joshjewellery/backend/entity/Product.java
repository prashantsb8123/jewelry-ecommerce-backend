package com.joshjewellery.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true, length = 280)
    private String slug;

    @Column(unique = true, length = 100)
    private String sku;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "sale_price", precision = 12, scale = 2)
    private BigDecimal salePrice;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating = new BigDecimal("4.9");

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 10;

    @Column(name = "is_best_seller")
    private boolean isBestSeller = false;

    @Column(name = "is_new_arrival")
    private boolean isNewArrival = false;

    @Column(name = "is_featured")
    private boolean isFeatured = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();
}
