package com.futela.api.application.dto.request.property;

import com.futela.api.domain.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record UpdatePropertyRequest(
        @NotBlank(message = "Le titre est obligatoire")
        String title,

        String description,
        ListingType listingType,

        @Positive(message = "Le prix par jour doit être positif")
        BigDecimal pricePerDay,

        BigDecimal pricePerMonth,
        BigDecimal salePrice,
        UUID addressId,
        UUID categoryId,

        // Shared columns
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

        // EventHall
        Integer capacity,

        // Land
        LandType landType,
        Integer surfaceArea,

        // JSON attributes
        Map<String, Object> attributes
) {}
