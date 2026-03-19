package com.futela.api.application.dto.request.payment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RefundPaymentRequest(
        @NotNull UUID transactionId,
        String reason
) {}
