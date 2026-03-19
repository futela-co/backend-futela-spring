package com.futela.api.application.dto.response.property;

import com.futela.api.domain.enums.*;
import com.futela.api.domain.model.property.Property;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record PropertyResponse(
        UUID id,
        String title,
        String description,
        PropertyType type,
        PropertyStatus status,
        ListingType listingType,
        BigDecimal pricePerDay,
        BigDecimal pricePerMonth,
        BigDecimal salePrice,
        String slug,
        boolean isPublished,
        boolean isAvailable,
        boolean isActive,
        int viewCount,
        Double rating,
        int reviewCount,
        UUID ownerId,
        String ownerName,
        UUID categoryId,
        String categoryName,
        UUID addressId,
        String formattedAddress,
        // Type-specific columns
        Integer bedrooms,
        Integer bathrooms,
        Integer squareMeters,
        String brand,
        String model,
        Integer year,
        Integer mileage,
        FuelType fuelType,
        Transmission transmission,
        Integer capacity,
        LandType landType,
        Integer surfaceArea,
        Map<String, Object> attributes,
        List<PhotoResponse> photos,
        Instant createdAt,
        Instant updatedAt
) {
    public static PropertyResponse fromDomain(Property property) {
        List<PhotoResponse> photoResponses = property.photos() != null
                ? property.photos().stream().map(PhotoResponse::fromDomain).toList()
                : List.of();

        return new PropertyResponse(
                property.id(), property.title(), property.description(),
                property.type(), property.status(), property.listingType(),
                property.pricePerDay(), property.pricePerMonth(), property.salePrice(),
                property.slug(), property.isPublished(), property.isAvailable(), property.isActive(),
                property.viewCount(), property.rating(), property.reviewCount(),
                property.ownerId(), property.ownerName(),
                property.categoryId(), property.categoryName(),
                property.addressId(), property.formattedAddress(),
                property.bedrooms(), property.bathrooms(), property.squareMeters(),
                property.brand(), property.model(), property.year(), property.mileage(),
                property.fuelType(), property.transmission(),
                property.capacity(), property.landType(), property.surfaceArea(),
                property.attributes(), photoResponses,
                property.createdAt(), property.updatedAt()
        );
    }
}
