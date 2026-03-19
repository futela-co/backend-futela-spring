package com.futela.api.domain.port.in.payment;

import com.futela.api.application.dto.response.payment.CurrencyResponse;

import java.util.List;

public interface GetCurrenciesUseCase {
    List<CurrencyResponse> execute();
}
