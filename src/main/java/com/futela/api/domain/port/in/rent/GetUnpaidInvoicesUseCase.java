package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.response.rent.RentInvoiceResponse;

import java.util.List;
import java.util.UUID;

public interface GetUnpaidInvoicesUseCase {
    List<RentInvoiceResponse> execute(UUID landlordId);
}
