package com.futela.api.domain.port.in.payment;

import com.futela.api.application.dto.response.payment.TransactionResponse;

public interface ConfirmPaymentUseCase {
    TransactionResponse execute(String externalRef, String status);
}
