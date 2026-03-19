package com.futela.api.application.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Le prénom est requis")
        @Size(min = 2, max = 100, message = "Le prénom doit contenir entre 2 et 100 caractères")
        String firstName,

        @NotBlank(message = "Le nom est requis")
        @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
        String lastName,

        @Email(message = "L'email n'est pas valide")
        String email,

        @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
        String phoneNumber,

        @NotBlank(message = "Le mot de passe est requis")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        String password,

        String deviceFingerprint,
        String deviceName
) {}
