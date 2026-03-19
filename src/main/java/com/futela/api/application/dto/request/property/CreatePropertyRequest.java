package com.futela.api.application.dto.request.property;

import com.futela.api.domain.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record CreatePropertyRequest(
        @NotBlank(message = "Le titre est obligatoire")
        String title,

        String description,

        @NotNull(message = "Le type de propriété est obligatoire")
        PropertyType type,

        @NotNull(message = "Le type d'annonce est obligatoire")
        ListingType listingType,

        @NotNull(message = "Le prix par jour est obligatoire")
        @Positive(message = "Le prix par jour doit être positif")
        BigDecimal pricePerDay,

        BigDecimal pricePerMonth,
        BigDecimal salePrice,

        @NotNull(message = "L'adresse est obligatoire")
        UUID addressId,

        UUID categoryId,

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

        // EventHall
        Integer capacity,

        // Land
        LandType landType,
        Integer surfaceArea,

        // JSON attributes
        Map<String, Object> attributes
) {}
