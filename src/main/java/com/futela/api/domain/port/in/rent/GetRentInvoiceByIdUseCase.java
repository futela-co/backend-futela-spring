package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.RentInvoiceResponse;

import java.util.UUID;

public interface GetRentInvoiceByIdUseCase {
    RentInvoiceResponse execute(UUID invoiceId);
}
