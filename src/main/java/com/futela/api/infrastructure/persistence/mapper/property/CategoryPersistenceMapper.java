package com.futela.api.infrastructure.persistence.mapper.property;

import com.futela.api.domain.model.property.Category;
import com.futela.api.infrastructure.persistence.entity.property.CategoryEntity;

public final class CategoryPersistenceMapper {

    private CategoryPersistenceMapper() {}

    public static Category toDomain(CategoryEntity entity) {
        return new Category(
                entity.getId(), entity.getName(), entity.getSlug(),
                entity.getDescription(), entity.getIcon(),
                entity.isActive(),
                entity.getCompany() != null ? entity.getCompany().getId() : null,
                entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }
}
