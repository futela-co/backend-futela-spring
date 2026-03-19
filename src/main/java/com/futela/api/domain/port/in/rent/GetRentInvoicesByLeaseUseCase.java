package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.RentInvoiceResponse;

import java.util.List;
import java.util.UUID;

public interface GetRentInvoicesByLeaseUseCase {
    List<RentInvoiceResponse> execute(UUID leaseId);
}
