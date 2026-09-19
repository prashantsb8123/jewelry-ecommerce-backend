package com.joshjewellery.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class CategoryDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CategoryRequest {
        @NotBlank(message = "Name is required")
        private String name;
        private String description;
        private String imageUrl;
        private UUID parentId;
        @JsonProperty("isFeatured")
        private boolean isFeatured = false;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponse {
        private UUID id;
        private String name;
        private String slug;
        private String description;
        private String imageUrl;
        @JsonProperty("isFeatured")
        private boolean isFeatured;
    }
}
