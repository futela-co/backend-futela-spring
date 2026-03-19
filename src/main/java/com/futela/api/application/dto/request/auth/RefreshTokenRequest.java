package com.futela.api.application.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "Le jeton de rafraîchissement est requis")
        String refreshToken
) {}
