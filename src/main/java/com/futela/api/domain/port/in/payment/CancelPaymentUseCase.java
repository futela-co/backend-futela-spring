package com.futela.api.domain.port.in.payment;

import com.futela.api.application.dto.response.payment.TransactionResponse;

import java.util.UUID;

public interface CancelPaymentUseCase {
    TransactionResponse execute(UUID transactionId);
}
