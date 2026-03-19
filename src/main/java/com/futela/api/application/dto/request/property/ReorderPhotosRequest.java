package com.futela.api.application.dto.request.property;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ReorderPhotosRequest(
        @NotEmpty(message = "La liste des photos est obligatoire")
        List<UUID> photoIds
) {}
