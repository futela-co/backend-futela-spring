package com.futela.api.application.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Le mot de passe actuel est requis")
        String currentPassword,

        @NotBlank(message = "Le nouveau mot de passe est requis")
        String newPassword
) {}
