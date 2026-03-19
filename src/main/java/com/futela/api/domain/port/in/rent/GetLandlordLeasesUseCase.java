package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.LeaseResponse;

import java.util.List;
import java.util.UUID;

public interface GetLandlordLeasesUseCase {
    List<LeaseResponse> execute(UUID landlordId);
}
