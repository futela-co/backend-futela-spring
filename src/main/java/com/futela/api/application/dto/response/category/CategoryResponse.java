package com.futela.api.application.dto.response.category;

import com.futela.api.domain.model.property.Category;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        String description,
        String icon,
        boolean isActive,
        Instant createdAt
) {
    public static CategoryResponse fromDomain(Category category) {
        return new CategoryResponse(
                category.id(), category.name(), category.slug(),
                category.description(), category.icon(),
                category.isActive(), category.createdAt()
        );
    }
}
