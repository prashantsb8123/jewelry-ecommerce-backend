package com.joshjewellery.backend.service.impl;

import com.joshjewellery.backend.dto.CategoryDTOs;
import com.joshjewellery.backend.entity.Category;
import com.joshjewellery.backend.exception.ResourceNotFoundException;
import com.joshjewellery.backend.repository.CategoryRepository;
import com.joshjewellery.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTOs.CategoryResponse> getAllCategories() {
        return categoryRepository.findByIsDeletedFalse().stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTOs.CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public CategoryDTOs.CategoryResponse createCategory(CategoryDTOs.CategoryRequest request, org.springframework.web.multipart.MultipartFile image) {
        String slug = request.getName().toLowerCase().replaceAll("[^a-z0-9]", "-").replaceAll("-+", "-");

        String imageUrl = request.getImageUrl();
        if (image != null && !image.isEmpty()) {
            String saved = saveImageFile(image);
            if (saved != null) {
                imageUrl = saved;
            }
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .imageUrl(imageUrl)
                .isFeatured(request.isFeatured())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTOs.CategoryResponse updateCategory(UUID id, CategoryDTOs.CategoryRequest request, org.springframework.web.multipart.MultipartFile image) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setFeatured(request.isFeatured());

        if (image != null && !image.isEmpty()) {
            String saved = saveImageFile(image);
            if (saved != null) {
                category.setImageUrl(saved);
            }
        }

        Category updated = categoryRepository.save(category);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setDeleted(true);
        categoryRepository.save(category);
    }

    private CategoryDTOs.CategoryResponse mapToResponse(Category category) {
        return CategoryDTOs.CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .isFeatured(category.isFeatured())
                .build();
    }

    private String saveImageFile(org.springframework.web.multipart.MultipartFile image) {
        try {
            String uploadDir = "uploads/categories/";
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
            return "/uploads/categories/" + filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
