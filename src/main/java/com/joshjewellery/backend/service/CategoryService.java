package com.joshjewellery.backend.service;

import com.joshjewellery.backend.dto.CategoryDTOs;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryDTOs.CategoryResponse> getAllCategories();
    CategoryDTOs.CategoryResponse getCategoryBySlug(String slug);
    CategoryDTOs.CategoryResponse createCategory(CategoryDTOs.CategoryRequest request, MultipartFile image);
    CategoryDTOs.CategoryResponse updateCategory(UUID id, CategoryDTOs.CategoryRequest request, MultipartFile image);
    void deleteCategory(UUID id);
}
