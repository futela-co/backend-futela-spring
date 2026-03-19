package com.futela.api.application.dto.request.messaging;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitContactRequest(
        @NotBlank(message = "Le prénom est requis")
        @Size(min = 2, max = 100)
        String firstName,

        @NotBlank(message = "Le nom est requis")
        @Size(min = 2, max = 100)
        String lastName,

        @NotBlank(message = "L'email est requis")
        @Email(message = "L'email est invalide")
        String email,

        @Size(max = 20)
        String phone,

        @NotBlank(message = "Le sujet est requis")
        @Size(min = 3, max = 200)
        String subject,

        @NotBlank(message = "Le message est requis")
        @Size(min = 10)
        String message
) {
}
