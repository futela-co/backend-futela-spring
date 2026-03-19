package com.futela.api.infrastructure.persistence.repository.reservation;

import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaReservationRepository extends JpaRepository<ReservationEntity, UUID> {

    Optional<ReservationEntity> findByIdAndDeletedAtIsNull(UUID id);

    List<ReservationEntity> findByPropertyIdAndDeletedAtIsNull(UUID propertyId);

    List<ReservationEntity> findByUserIdAndDeletedAtIsNull(UUID userId);

    @Query("""
            SELECT COUNT(r) > 0 FROM ReservationEntity r
            WHERE r.property.id = :propertyId
            AND r.status NOT IN ('CANCELLED')
            AND r.deletedAt IS NULL
            AND r.startDate < :endDate
            AND r.endDate > :startDate
            AND (:excludeId IS NULL OR r.id <> :excludeId)
            """)
    boolean existsOverlapping(
            @Param("propertyId") UUID propertyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") UUID excludeId
    );
}
