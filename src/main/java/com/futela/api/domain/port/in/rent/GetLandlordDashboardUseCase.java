package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.LandlordDashboardResponse;

import java.util.UUID;

public interface GetLandlordDashboardUseCase {
    LandlordDashboardResponse execute(UUID landlordId);
}
