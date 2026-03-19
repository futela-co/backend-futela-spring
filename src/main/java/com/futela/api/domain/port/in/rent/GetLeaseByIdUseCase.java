package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.LeaseResponse;

import java.util.UUID;

public interface GetLeaseByIdUseCase {
    LeaseResponse execute(UUID leaseId);
}
