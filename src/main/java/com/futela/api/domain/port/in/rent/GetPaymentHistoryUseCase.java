package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.RentPaymentResponse;

import java.util.List;
import java.util.UUID;

public interface GetPaymentHistoryUseCase {
    List<RentPaymentResponse> execute(UUID landlordId);
}
