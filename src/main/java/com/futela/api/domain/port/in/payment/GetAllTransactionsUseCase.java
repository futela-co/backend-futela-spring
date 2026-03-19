package com.futela.api.domain.port.in.payment;

import com.futela.api.application.dto.response.common.PagedResponse;
import com.futela.api.application.dto.response.payment.TransactionResponse;

public interface GetAllTransactionsUseCase {
    PagedResponse<TransactionResponse> execute(int page, int size);
}
