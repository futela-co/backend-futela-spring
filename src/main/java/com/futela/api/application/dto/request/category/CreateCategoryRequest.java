package com.futela.api.application.dto.request.category;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(
        @NotBlank(message = "Le nom de la catégorie est obligatoire")
        String name,

        String description,

        String icon
) {}
