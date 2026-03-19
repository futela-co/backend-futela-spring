package com.futela.api.domain.port.out.property;

import com.futela.api.domain.model.property.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    List<Category> findAllByCompanyId(UUID companyId);
    Optional<Category> findById(UUID id);
    Category save(Category category);
    void softDelete(UUID id);
    boolean existsBySlug(String slug);
}
