package com.futela.api.infrastructure.persistence.adapter.property;

import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.property.Category;
import com.futela.api.domain.port.out.property.CategoryRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.property.CategoryEntity;
import com.futela.api.infrastructure.persistence.mapper.property.CategoryPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.property.JpaCategoryRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final JpaCategoryRepository jpaRepository;
    private final EntityManager entityManager;

    public CategoryRepositoryAdapter(JpaCategoryRepository jpaRepository, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public List<Category> findAllByCompanyId(UUID companyId) {
        return jpaRepository.findByCompanyIdAndDeletedAtIsNullOrderByNameAsc(companyId).stream()
                .map(CategoryPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(CategoryPersistenceMapper::toDomain);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity;
        if (category.id() != null) {
            entity = jpaRepository.findById(category.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie", category.id().toString()));
        } else {
            entity = new CategoryEntity();
            CompanyEntity company = entityManager.getReference(CompanyEntity.class, category.companyId());
            entity.setCompany(company);
        }
        entity.setName(category.name());
        entity.setSlug(category.slug());
        entity.setDescription(category.description());
        entity.setIcon(category.icon());
        entity.setActive(category.isActive());

        return CategoryPersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void softDelete(UUID id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.softDelete();
            jpaRepository.save(entity);
        });
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }
}
