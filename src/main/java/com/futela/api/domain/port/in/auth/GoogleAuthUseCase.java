package com.futela.api.domain.port.in.auth;

import com.futela.api.application.dto.request.auth.GoogleAuthRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;

public interface GoogleAuthUseCase {
    AuthResponse execute(GoogleAuthRequest request);
}
