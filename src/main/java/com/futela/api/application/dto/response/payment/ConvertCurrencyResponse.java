package com.futela.api.application.dto.response.payment;

import java.math.BigDecimal;

public record ConvertCurrencyResponse(
        String from,
        String to,
        BigDecimal amount,
        BigDecimal convertedAmount,
        BigDecimal rate
) {}
