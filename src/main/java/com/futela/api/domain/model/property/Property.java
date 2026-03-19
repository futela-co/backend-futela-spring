package com.futela.api.domain.model.property;

import com.futela.api.domain.enums.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record Property(
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
        UUID companyId,
        // Shared columns (Apartment/House)
        Integer bedrooms,
        Integer bathrooms,
        Integer squareMeters,
        // Car columns
        String brand,
        String model,
        Integer year,
        Integer mileage,
        FuelType fuelType,
        Transmission transmission,
        // EventHall column
        Integer capacity,
        // Land column
        LandType landType,
        Integer surfaceArea,
        // JSON attributes
        Map<String, Object> attributes,
        // Related
        List<Photo> photos,
        // Address info
        String formattedAddress,
        Instant createdAt,
        Instant updatedAt
) {}
