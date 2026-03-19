package com.futela.api.presentation.controller.property;

import com.futela.api.application.dto.request.property.CreatePropertyRequest;
import com.futela.api.application.dto.request.property.ReorderPhotosRequest;
import com.futela.api.application.dto.request.property.UpdatePropertyRequest;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.common.PagedResponse;
import com.futela.api.application.dto.response.property.PhotoResponse;
import com.futela.api.application.dto.response.property.PropertyResponse;
import com.futela.api.application.dto.response.property.PropertySummaryResponse;
import com.futela.api.application.usecase.photo.PhotoUseCaseService;
import com.futela.api.application.usecase.property.PropertyUseCaseService;
import com.futela.api.domain.enums.ListingType;
import com.futela.api.domain.enums.PropertyType;
import com.futela.api.domain.port.out.property.PropertySearchCriteria;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.futela.api.application.service.SecurityService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    private final PropertyUseCaseService propertyService;
    private final PhotoUseCaseService photoService;
    private final SecurityService securityService;

    public PropertyController(PropertyUseCaseService propertyService, PhotoUseCaseService photoService, SecurityService securityService) {
        this.propertyService = propertyService;
        this.photoService = photoService;
        this.securityService = securityService;
    }

    @GetMapping
    public ApiResponse<PagedResponse<PropertySummaryResponse>> searchProperties(
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) ListingType listingType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) UUID cityId,
            @RequestParam(required = false) UUID districtId,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) Boolean furnished,
            @RequestParam(required = false, defaultValue = "newest") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {

        var criteria = new PropertySearchCriteria(type, listingType, minPrice, maxPrice,
                cityId, districtId, bedrooms, furnished, sort, page, size);
        var result = propertyService.searchProperties(criteria);
        var paged = new PagedResponse<>(result.content(), result.page(), result.size(),
                result.totalElements(), result.totalPages());
        return ApiResponse.success(paged);
    }

    @GetMapping("/search")
    public ApiResponse<PagedResponse<PropertySummaryResponse>> searchPropertiesText(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) ListingType listingType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) UUID cityId,
            @RequestParam(required = false) UUID districtId,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) Boolean furnished,
            @RequestParam(required = false, defaultValue = "newest") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        var criteria = new PropertySearchCriteria(type, listingType, minPrice, maxPrice,
                cityId, districtId, bedrooms, furnished, sort, page, size);
        var result = propertyService.searchProperties(criteria);
        var paged = new PagedResponse<>(result.content(), result.page(), result.size(),
                result.totalElements(), result.totalPages());
        return ApiResponse.success(paged);
    }

    @GetMapping("/{id}")
    public ApiResponse<PropertyResponse> getPropertyById(@PathVariable UUID id) {
        return ApiResponse.success(propertyService.getPropertyById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PropertyResponse> createProperty(
            @Valid @RequestBody CreatePropertyRequest request) {
        return ApiResponse.success(propertyService.createProperty(request, securityService.getCurrentUserId(), securityService.getCurrentCompanyId()),
                "Propriété créée avec succès");
    }

    @PutMapping("/{id}")
    public ApiResponse<PropertyResponse> updateProperty(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePropertyRequest request) {
        return ApiResponse.success(propertyService.updateProperty(id, request, securityService.getCurrentUserId()),
                "Propriété modifiée avec succès");
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProperty(@PathVariable UUID id) {
        // TODO: Implement delete property use case
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<PropertyResponse> publishProperty(@PathVariable UUID id) {
        return ApiResponse.success(propertyService.publishProperty(id, securityService.getCurrentUserId()),
                "Propriété publiée avec succès");
    }

    @PostMapping("/{id}/unpublish")
    public ApiResponse<PropertyResponse> unpublishProperty(@PathVariable UUID id) {
        return ApiResponse.success(propertyService.unpublishProperty(id, securityService.getCurrentUserId()),
                "Propriété dépubliée avec succès");
    }

    @PostMapping("/{id}/soft-delete")
    public ApiResponse<Void> softDeleteProperty(@PathVariable UUID id) {
        propertyService.softDeleteProperty(id, securityService.getCurrentUserId());
        return ApiResponse.success(null, "Propriété supprimée avec succès");
    }

    @PostMapping("/{id}/restore")
    public ApiResponse<Void> restoreProperty(@PathVariable UUID id) {
        propertyService.restoreProperty(id, securityService.getCurrentUserId());
        return ApiResponse.success(null, "Propriété restaurée avec succès");
    }

    // Symfony: POST /properties/{id}/contact
    @PostMapping("/{id}/contact")
    public ApiResponse<Void> contactPropertyOwner(
            @PathVariable UUID id,
            @RequestBody java.util.Map<String, String> body) {
        // TODO: implement contact property owner use case
        return ApiResponse.success(null, "Message envoyé au propriétaire");
    }

    // === Photos ===

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PhotoResponse> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String caption) throws IOException {

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new com.futela.api.domain.exception.InvalidOperationException(
                    "La taille du fichier ne doit pas dépasser 5 Mo");
        }

        return ApiResponse.success(
                photoService.uploadPhoto(id, file.getInputStream(), file.getOriginalFilename(),
                        file.getContentType(), caption, securityService.getCurrentUserId()),
                "Photo uploadée avec succès");
    }

    @DeleteMapping("/{id}/photos/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhoto(
            @PathVariable UUID id,
            @PathVariable UUID photoId) {
        photoService.deletePhoto(id, photoId, securityService.getCurrentUserId());
    }

    @PatchMapping("/{id}/photos/{photoId}/primary")
    public ApiResponse<PhotoResponse> setPrimaryPhoto(
            @PathVariable UUID id,
            @PathVariable UUID photoId) {
        return ApiResponse.success(photoService.setPrimaryPhoto(id, photoId, securityService.getCurrentUserId()),
                "Photo principale définie avec succès");
    }

    @PutMapping("/{id}/photos/reorder")
    public ApiResponse<List<PhotoResponse>> reorderPhotos(
            @PathVariable UUID id,
            @Valid @RequestBody ReorderPhotosRequest request) {
        return ApiResponse.success(photoService.reorderPhotos(id, request.photoIds(), securityService.getCurrentUserId()),
                "Photos réordonnées avec succès");
    }
}
