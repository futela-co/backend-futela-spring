package com.futela.api.domain.port.in.payment;

import com.futela.api.application.dto.request.payment.CreateCurrencyRequest;
import com.futela.api.application.dto.response.payment.CurrencyResponse;

public interface CreateCurrencyUseCase {
    CurrencyResponse execute(CreateCurrencyRequest request);
}
