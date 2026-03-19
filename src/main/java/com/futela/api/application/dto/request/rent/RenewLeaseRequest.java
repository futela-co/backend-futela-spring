package com.futela.api.application.dto.request.rent;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RenewLeaseRequest(
        @NotNull LocalDate newEndDate,
        BigDecimal newMonthlyRent
) {}
