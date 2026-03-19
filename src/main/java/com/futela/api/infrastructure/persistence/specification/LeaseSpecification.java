package com.futela.api.infrastructure.persistence.specification;

import com.futela.api.domain.enums.LeaseStatus;
import com.futela.api.infrastructure.persistence.entity.rent.LeaseEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class LeaseSpecification {

    private LeaseSpecification() {}

    public static Specification<LeaseEntity> hasLandlord(UUID landlordId) {
        return (root, query, cb) -> cb.equal(root.get("landlord").get("id"), landlordId);
    }

    public static Specification<LeaseEntity> hasTenant(UUID tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenant").get("id"), tenantId);
    }

    public static Specification<LeaseEntity> hasProperty(UUID propertyId) {
        return (root, query, cb) -> cb.equal(root.get("property").get("id"), propertyId);
    }

    public static Specification<LeaseEntity> hasStatus(LeaseStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<LeaseEntity> isNotDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }
}
