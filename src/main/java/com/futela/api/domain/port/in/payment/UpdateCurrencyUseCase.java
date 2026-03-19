package com.futela.api.domain.port.in.payment;

import com.futela.api.application.dto.request.payment.UpdateCurrencyRequest;
import com.futela.api.application.dto.response.payment.CurrencyResponse;

import java.util.UUID;

public interface UpdateCurrencyUseCase {
    CurrencyResponse execute(UUID currencyId, UpdateCurrencyRequest request);
}
