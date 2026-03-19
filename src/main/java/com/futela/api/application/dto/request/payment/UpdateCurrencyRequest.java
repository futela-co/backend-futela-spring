package com.futela.api.application.dto.request.payment;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record UpdateCurrencyRequest(
        String name,
        String symbol,
        @DecimalMin("0.0001") BigDecimal exchangeRate,
        Boolean isActive
) {}
