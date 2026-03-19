package com.futela.api.application.dto.response.rent;

import java.util.UUID;

public record TenantSummaryResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String avatar
) {}
