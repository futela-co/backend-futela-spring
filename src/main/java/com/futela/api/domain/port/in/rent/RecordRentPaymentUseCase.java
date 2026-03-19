package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.request.rent.RecordRentPaymentRequest;
import com.futela.api.application.dto.response.rent.RentPaymentResponse;

import java.util.UUID;

public interface RecordRentPaymentUseCase {
    RentPaymentResponse execute(UUID leaseId, RecordRentPaymentRequest request);
}
