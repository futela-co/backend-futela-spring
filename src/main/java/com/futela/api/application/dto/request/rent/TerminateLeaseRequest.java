package com.futela.api.application.dto.request.rent;

import jakarta.validation.constraints.NotBlank;

public record TerminateLeaseRequest(
        @NotBlank String reason
) {}
