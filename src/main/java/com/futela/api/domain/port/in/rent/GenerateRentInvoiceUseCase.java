package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.RentInvoiceResponse;

import java.util.UUID;

public interface GenerateRentInvoiceUseCase {
    RentInvoiceResponse execute(UUID leaseId, int month, int year);
}
