package com.futela.api.infrastructure.persistence.repository.property;

import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPropertyRepository extends JpaRepository<PropertyEntity, UUID>, JpaSpecificationExecutor<PropertyEntity> {

    @Query("SELECT p FROM PropertyEntity p LEFT JOIN FETCH p.photos LEFT JOIN FETCH p.owner LEFT JOIN FETCH p.address LEFT JOIN FETCH p.category WHERE p.slug = :slug AND p.deletedAt IS NULL")
    Optional<PropertyEntity> findBySlugAndDeletedAtIsNull(@Param("slug") String slug);

    @EntityGraph(attributePaths = {"owner", "address", "category", "photos"})
    @Query(value = "SELECT p FROM PropertyEntity p WHERE p.owner.id = :ownerId AND p.deletedAt IS NULL",
            countQuery = "SELECT COUNT(p) FROM PropertyEntity p WHERE p.owner.id = :ownerId AND p.deletedAt IS NULL")
    Page<PropertyEntity> findByOwnerIdAndDeletedAtIsNull(@Param("ownerId") UUID ownerId, Pageable pageable);

    @EntityGraph(attributePaths = {"owner", "address", "category", "photos"})
    @Query("SELECT p FROM PropertyEntity p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<PropertyEntity> findByIdWithRelations(@Param("id") UUID id);

    long countByDeletedAtIsNull();

    @Query("SELECT p FROM PropertyEntity p LEFT JOIN FETCH p.owner WHERE p.id = :id")
    Optional<PropertyEntity> findByIdIncludingDeleted(@Param("id") UUID id);
}
