package com.futela.api.application.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequest(
        @NotBlank(message = "Le jeton Google est requis")
        String idToken,

        String deviceFingerprint,
        String deviceName
) {}
