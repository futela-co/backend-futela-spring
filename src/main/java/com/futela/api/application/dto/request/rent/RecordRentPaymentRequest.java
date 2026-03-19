package com.futela.api.application.dto.request.rent;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RecordRentPaymentRequest(
        @NotNull UUID invoiceId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull LocalDate paymentDate,
        @NotBlank String paymentMethod,
        String reference,
        String notes
) {}
