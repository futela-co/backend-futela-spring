package com.futela.api.infrastructure.persistence.repository.property;

import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPropertyRepository extends JpaRepository<PropertyEntity, UUID>, JpaSpecificationExecutor<PropertyEntity> {
    Optional<PropertyEntity> findBySlugAndDeletedAtIsNull(String slug);
    Page<PropertyEntity> findByOwnerIdAndDeletedAtIsNull(UUID ownerId, Pageable pageable);
}
