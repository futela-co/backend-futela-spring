package com.futela.api.infrastructure.persistence.mapper.property;

import com.futela.api.domain.enums.*;
import com.futela.api.domain.model.property.Photo;
import com.futela.api.domain.model.property.Property;
import com.futela.api.infrastructure.persistence.entity.address.AddressEntity;
import com.futela.api.infrastructure.persistence.entity.property.*;
import com.futela.api.infrastructure.persistence.mapper.address.AddressPersistenceMapper;

import java.util.List;
import java.util.Map;

public final class PropertyPersistenceMapper {

    private PropertyPersistenceMapper() {}

    public static Property toDomain(PropertyEntity entity) {
        List<Photo> photos = entity.getPhotos() != null
                ? entity.getPhotos().stream().map(PhotoPersistenceMapper::toDomain).toList()
                : List.of();

        // Build formatted address
        AddressEntity addr = entity.getAddress();
        String formattedAddress = buildFormattedAddress(addr);

        // Get type-specific columns
        String brand = null;
        String model = null;
        Integer year = null;
        Integer mileage = null;
        FuelType fuelType = null;
        Transmission transmission = null;
        Integer capacity = null;
        LandType landType = null;
        Integer surfaceArea = null;

        if (entity instanceof CarEntity car) {
            brand = car.getBrand();
            model = car.getModel();
            year = car.getYear();
            mileage = car.getMileage();
            fuelType = car.getFuelType();
            transmission = car.getTransmission();
        } else if (entity instanceof EventHallEntity hall) {
            capacity = hall.getCapacity();
        } else if (entity instanceof LandEntity land) {
            landType = land.getLandType();
            surfaceArea = land.getSurfaceArea();
        }

        PropertyStatus status = entity.getStatus();
        if (status == null) {
            status = entity.isPublished() ? PropertyStatus.PUBLISHED : PropertyStatus.DRAFT;
        }

        return new Property(
                entity.getId(), entity.getTitle(), entity.getDescription(),
                entity.getPropertyType(), status, entity.getListingType(),
                entity.getPricePerDay(), entity.getPricePerMonth(), entity.getSalePrice(),
                entity.getSlug(), entity.isPublished(), entity.isAvailable(), entity.isActive(),
                entity.getViewCount(), entity.getRating(), entity.getReviewCount(),
                entity.getOwner().getId(),
                entity.getOwner().getFirstName() + " " + entity.getOwner().getLastName(),
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                entity.getCategory() != null ? entity.getCategory().getName() : null,
                entity.getAddress().getId(),
                entity.getCompany() != null ? entity.getCompany().getId() : null,
                entity.getBedrooms(), entity.getBathrooms(), entity.getSquareMeters(),
                brand, model, year, mileage, fuelType, transmission,
                capacity, landType, surfaceArea,
                entity.getAttributes(),
                photos, formattedAddress,
                entity.getCreatedAt(), entity.getUpdatedAt()
        );
    }

    private static String buildFormattedAddress(AddressEntity addr) {
        if (addr == null) return "";
        StringBuilder sb = new StringBuilder();
        if (addr.getNumber() != null && addr.getStreet() != null) {
            sb.append(addr.getNumber()).append(" ").append(addr.getStreet());
        } else if (addr.getStreet() != null) {
            sb.append(addr.getStreet());
        }
        if (addr.getDistrict() != null) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(addr.getDistrict().getName());
        }
        if (addr.getTown() != null) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(addr.getTown().getName());
        }
        if (addr.getCity() != null) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(addr.getCity().getName());
        }
        return sb.toString();
    }

    public static PropertyEntity createEntity(PropertyType type) {
        return switch (type) {
            case APARTMENT -> new ApartmentEntity();
            case HOUSE -> new HouseEntity();
            case LAND -> new LandEntity();
            case CAR -> new CarEntity();
            case EVENT_HALL -> new EventHallEntity();
        };
    }
}
