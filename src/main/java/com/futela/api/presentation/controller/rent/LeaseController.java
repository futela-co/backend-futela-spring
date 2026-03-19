package com.futela.api.presentation.controller.rent;

import com.futela.api.application.dto.request.rent.CreateLeaseRequest;
import com.futela.api.application.dto.request.rent.RenewLeaseRequest;
import com.futela.api.application.dto.request.rent.TerminateLeaseRequest;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.dto.response.rent.LeaseResponse;
import com.futela.api.domain.port.in.rent.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leases")
public class LeaseController {

    private final CreateLeaseUseCase createLeaseUseCase;
    private final GetLeaseByIdUseCase getLeaseByIdUseCase;
    private final GetLandlordLeasesUseCase getLandlordLeasesUseCase;
    private final GetTenantLeasesUseCase getTenantLeasesUseCase;
    private final GetActiveLeasesUseCase getActiveLeasesUseCase;
    private final RenewLeaseUseCase renewLeaseUseCase;
    private final TerminateLeaseUseCase terminateLeaseUseCase;

    public LeaseController(CreateLeaseUseCase createLeaseUseCase,
                           GetLeaseByIdUseCase getLeaseByIdUseCase,
                           GetLandlordLeasesUseCase getLandlordLeasesUseCase,
                           GetTenantLeasesUseCase getTenantLeasesUseCase,
                           GetActiveLeasesUseCase getActiveLeasesUseCase,
                           RenewLeaseUseCase renewLeaseUseCase,
                           TerminateLeaseUseCase terminateLeaseUseCase) {
        this.createLeaseUseCase = createLeaseUseCase;
        this.getLeaseByIdUseCase = getLeaseByIdUseCase;
        this.getLandlordLeasesUseCase = getLandlordLeasesUseCase;
        this.getTenantLeasesUseCase = getTenantLeasesUseCase;
        this.getActiveLeasesUseCase = getActiveLeasesUseCase;
        this.renewLeaseUseCase = renewLeaseUseCase;
        this.terminateLeaseUseCase = terminateLeaseUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LeaseResponse> create(@Valid @RequestBody CreateLeaseRequest request) {
        return ApiResponse.success(createLeaseUseCase.execute(request), "Bail créé avec succès");
    }

    @GetMapping("/{id}")
    public ApiResponse<LeaseResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(getLeaseByIdUseCase.execute(id));
    }

    @GetMapping("/landlord")
    public ApiResponse<List<LeaseResponse>> getLandlordLeases(@RequestParam UUID landlordId) {
        return ApiResponse.success(getLandlordLeasesUseCase.execute(landlordId));
    }

    @GetMapping("/tenant")
    public ApiResponse<List<LeaseResponse>> getTenantLeases(@RequestParam UUID tenantId) {
        return ApiResponse.success(getTenantLeasesUseCase.execute(tenantId));
    }

    @GetMapping("/active")
    public ApiResponse<List<LeaseResponse>> getActiveLeases() {
        return ApiResponse.success(getActiveLeasesUseCase.execute());
    }

    @PostMapping("/{id}/renew")
    public ApiResponse<LeaseResponse> renew(@PathVariable UUID id, @Valid @RequestBody RenewLeaseRequest request) {
        return ApiResponse.success(renewLeaseUseCase.execute(id, request), "Bail renouvelé avec succès");
    }

    @PostMapping("/{id}/terminate")
    public ApiResponse<LeaseResponse> terminate(@PathVariable UUID id, @Valid @RequestBody TerminateLeaseRequest request) {
        return ApiResponse.success(terminateLeaseUseCase.execute(id, request), "Bail résilié avec succès");
    }
}
