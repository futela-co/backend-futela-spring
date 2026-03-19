package com.futela.api.presentation.controller.property;

import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.property.PropertySummaryResponse;
import com.futela.api.application.service.SecurityService;
import com.futela.api.application.usecase.listing.ListingUseCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles /api/me/favorites endpoints (matches Symfony FavoriteResource)
 */
@RestController
@RequestMapping("/api/me/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final ListingUseCaseService listingService;
    private final SecurityService securityService;

    // GET /api/me/favorites - list my favorites
    @GetMapping
    public ApiResponse<List<PropertySummaryResponse>> getFavorites() {
        UUID userId = securityService.getCurrentUserId();
        return ApiResponse.success(listingService.getUserFavorites(userId));
    }

    // POST /api/me/favorites - add to favorites (body: {propertyId: "uuid"})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> addToFavorites(@RequestBody Map<String, String> body) {
        UUID userId = securityService.getCurrentUserId();
        UUID propertyId = UUID.fromString(body.get("propertyId"));
        // companyId will be resolved from the property internally
        listingService.addToFavorites(userId, propertyId, null);
        return ApiResponse.success(null, "Propriété ajoutée aux favoris");
    }

    // DELETE /api/me/favorites/{propertyId} - remove from favorites
    @DeleteMapping("/{propertyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromFavorites(@PathVariable UUID propertyId) {
        UUID userId = securityService.getCurrentUserId();
        listingService.removeFromFavorites(userId, propertyId);
    }
}
