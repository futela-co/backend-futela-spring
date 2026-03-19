package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.request.rent.CreateLeaseRequest;
import com.futela.api.application.dto.response.rent.LeaseResponse;

public interface CreateLeaseUseCase {
    LeaseResponse execute(CreateLeaseRequest request);
}
