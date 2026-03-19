package com.futela.api.application.dto.response.payment;

import com.futela.api.domain.model.payment.Currency;

import java.math.BigDecimal;
import java.util.UUID;

public record CurrencyResponse(
        UUID id,
        String code,
        String name,
        String symbol,
        BigDecimal exchangeRate,
        boolean isActive
) {
    public static CurrencyResponse from(Currency currency) {
        return new CurrencyResponse(
                currency.id(),
                currency.code(),
                currency.name(),
                currency.symbol(),
                currency.exchangeRate(),
                currency.isActive()
        );
    }
}
