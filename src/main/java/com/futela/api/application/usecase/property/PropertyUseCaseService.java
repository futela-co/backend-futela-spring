package com.futela.api.application.usecase.property;

import com.futela.api.application.dto.request.property.CreatePropertyRequest;
import com.futela.api.application.dto.request.property.UpdatePropertyRequest;
import com.futela.api.application.dto.response.property.PropertyResponse;
import com.futela.api.application.dto.response.property.PropertySummaryResponse;
import com.futela.api.domain.enums.PropertyStatus;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.model.common.PageResult;
import com.futela.api.domain.port.out.property.PropertySearchCriteria;
import com.futela.api.infrastructure.persistence.adapter.property.PropertyRepositoryAdapter;
import com.futela.api.infrastructure.persistence.entity.address.AddressEntity;
import com.futela.api.infrastructure.persistence.entity.core.CompanyEntity;
import com.futela.api.infrastructure.persistence.entity.property.*;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.mapper.property.PropertyPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.property.JpaCategoryRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class PropertyUseCaseService {

    private final PropertyRepositoryAdapter propertyRepository;
    private final JpaCategoryRepository categoryRepository;
    private final EntityManager entityManager;

    public PropertyUseCaseService(PropertyRepositoryAdapter propertyRepository,
                                  JpaCategoryRepository categoryRepository,
                                  EntityManager entityManager) {
        this.propertyRepository = propertyRepository;
        this.categoryRepository = categoryRepository;
        this.entityManager = entityManager;
    }

    public PropertyResponse createProperty(CreatePropertyRequest request, UUID ownerId, UUID companyId) {
        PropertyEntity entity = PropertyPersistenceMapper.createEntity(request.type());

        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setListingType(request.listingType());
        entity.setPricePerDay(request.pricePerDay());
        entity.setPricePerMonth(request.pricePerMonth());
        entity.setSalePrice(request.salePrice());
        entity.setSlug(generateSlug(request.title()));
        entity.setOwner(entityManager.getReference(UserEntity.class, ownerId));
        entity.setAddress(entityManager.getReference(AddressEntity.class, request.addressId()));
        entity.setCompany(entityManager.getReference(CompanyEntity.class, companyId));

        if (request.categoryId() != null) {
            entity.setCategory(categoryRepository.findById(request.categoryId()).orElse(null));
        }

        entity.setBedrooms(request.bedrooms());
        entity.setBathrooms(request.bathrooms());
        entity.setSquareMeters(request.squareMeters());

        if (entity instanceof CarEntity car) {
            car.setBrand(request.brand());
            car.setModel(request.model());
            car.setYear(request.year());
            car.setMileage(request.mileage());
            car.setFuelType(request.fuelType());
            car.setTransmission(request.transmission());
        } else if (entity instanceof EventHallEntity hall) {
            hall.setCapacity(request.capacity());
        } else if (entity instanceof LandEntity land) {
            land.setLandType(request.landType());
            land.setSurfaceArea(request.surfaceArea());
        }

        if (request.attributes() != null) {
            entity.setAttributes(request.attributes());
        }

        var saved = propertyRepository.saveEntity(entity);
        return PropertyResponse.fromDomain(PropertyPersistenceMapper.toDomain(saved));
    }

    public PropertyResponse updateProperty(UUID id, UpdatePropertyRequest request, UUID ownerId) {
        var entity = propertyRepository.findEntityById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété", id.toString()));

        if (!entity.getOwner().getId().equals(ownerId)) {
            throw new InvalidOperationException("Vous n'êtes pas le propriétaire de cette propriété");
        }

        entity.setTitle(request.title());
        entity.setDescription(request.description());
        if (request.listingType() != null) entity.setListingType(request.listingType());
        if (request.pricePerDay() != null) entity.setPricePerDay(request.pricePerDay());
        entity.setPricePerMonth(request.pricePerMonth());
        entity.setSalePrice(request.salePrice());

        if (request.addressId() != null) {
            entity.setAddress(entityManager.getReference(AddressEntity.class, request.addressId()));
        }
        if (request.categoryId() != null) {
            entity.setCategory(categoryRepository.findById(request.categoryId()).orElse(null));
        }

        entity.setBedrooms(request.bedrooms());
        entity.setBathrooms(request.bathrooms());
        entity.setSquareMeters(request.squareMeters());

        if (entity instanceof CarEntity car) {
            car.setBrand(request.brand());
            car.setModel(request.model());
            car.setYear(request.year());
            car.setMileage(request.mileage());
            car.setFuelType(request.fuelType());
            car.setTransmission(request.transmission());
        } else if (entity instanceof EventHallEntity hall) {
            hall.setCapacity(request.capacity());
        } else if (entity instanceof LandEntity land) {
            land.setLandType(request.landType());
            land.setSurfaceArea(request.surfaceArea());
        }

        if (request.attributes() != null) {
            entity.setAttributes(request.attributes());
        }

        var saved = propertyRepository.saveEntity(entity);
        return PropertyResponse.fromDomain(PropertyPersistenceMapper.toDomain(saved));
    }

    @Transactional(readOnly = true)
    public PropertyResponse getPropertyBySlug(String slug) {
        return propertyRepository.findBySlug(slug)
                .map(PropertyResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété", slug));
    }

    @Transactional(readOnly = true)
    public PropertyResponse getPropertyById(UUID id) {
        return propertyRepository.findById(id)
                .map(PropertyResponse::fromDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété", id.toString()));
    }

    @Transactional(readOnly = true)
    public PageResult<PropertySummaryResponse> getPropertiesByOwner(UUID ownerId, int page, int size) {
        var result = propertyRepository.findByOwnerId(ownerId, page, size);
        return new PageResult<>(
                result.content().stream().map(PropertySummaryResponse::fromDomain).toList(),
                result.page(), result.size(), result.totalElements()
        );
    }

    @Transactional(readOnly = true)
    public PageResult<PropertySummaryResponse> searchProperties(PropertySearchCriteria criteria) {
        var result = propertyRepository.search(criteria);
        return new PageResult<>(
                result.content().stream().map(PropertySummaryResponse::fromDomain).toList(),
                result.page(), result.size(), result.totalElements()
        );
    }

    public PropertyResponse publishProperty(UUID id, UUID ownerId) {
        var entity = propertyRepository.findEntityById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété", id.toString()));

        if (!entity.getOwner().getId().equals(ownerId)) {
            throw new InvalidOperationException("Vous n'êtes pas le propriétaire de cette propriété");
        }

        if (entity.getPhotos().isEmpty()) {
            throw new InvalidOperationException("La propriété doit avoir au moins une photo pour être publiée");
        }

        entity.setPublished(true);
        entity.setStatus(PropertyStatus.PUBLISHED);
        var saved = propertyRepository.saveEntity(entity);
        return PropertyResponse.fromDomain(PropertyPersistenceMapper.toDomain(saved));
    }

    public PropertyResponse unpublishProperty(UUID id, UUID ownerId) {
        var entity = propertyRepository.findEntityById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propriété", id.toString()));

        if (!entity.getOwner().getId().equals(ownerId)) {
            throw new InvalidOperationException("Vous n'êtes pas le propriétaire de cette propriété");
        }

        entity.setPublished(false);
        entity.setStatus(PropertyStatus.DRAFT);
        var saved = propertyRepository.saveEntity(entity);
        return PropertyResponse.fromDomain(PropertyPersistenceMapper.toDomain(saved));
    }

    private String generateSlug(String title) {
        String base = title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        return base + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
