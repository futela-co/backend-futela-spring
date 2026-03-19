package com.futela.api.infrastructure.persistence.repository.property;

import com.futela.api.infrastructure.persistence.entity.property.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {
    List<CategoryEntity> findByCompanyIdAndDeletedAtIsNullOrderByNameAsc(UUID companyId);
    boolean existsBySlug(String slug);
}
