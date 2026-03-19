package com.futela.api.application.dto.response.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    @DisplayName("Doit créer une réponse de succès avec les données")
    void shouldCreateSuccessResponseWithData() {
        ApiResponse<String> response = ApiResponse.success("test data");

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isEqualTo("test data");
        assertThat(response.message()).isNull();
        assertThat(response.error()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Doit créer une réponse de succès avec données et message")
    void shouldCreateSuccessResponseWithDataAndMessage() {
        ApiResponse<String> response = ApiResponse.success("data", "Opération réussie");

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isEqualTo("data");
        assertThat(response.message()).isEqualTo("Opération réussie");
        assertThat(response.error()).isNull();
    }

    @Test
    @DisplayName("Doit créer une réponse d'erreur avec code et message")
    void shouldCreateErrorResponseWithCodeAndMessage() {
        ApiResponse<Void> response = ApiResponse.error("NOT_FOUND", "Ressource non trouvée");

        assertThat(response.success()).isFalse();
        assertThat(response.data()).isNull();
        assertThat(response.error()).isNotNull();
        assertThat(response.error().code()).isEqualTo("NOT_FOUND");
        assertThat(response.error().message()).isEqualTo("Ressource non trouvée");
        assertThat(response.error().details()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    @DisplayName("Doit créer une réponse d'erreur avec détails de champs")
    void shouldCreateErrorResponseWithFieldDetails() {
        List<ApiResponse.FieldError> fieldErrors = List.of(
                new ApiResponse.FieldError("email", "L'email n'est pas valide"),
                new ApiResponse.FieldError("password", "Le mot de passe est requis")
        );

        ApiResponse<Void> response = ApiResponse.error("VALIDATION_ERROR", "Erreur de validation", fieldErrors);

        assertThat(response.success()).isFalse();
        assertThat(response.error()).isNotNull();
        assertThat(response.error().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.error().details()).hasSize(2);
        assertThat(response.error().details().get(0).field()).isEqualTo("email");
        assertThat(response.error().details().get(0).message()).isEqualTo("L'email n'est pas valide");
        assertThat(response.error().details().get(1).field()).isEqualTo("password");
    }

    @Test
    @DisplayName("Doit toujours inclure un timestamp")
    void shouldAlwaysIncludeTimestamp() {
        ApiResponse<Void> success = ApiResponse.success(null);
        ApiResponse<Void> error = ApiResponse.error("ERR", "msg");

        assertThat(success.timestamp()).isNotNull();
        assertThat(error.timestamp()).isNotNull();
    }
}
