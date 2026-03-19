package com.futela.api.presentation.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futela.api.application.dto.request.auth.LoginRequest;
import com.futela.api.application.dto.request.auth.RefreshTokenRequest;
import com.futela.api.application.dto.request.auth.RegisterRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;
import com.futela.api.application.dto.response.auth.UserResponse;
import com.futela.api.application.service.SecurityService;
import com.futela.api.domain.enums.UserRole;
import com.futela.api.domain.enums.UserStatus;
import com.futela.api.domain.port.in.auth.*;
import com.futela.api.infrastructure.security.CustomUserDetailsService;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private RegisterUseCase registerUseCase;

    @MockitoBean
    private RefreshAccessTokenUseCase refreshAccessTokenUseCase;

    @MockitoBean
    private LogoutUseCase logoutUseCase;

    @MockitoBean
    private GoogleAuthUseCase googleAuthUseCase;

    @MockitoBean
    private GetCurrentUserUseCase getCurrentUserUseCase;

    @MockitoBean
    private GetActiveDevicesUseCase getActiveDevicesUseCase;

    @MockitoBean
    private LogoutDeviceUseCase logoutDeviceUseCase;

    @MockitoBean
    private RevokeAllSessionsUseCase revokeAllSessionsUseCase;

    @MockitoBean
    private ConfirmEmailUseCase confirmEmailUseCase;

    @MockitoBean
    private SendEmailCodeUseCase sendEmailCodeUseCase;

    @MockitoBean
    private SendPhoneCodeUseCase sendPhoneCodeUseCase;

    @MockitoBean
    private SecurityService securityService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private EntityManager entityManager;

    private AuthResponse authResponse;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        UserResponse userResponse = new UserResponse(
                userId, "test@futela.com", "Jean", "Dupont", "Jean Dupont",
                "+243999999999", null, UserRole.USER, UserStatus.ACTIVE,
                false, true, false,
                null, Instant.now(), companyId, "Futela",
                Instant.now(), Instant.now()
        );

        authResponse = new AuthResponse(
                "access-token-jwt",
                "refresh-token-raw",
                UUID.randomUUID().toString(),
                900, 604800,
                userResponse
        );
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/login doit retourner 200 avec un body valide")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("test@futela.com", "password123", null, null);
        when(loginUseCase.execute(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-jwt"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/login doit retourner 400 avec des champs manquants")
    void shouldReturn400WhenLoginFieldsMissing() throws Exception {
        String invalidBody = "{\"username\": \"\", \"password\": \"\"}";

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register doit retourner 201 avec un body valide")
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Jean", "Dupont", "jean@futela.com", "+243999999999",
                "password123", null, null
        );
        when(registerUseCase.execute(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access-token-jwt"))
    }

    @Test
    @DisplayName("GET /api/auth/me sans token doit retourner 401")
    void shouldReturn401WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@futela.com")
    @DisplayName("GET /api/auth/me avec token doit retourner 200")
    void shouldReturnCurrentUserWhenAuthenticated() throws Exception {
        UUID companyId = UUID.randomUUID();
        UserResponse userResponse = new UserResponse(
                userId, "test@futela.com", "Jean", "Dupont", "Jean Dupont",
                null, null, UserRole.USER, UserStatus.ACTIVE,
                false, true, false,
                null, null, companyId, "Futela",
                Instant.now(), Instant.now()
        );

        when(securityService.getCurrentUserId()).thenReturn(userId);
        when(getCurrentUserUseCase.execute(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@futela.com"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/refresh doit retourner 200 avec un refresh token valide")
    void shouldRefreshTokenSuccessfully() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(refreshAccessTokenUseCase.execute(any(RefreshTokenRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-jwt"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/register doit retourner 400 si le prénom est manquant")
    void shouldReturn400WhenFirstNameMissing() throws Exception {
        String body = """
                {
                    "firstName": "",
                    "lastName": "Dupont",
                    "email": "jean@futela.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
