package com.futela.api.application.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "L'identifiant est requis")
        String username,

        @NotBlank(message = "Le mot de passe est requis")
        String password,

        String deviceFingerprint,
        String deviceName
) {}
