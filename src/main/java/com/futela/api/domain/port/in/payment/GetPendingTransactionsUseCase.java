package com.futela.api.domain.port.in.payment;

import com.futela.api.application.dto.response.payment.TransactionResponse;

import java.util.List;

public interface GetPendingTransactionsUseCase {
    List<TransactionResponse> execute();
}
