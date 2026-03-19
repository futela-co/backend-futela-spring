package com.futela.api.presentation.advice;

import com.futela.api.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Doit retourner une réponse 404 pour ResourceNotFoundException")
    void shouldHandle404ResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Utilisateur", "abc-123");

        GlobalExceptionHandler.ErrorResponse response = handler.handleNotFound(ex);

        assertThat(response.code()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(response.message()).contains("Utilisateur");
        assertThat(response.message()).contains("abc-123");
    }

    @Test
    @DisplayName("Doit retourner une réponse 409 pour DuplicateResourceException")
    void shouldHandle409DuplicateResource() {
        DuplicateResourceException ex = new DuplicateResourceException("Un compte avec cet email existe déjà");

        GlobalExceptionHandler.ErrorResponse response = handler.handleDuplicate(ex);

        assertThat(response.code()).isEqualTo("DUPLICATE_RESOURCE");
        assertThat(response.message()).contains("email existe déjà");
    }

    @Test
    @DisplayName("Doit retourner une réponse 400 pour ValidationException")
    void shouldHandle400ValidationException() {
        ValidationException ex = new ValidationException("Le champ email est invalide");

        GlobalExceptionHandler.ErrorResponse response = handler.handleValidation(ex);

        assertThat(response.code()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.message()).contains("email est invalide");
    }

    @Test
    @DisplayName("Doit retourner une réponse 401 pour UnauthorizedException")
    void shouldHandle401UnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Identifiants invalides");

        GlobalExceptionHandler.ErrorResponse response = handler.handleUnauthorized(ex);

        assertThat(response.code()).isEqualTo("UNAUTHORIZED");
        assertThat(response.message()).isEqualTo("Identifiants invalides");
    }

    @Test
    @DisplayName("Doit retourner une réponse 403 pour TenantMismatchException")
    void shouldHandle403TenantMismatchException() {
        TenantMismatchException ex = new TenantMismatchException();

        GlobalExceptionHandler.ErrorResponse response = handler.handleTenantMismatch(ex);

        assertThat(response.code()).isEqualTo("TENANT_MISMATCH");
        assertThat(response.message()).contains("organisation");
    }

    @Test
    @DisplayName("Doit retourner une réponse 400 pour InvalidOperationException")
    void shouldHandle400InvalidOperationException() {
        InvalidOperationException ex = new InvalidOperationException("Opération non autorisée");

        GlobalExceptionHandler.ErrorResponse response = handler.handleInvalidOperation(ex);

        assertThat(response.code()).isEqualTo("INVALID_OPERATION");
        assertThat(response.message()).isEqualTo("Opération non autorisée");
    }

    @Test
    @DisplayName("Doit retourner une réponse 500 pour les exceptions génériques")
    void shouldHandle500GenericException() {
        Exception ex = new RuntimeException("Erreur inattendue");

        GlobalExceptionHandler.ErrorResponse response = handler.handleGeneric(ex);

        assertThat(response.code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.message()).isEqualTo("Une erreur interne est survenue");
    }

    @Test
    @DisplayName("Doit retourner une réponse 403 pour AccessDeniedException")
    void shouldHandle403AccessDeniedException() {
        org.springframework.security.access.AccessDeniedException ex =
                new org.springframework.security.access.AccessDeniedException("Accès refusé");

        GlobalExceptionHandler.ErrorResponse response = handler.handleAccessDenied(ex);

        assertThat(response.code()).isEqualTo("FORBIDDEN");
        assertThat(response.message()).isEqualTo("Accès refusé");
    }
}
