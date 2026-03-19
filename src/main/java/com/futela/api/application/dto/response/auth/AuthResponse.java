package com.futela.api.application.dto.response.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String sessionId,
        int expiresIn,
        int refreshExpiresIn,
        String tokenType,
        UserResponse user
) {
    public AuthResponse(String accessToken, String refreshToken, String sessionId,
                        int expiresIn, int refreshExpiresIn, UserResponse user) {
        this(accessToken, refreshToken, sessionId, expiresIn, refreshExpiresIn, "Bearer", user);
    }
}
