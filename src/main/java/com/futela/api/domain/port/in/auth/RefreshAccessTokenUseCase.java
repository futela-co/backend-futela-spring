package com.futela.api.domain.port.in.auth;

import com.futela.api.application.dto.request.auth.RefreshTokenRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;

public interface RefreshAccessTokenUseCase {
    AuthResponse execute(RefreshTokenRequest request);
}
