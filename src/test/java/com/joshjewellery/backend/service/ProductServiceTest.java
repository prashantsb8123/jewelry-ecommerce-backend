package com.joshjewellery.backend.service;

import com.joshjewellery.backend.dto.ProductDTOs;
import com.joshjewellery.backend.entity.Product;
import com.joshjewellery.backend.exception.ResourceNotFoundException;
import com.joshjewellery.backend.repository.CategoryRepository;
import com.joshjewellery.backend.repository.ProductRepository;
import com.joshjewellery.backend.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testGetProductBySlugSuccess() {
        String slug = "royal-solitaire-diamond-ring";
        Product mockProduct = Product.builder()
                .id(UUID.randomUUID())
                .title("Royal Solitaire Diamond Ring")
                .slug(slug)
                .sku("RNG-001")
                .price(new BigDecimal("78500"))
                .isDeleted(false)
                .build();

        when(productRepository.findBySlugAndIsDeletedFalse(slug)).thenReturn(Optional.of(mockProduct));

        ProductDTOs.ProductResponse response = productService.getProductBySlug(slug);

        assertNotNull(response);
        assertEquals("Royal Solitaire Diamond Ring", response.getTitle());
        assertEquals("RNG-001", response.getSku());
    }

    @Test
    void testGetProductBySlugNotFound() {
        String slug = "non-existent-ring";
        when(productRepository.findBySlugAndIsDeletedFalse(slug)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductBySlug(slug));
    }
}
