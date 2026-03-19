package com.futela.api.infrastructure.persistence.specification;

import com.futela.api.domain.enums.ReservationStatus;
import com.futela.api.infrastructure.persistence.entity.reservation.ReservationEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public final class ReservationSpecification {

    private ReservationSpecification() {}

    public static Specification<ReservationEntity> notDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<ReservationEntity> hasPropertyId(UUID propertyId) {
        return (root, query, cb) -> cb.equal(root.get("property").get("id"), propertyId);
    }

    public static Specification<ReservationEntity> hasUserId(UUID userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<ReservationEntity> hasStatus(ReservationStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<ReservationEntity> overlaps(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> cb.and(
                cb.lessThan(root.get("startDate"), endDate),
                cb.greaterThan(root.get("endDate"), startDate),
                cb.notEqual(root.get("status"), ReservationStatus.CANCELLED)
        );
    }

    public static Specification<ReservationEntity> excludeId(UUID excludeId) {
        if (excludeId == null) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.notEqual(root.get("id"), excludeId);
    }
}
