package com.futela.api.infrastructure.persistence.adapter.property;

import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.common.PageResult;
import com.futela.api.domain.model.property.Property;
import com.futela.api.domain.port.out.property.PropertyRepositoryPort;
import com.futela.api.domain.port.out.property.PropertySearchCriteria;
import com.futela.api.infrastructure.persistence.entity.property.*;
import com.futela.api.infrastructure.persistence.mapper.property.PropertyPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.property.JpaPropertyRepository;
import com.futela.api.infrastructure.persistence.specification.PropertySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PropertyRepositoryAdapter implements PropertyRepositoryPort {

    private final JpaPropertyRepository jpaRepository;

    public PropertyRepositoryAdapter(JpaPropertyRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Property save(Property property) {
        // For save, the caller must use the entity manager directly or the use case handles entity creation
        throw new UnsupportedOperationException("Use saveEntity instead for property creation/update");
    }

    public PropertyEntity saveEntity(PropertyEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public Optional<Property> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(PropertyPersistenceMapper::toDomain);
    }

    public Optional<PropertyEntity> findEntityById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null);
    }

    public Optional<PropertyEntity> findEntityByIdIncludingDeleted(UUID id) {
        return jpaRepository.findByIdIncludingDeleted(id);
    }

    @Override
    public Optional<Property> findBySlug(String slug) {
        return jpaRepository.findBySlugAndDeletedAtIsNull(slug)
                .map(PropertyPersistenceMapper::toDomain);
    }

    @Override
    public PageResult<Property> findByOwnerId(UUID ownerId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PropertyEntity> result = jpaRepository.findByOwnerIdAndDeletedAtIsNull(ownerId, pageable);
        return new PageResult<>(
                result.getContent().stream().map(PropertyPersistenceMapper::toDomain).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements()
        );
    }

    @Override
    public PageResult<Property> search(PropertySearchCriteria criteria) {
        Specification<PropertyEntity> spec = PropertySpecification.buildSpecification(criteria);

        Sort sort = switch (criteria.sort() != null ? criteria.sort() : "newest") {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "pricePerDay");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "pricePerDay");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        var pageable = PageRequest.of(criteria.page(), criteria.size(), sort);
        Page<PropertyEntity> result = jpaRepository.findAll(spec, pageable);

        return new PageResult<>(
                result.getContent().stream().map(PropertyPersistenceMapper::toDomain).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements()
        );
    }

    @Override
    public void softDelete(UUID id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.softDelete();
            entity.setPublished(false);
            entity.setActive(false);
            jpaRepository.save(entity);
        });
    }

    @Override
    public long countActive() {
        return jpaRepository.countByDeletedAtIsNull();
    }
}
