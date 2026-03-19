package com.futela.api.infrastructure.persistence.specification;

import com.futela.api.domain.port.out.property.PropertySearchCriteria;
import com.futela.api.infrastructure.persistence.entity.property.PropertyEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class PropertySpecification {

    private PropertySpecification() {}

    public static Specification<PropertyEntity> buildSpecification(PropertySearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Fetch join relations to avoid N+1 (only for non-count queries)
            if (query != null && query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("owner", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("address", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("category", jakarta.persistence.criteria.JoinType.LEFT);
                root.fetch("photos", jakarta.persistence.criteria.JoinType.LEFT);
                query.distinct(true);
            }

            // Only published and not deleted
            predicates.add(cb.isTrue(root.get("isPublished")));
            predicates.add(cb.isNull(root.get("deletedAt")));
            predicates.add(cb.isTrue(root.get("isActive")));

            if (criteria.type() != null) {
                predicates.add(cb.equal(root.type(), switch (criteria.type()) {
                    case APARTMENT -> com.futela.api.infrastructure.persistence.entity.property.ApartmentEntity.class;
                    case HOUSE -> com.futela.api.infrastructure.persistence.entity.property.HouseEntity.class;
                    case LAND -> com.futela.api.infrastructure.persistence.entity.property.LandEntity.class;
                    case CAR -> com.futela.api.infrastructure.persistence.entity.property.CarEntity.class;
                    case EVENT_HALL -> com.futela.api.infrastructure.persistence.entity.property.EventHallEntity.class;
                }));
            }

            if (criteria.listingType() != null) {
                predicates.add(cb.equal(root.get("listingType"), criteria.listingType()));
            }

            if (criteria.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerDay"), criteria.minPrice()));
            }

            if (criteria.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pricePerDay"), criteria.maxPrice()));
            }

            if (criteria.cityId() != null) {
                predicates.add(cb.equal(root.get("address").get("city").get("id"), criteria.cityId()));
            }

            if (criteria.districtId() != null) {
                predicates.add(cb.equal(root.get("address").get("district").get("id"), criteria.districtId()));
            }

            if (criteria.bedrooms() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("bedrooms"), criteria.bedrooms()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
