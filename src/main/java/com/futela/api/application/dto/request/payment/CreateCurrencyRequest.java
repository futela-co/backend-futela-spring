package com.futela.api.application.dto.request.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateCurrencyRequest(
        @NotBlank @Size(min = 3, max = 3) String code,
        @NotBlank String name,
        @NotBlank String symbol,
        @NotNull @DecimalMin("0.0001") BigDecimal exchangeRate
) {}
