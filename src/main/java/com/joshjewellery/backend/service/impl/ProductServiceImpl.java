package com.joshjewellery.backend.service.impl;

import com.joshjewellery.backend.dto.ProductDTOs;
import com.joshjewellery.backend.entity.Category;
import com.joshjewellery.backend.entity.Product;
import com.joshjewellery.backend.entity.ProductImage;
import com.joshjewellery.backend.exception.ResourceNotFoundException;
import com.joshjewellery.backend.repository.CategoryRepository;
import com.joshjewellery.backend.repository.ProductRepository;
import com.joshjewellery.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTOs.ProductResponse> getAllProducts(String category, String search, Pageable pageable) {
        boolean hasCategory = category != null && !category.isBlank();
        boolean hasSearch = search != null && !search.isBlank();

        if (!hasCategory && !hasSearch) {
            return productRepository.findByIsDeletedFalse(pageable).map(this::mapToResponse);
        }

        return productRepository.filterProducts(
                hasCategory ? category : null,
                hasSearch ? search : null,
                pageable
        ).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTOs.ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));
        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTOs.ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductDTOs.ProductResponse createProduct(ProductDTOs.ProductRequest request, org.springframework.web.multipart.MultipartFile image) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        }

        String slug = request.getTitle().toLowerCase().replaceAll("[^a-z0-9]", "-").replaceAll("-+", "-");

        Product product = Product.builder()
                .title(request.getTitle())
                .slug(slug)
                .sku(request.getSku())
                .description(request.getDescription())
                .category(category)
                .price(request.getPrice())
                .salePrice(request.getSalePrice())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 10)
                .isBestSeller(request.isBestSeller())
                .isNewArrival(request.isNewArrival())
                .isFeatured(request.isFeatured())
                .build();

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImageFile(image);
            if (imageUrl != null) {
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .imageUrl(imageUrl)
                        .displayOrder(0)
                        .isPrimary(true)
                        .build();
                List<ProductImage> images = new ArrayList<>();
                images.add(productImage);
                product.setImages(images);
            }
        }

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTOs.ProductResponse updateProduct(UUID id, ProductDTOs.ProductRequest request, org.springframework.web.multipart.MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        product.setTitle(request.getTitle());
        product.setSku(request.getSku());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSalePrice(request.getSalePrice());
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : product.getStockQuantity());
        product.setBestSeller(request.isBestSeller());
        product.setNewArrival(request.isNewArrival());
        product.setFeatured(request.isFeatured());

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImageFile(image);
            if (imageUrl != null) {
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .imageUrl(imageUrl)
                        .displayOrder(0)
                        .isPrimary(true)
                        .build();
                if (product.getImages() != null) {
                    product.getImages().clear();
                    product.getImages().add(productImage);
                } else {
                    List<ProductImage> images = new ArrayList<>();
                    images.add(productImage);
                    product.setImages(images);
                }
            }
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTOs.ProductResponse> getBestSellers() {
        return productRepository.findByIsBestSellerTrueAndIsDeletedFalse().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTOs.ProductResponse> getNewArrivals() {
        return productRepository.findByIsNewArrivalTrueAndIsDeletedFalse().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTOs.ProductResponse> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrueAndIsDeletedFalse().stream().map(this::mapToResponse).toList();
    }

    private ProductDTOs.ProductResponse mapToResponse(Product product) {
        List<String> imageList = product.getImages() != null ?
                product.getImages().stream().map(ProductImage::getImageUrl).toList() : new ArrayList<>();

        return ProductDTOs.ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .slug(product.getSlug())
                .sku(product.getSku())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "Other")
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .stockQuantity(product.getStockQuantity())
                .isBestSeller(product.isBestSeller())
                .isNewArrival(product.isNewArrival())
                .isFeatured(product.isFeatured())
                .images(imageList)
                .build();
    }

    private String saveImageFile(org.springframework.web.multipart.MultipartFile image) {
        try {
            String uploadDir = "uploads/products/";
            java.io.File dir = new java.io.File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String originalName = image.getOriginalFilename();
            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + filename);
            java.nio.file.Files.copy(image.getInputStream(), path, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/products/" + filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
