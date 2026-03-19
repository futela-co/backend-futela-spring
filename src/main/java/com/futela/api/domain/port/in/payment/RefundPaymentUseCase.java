package com.futela.api.domain.port.in.payment;

import com.futela.api.application.dto.request.payment.RefundPaymentRequest;
import com.futela.api.application.dto.response.payment.TransactionResponse;

public interface RefundPaymentUseCase {
    TransactionResponse execute(RefundPaymentRequest request);
}
