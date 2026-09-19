package com.joshjewellery.backend.service;

import com.joshjewellery.backend.dto.ProductDTOs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Page<ProductDTOs.ProductResponse> getAllProducts(String category, String search, Pageable pageable);
    ProductDTOs.ProductResponse getProductBySlug(String slug);
    ProductDTOs.ProductResponse getProductById(UUID id);
    ProductDTOs.ProductResponse createProduct(ProductDTOs.ProductRequest request, org.springframework.web.multipart.MultipartFile image);
    ProductDTOs.ProductResponse updateProduct(UUID id, ProductDTOs.ProductRequest request, org.springframework.web.multipart.MultipartFile image);
    void deleteProduct(UUID id);
    List<ProductDTOs.ProductResponse> getBestSellers();
    List<ProductDTOs.ProductResponse> getNewArrivals();
    List<ProductDTOs.ProductResponse> getFeaturedProducts();
}
