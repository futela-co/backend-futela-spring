package com.futela.api.application.dto.response.property;

import com.futela.api.domain.enums.ListingType;
import com.futela.api.domain.enums.PropertyStatus;
import com.futela.api.domain.enums.PropertyType;
import com.futela.api.domain.model.property.Property;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PropertySummaryResponse(
        UUID id,
        String title,
        PropertyType type,
        PropertyStatus status,
        ListingType listingType,
        BigDecimal pricePerDay,
        String slug,
        boolean isPublished,
        boolean isAvailable,
        Double rating,
        int reviewCount,
        String primaryPhotoUrl,
        String formattedAddress,
        Integer bedrooms,
        Integer bathrooms,
        Integer squareMeters,
        Instant createdAt
) {
    public static PropertySummaryResponse fromDomain(Property property) {
        String primaryPhoto = null;
        if (property.photos() != null && !property.photos().isEmpty()) {
            primaryPhoto = property.photos().stream()
                    .filter(com.futela.api.domain.model.property.Photo::isPrimary)
                    .findFirst()
                    .map(com.futela.api.domain.model.property.Photo::url)
                    .orElse(property.photos().getFirst().url());
        }

        return new PropertySummaryResponse(
                property.id(), property.title(), property.type(),
                property.status(), property.listingType(),
                property.pricePerDay(), property.slug(),
                property.isPublished(), property.isAvailable(),
                property.rating(), property.reviewCount(),
                primaryPhoto, property.formattedAddress(),
                property.bedrooms(), property.bathrooms(), property.squareMeters(),
                property.createdAt()
        );
    }
}
