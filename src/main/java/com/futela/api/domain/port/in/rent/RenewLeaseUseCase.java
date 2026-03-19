package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.request.rent.RenewLeaseRequest;
import com.futela.api.application.dto.response.rent.LeaseResponse;

import java.util.UUID;

public interface RenewLeaseUseCase {
    LeaseResponse execute(UUID leaseId, RenewLeaseRequest request);
}
