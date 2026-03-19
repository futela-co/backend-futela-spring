package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.TenantSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface GetLandlordTenantsUseCase {
    List<TenantSummaryResponse> execute(UUID landlordId);
}
