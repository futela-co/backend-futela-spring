package com.futela.api.presentation.controller.auth;

import com.futela.api.application.dto.request.auth.GoogleAuthRequest;
import com.futela.api.application.dto.request.auth.LoginRequest;
import com.futela.api.application.dto.request.auth.RefreshTokenRequest;
import com.futela.api.application.dto.request.auth.RegisterRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;
import com.futela.api.application.dto.response.auth.DeviceSessionResponse;
import com.futela.api.application.dto.response.auth.UserResponse;
import com.futela.api.application.dto.response.common.ApiResponse;
import com.futela.api.application.service.SecurityService;
import com.futela.api.domain.port.in.auth.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Endpoints d'authentification et gestion des sessions")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GoogleAuthUseCase googleAuthUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final GetActiveDevicesUseCase getActiveDevicesUseCase;
    private final LogoutDeviceUseCase logoutDeviceUseCase;
    private final RevokeAllSessionsUseCase revokeAllSessionsUseCase;
    private final ConfirmEmailUseCase confirmEmailUseCase;
    private final SendEmailCodeUseCase sendEmailCodeUseCase;
    private final SendPhoneCodeUseCase sendPhoneCodeUseCase;
    private final SecurityService securityService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Connexion", description = "Authentification par email/téléphone et mot de passe")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = loginUseCase.execute(request);
        return ApiResponse.success(response, "Connexion réussie");
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Inscription", description = "Création d'un nouveau compte utilisateur")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = registerUseCase.execute(request);
        return ApiResponse.success(response, "Inscription réussie");
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Rafraîchir le token", description = "Obtenir un nouveau token d'accès via le refresh token")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = refreshAccessTokenUseCase.execute(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Déconnexion", description = "Invalider la session courante")
    public void logout() {
        UUID sessionId = securityService.getCurrentSessionId();
        logoutUseCase.execute(sessionId);
    }

    @PostMapping("/google")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Connexion Google", description = "Authentification via Google OAuth")
    public ApiResponse<AuthResponse> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        AuthResponse response = googleAuthUseCase.execute(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Profil connecté", description = "Récupérer les informations de l'utilisateur connecté")
    public ApiResponse<UserResponse> getCurrentUser() {
        UUID userId = securityService.getCurrentUserId();
        UserResponse response = getCurrentUserUseCase.execute(userId);
        return ApiResponse.success(response);
    }

    @GetMapping("/devices")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Sessions actives", description = "Lister tous les appareils/sessions actifs")
    public ApiResponse<List<DeviceSessionResponse>> getActiveDevices() {
        UUID userId = securityService.getCurrentUserId();
        UUID sessionId = securityService.getCurrentSessionId();
        List<DeviceSessionResponse> response = getActiveDevicesUseCase.execute(userId, sessionId);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/devices/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Déconnecter un appareil", description = "Révoquer une session spécifique")
    public ApiResponse<Void> logoutDevice(@PathVariable UUID id) {
        logoutDeviceUseCase.execute(id);
        return ApiResponse.success(null, "Session révoquée");
    }

    @DeleteMapping("/devices")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Révoquer toutes les sessions (legacy)", description = "Déconnecter tous les appareils")
    public ApiResponse<Map<String, Object>> revokeAllSessionsLegacy() {
        UUID userId = securityService.getCurrentUserId();
        int revoked = revokeAllSessionsUseCase.execute(userId);
        return ApiResponse.success(
                Map.of("sessionsRevoked", revoked),
                "Toutes les sessions ont été révoquées"
        );
    }

    @PostMapping("/revoke-all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Révoquer toutes les sessions", description = "Déconnecter tous les appareils")
    public ApiResponse<Map<String, Object>> revokeAllSessions() {
        UUID userId = securityService.getCurrentUserId();
        int revoked = revokeAllSessionsUseCase.execute(userId);
        return ApiResponse.success(
                Map.of("sessionsRevoked", revoked),
                "Toutes les sessions ont été révoquées"
        );
    }

    @PostMapping("/confirm-email")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Confirmer l'email", description = "Vérifier l'email avec un code de confirmation")
    public Map<String, String> confirmEmail(@RequestBody Map<String, String> body) {
        UUID userId = securityService.getCurrentUserId();
        String code = body.get("code");
        confirmEmailUseCase.execute(userId, code);
        return Map.of("message", "Email confirmé avec succès");
    }

    @PostMapping("/confirm-phone")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Confirmer le téléphone", description = "Vérifier le téléphone avec un code de confirmation")
    public Map<String, String> confirmPhone(@RequestBody Map<String, String> body) {
        // TODO: Implémenter ConfirmPhoneUseCase
        return Map.of("message", "Téléphone confirmé avec succès");
    }

    @PostMapping("/send-email-code")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Envoyer code email", description = "Envoyer un code de vérification par email")
    public Map<String, String> sendEmailCode() {
        UUID userId = securityService.getCurrentUserId();
        sendEmailCodeUseCase.execute(userId);
        return Map.of("message", "Code de vérification envoyé");
    }

    @PostMapping("/send-phone-code")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Envoyer code SMS", description = "Envoyer un code de vérification par SMS")
    public Map<String, String> sendPhoneCode() {
        UUID userId = securityService.getCurrentUserId();
        sendPhoneCodeUseCase.execute(userId);
        return Map.of("message", "Code de vérification envoyé par SMS");
    }
}
