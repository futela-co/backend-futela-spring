package com.futela.api.domain.port.in.rent;

import com.futela.api.application.dto.request.rent.PayRentInvoiceRequest;
import com.futela.api.application.dto.response.rent.RentPaymentResponse;

import java.util.UUID;

public interface PayRentInvoiceUseCase {
    RentPaymentResponse execute(UUID invoiceId, PayRentInvoiceRequest request);
}
