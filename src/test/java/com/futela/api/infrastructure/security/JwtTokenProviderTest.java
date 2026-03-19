package com.futela.api.infrastructure.security;

import com.futela.api.domain.enums.UserRole;
import com.futela.api.domain.enums.UserStatus;
import com.futela.api.domain.model.auth.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private User testUser;
    private UUID userId;
    private UUID sessionId;
    private UUID companyId;

    @BeforeEach
    void setUp() {
        // Secret doit faire au moins 256 bits (32 bytes) pour HMAC-SHA
        String secret = "futela-test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha";
        long accessTokenExpiration = 900000L; // 15 min
        long refreshTokenExpiration = 604800000L; // 7 jours

        JwtProperties properties = new JwtProperties(secret, accessTokenExpiration, refreshTokenExpiration);
        jwtTokenProvider = new JwtTokenProvider(properties);

        userId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        companyId = UUID.randomUUID();

        testUser = new User(
                userId, "test@futela.com", "$2a$12$hash",
                "Jean", "Dupont", null, null,
                UserRole.ADMIN, UserStatus.ACTIVE,
                false, true, false,
                null, Instant.now(),
                companyId, "Futela",
                Instant.now(), Instant.now(), null
        );
    }

    @Test
    @DisplayName("Doit générer un token d'accès non vide")
    void shouldGenerateAccessToken() {
        String token = jwtTokenProvider.generateAccessToken(testUser, sessionId);

        assertThat(token).isNotNull().isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // JWT a 3 segments
    }

    @Test
    @DisplayName("Doit valider un token valide et retourner true")
    void shouldValidateValidToken() {
        String token = jwtTokenProvider.generateAccessToken(testUser, sessionId);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Doit retourner false pour un token expiré")
    void shouldReturnFalseForExpiredToken() {
        // Créer un provider avec expiration de 0ms
        JwtProperties shortLivedProperties = new JwtProperties(
                "futela-test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha",
                0L, 0L
        );
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(shortLivedProperties);

        String token = shortLivedProvider.generateAccessToken(testUser, sessionId);

        boolean isValid = shortLivedProvider.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Doit retourner false pour un token malformé")
    void shouldReturnFalseForMalformedToken() {
        boolean isValid = jwtTokenProvider.validateToken("ceci.nest.pas.un.jwt");

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Doit retourner false pour un token signé avec une autre clé")
    void shouldReturnFalseForTokenWithWrongSignature() {
        JwtProperties otherProperties = new JwtProperties(
                "another-secret-key-that-is-also-at-least-256-bits-long-for-hmac-sha",
                900000L, 604800000L
        );
        JwtTokenProvider otherProvider = new JwtTokenProvider(otherProperties);

        String token = otherProvider.generateAccessToken(testUser, sessionId);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Doit extraire le userId du token")
    void shouldExtractUserIdFromToken() {
        String token = jwtTokenProvider.generateAccessToken(testUser, sessionId);

        UUID extractedUserId = jwtTokenProvider.getUserIdFromToken(token);

        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("Doit extraire le companyId du token")
    void shouldExtractCompanyIdFromToken() {
        String token = jwtTokenProvider.generateAccessToken(testUser, sessionId);

        UUID extractedCompanyId = jwtTokenProvider.getCompanyIdFromToken(token);

        assertThat(extractedCompanyId).isEqualTo(companyId);
    }

    @Test
    @DisplayName("Doit extraire le rôle du token")
    void shouldExtractRoleFromToken() {
        String token = jwtTokenProvider.generateAccessToken(testUser, sessionId);

        String role = jwtTokenProvider.getRoleFromToken(token);

        assertThat(role).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Doit extraire l'email du token")
    void shouldExtractEmailFromToken() {
        String token = jwtTokenProvider.generateAccessToken(testUser, sessionId);

        String email = jwtTokenProvider.getEmailFromToken(token);

        assertThat(email).isEqualTo("test@futela.com");
    }

    @Test
    @DisplayName("Doit extraire le sessionId du token")
    void shouldExtractSessionIdFromToken() {
        String token = jwtTokenProvider.generateAccessToken(testUser, sessionId);

        UUID extractedSessionId = jwtTokenProvider.getSessionIdFromToken(token);

        assertThat(extractedSessionId).isEqualTo(sessionId);
    }

    @Test
    @DisplayName("Doit retourner null pour companyId si l'utilisateur n'a pas de company")
    void shouldReturnNullCompanyIdWhenUserHasNoCompany() {
        User userWithoutCompany = new User(
                userId, "test@futela.com", "$2a$12$hash",
                "Jean", "Dupont", null, null,
                UserRole.USER, UserStatus.ACTIVE,
                false, true, false,
                null, null,
                null, null,
                Instant.now(), Instant.now(), null
        );

        String token = jwtTokenProvider.generateAccessToken(userWithoutCompany, sessionId);
        UUID extractedCompanyId = jwtTokenProvider.getCompanyIdFromToken(token);

        assertThat(extractedCompanyId).isNull();
    }

    @Test
    @DisplayName("Doit hasher le refresh token de manière déterministe")
    void shouldHashRefreshTokenDeterministically() {
        String rawToken = "my-refresh-token";

        String hash1 = jwtTokenProvider.hashRefreshToken(rawToken);
        String hash2 = jwtTokenProvider.hashRefreshToken(rawToken);

        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isNotEqualTo(rawToken);
    }
}
