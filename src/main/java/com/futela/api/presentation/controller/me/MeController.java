package com.futela.api.presentation.controller.me;

import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.common.PagedResponse;
import com.futela.api.application.dto.response.property.PropertySummaryResponse;
import com.futela.api.application.service.SecurityService;
import com.futela.api.application.usecase.property.PropertyUseCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Handles all /api/me/* routes to match Symfony API Platform endpoints.
 * The frontend calls /api/me/properties, /api/me/leases, etc.
 */
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final PropertyUseCaseService propertyService;
    private final SecurityService securityService;

    // GET /api/me/properties - My properties (matches Symfony)
    @GetMapping("/properties")
    public ApiResponse<PagedResponse<PropertySummaryResponse>> getMyProperties(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        UUID ownerId = securityService.getCurrentUserId();
        var result = propertyService.getPropertiesByOwner(ownerId, page, size);
        var paged = new PagedResponse<>(result.content(), result.page(), result.size(),
                result.totalElements(), result.totalPages());
        return ApiResponse.success(paged);
    }

    // GET /api/me/properties/{id} - My property detail (matches Symfony)
    @GetMapping("/properties/{id}")
    public ApiResponse<?> getMyProperty(@PathVariable UUID id) {
        // Uses same service but without published-only filter
        return ApiResponse.success(propertyService.getPropertyById(id));
    }
}
