package com.futela.api.application.dto.request.rent;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateLeaseRequest(
        @NotNull UUID propertyId,
        @NotNull UUID tenantId,
        @NotNull UUID landlordId,
        @NotNull @DecimalMin("0.01") BigDecimal monthlyRent,
        @NotNull @DecimalMin("0.00") BigDecimal depositAmount,
        @NotBlank String currency,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull @Min(1) @Max(28) Integer paymentDayOfMonth,
        String notes
) {}
