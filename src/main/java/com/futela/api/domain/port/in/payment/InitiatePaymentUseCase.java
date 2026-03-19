package com.futela.api.domain.port.in.payment;

import com.futela.api.application.dto.request.payment.InitiatePaymentRequest;
import com.futela.api.application.dto.response.payment.TransactionResponse;

public interface InitiatePaymentUseCase {
    TransactionResponse execute(InitiatePaymentRequest request);
}
