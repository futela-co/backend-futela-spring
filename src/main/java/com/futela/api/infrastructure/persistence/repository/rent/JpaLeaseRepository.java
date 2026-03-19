package com.futela.api.infrastructure.persistence.repository.rent;

import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.infrastructure.persistence.entity.rent.LeaseEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaLeaseRepository extends JpaRepository<LeaseEntity, UUID> {

    @EntityGraph(attributePaths = {"property", "tenant", "landlord"})
    List<LeaseEntity> findByLandlordIdAndDeletedAtIsNull(UUID landlordId);

    @EntityGraph(attributePaths = {"property", "tenant", "landlord"})
    List<LeaseEntity> findByTenantIdAndDeletedAtIsNull(UUID tenantId);

    Optional<LeaseEntity> findByPropertyIdAndStatusAndDeletedAtIsNull(UUID propertyId, LeaseStatus status);

    @EntityGraph(attributePaths = {"property", "tenant", "landlord"})
    List<LeaseEntity> findByStatusAndDeletedAtIsNull(LeaseStatus status);

    @EntityGraph(attributePaths = {"property", "tenant", "landlord"})
    List<LeaseEntity> findByDeletedAtIsNull();

    long countByLandlordIdAndDeletedAtIsNull(UUID landlordId);
    long countByLandlordIdAndStatusAndDeletedAtIsNull(UUID landlordId, LeaseStatus status);

    @Query("SELECT l FROM LeaseEntity l LEFT JOIN FETCH l.property LEFT JOIN FETCH l.tenant LEFT JOIN FETCH l.landlord WHERE l.id = :id AND l.deletedAt IS NULL")
    Optional<LeaseEntity> findByIdAndNotDeleted(@Param("id") UUID id);

    long countByDeletedAtIsNull();
}
