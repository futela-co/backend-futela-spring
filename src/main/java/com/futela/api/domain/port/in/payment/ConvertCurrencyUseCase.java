package com.futela.api.domain.port.in.payment;

import com.futela.api.application.dto.response.payment.ConvertCurrencyResponse;

import java.math.BigDecimal;

public interface ConvertCurrencyUseCase {
    ConvertCurrencyResponse execute(String from, String to, BigDecimal amount);
}
