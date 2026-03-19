package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.request.rent.TerminateLeaseRequest;
import com.futela.api.application.dto.response.rent.LeaseResponse;

import java.util.UUID;

public interface TerminateLeaseUseCase {
    LeaseResponse execute(UUID leaseId, TerminateLeaseRequest request);
}
