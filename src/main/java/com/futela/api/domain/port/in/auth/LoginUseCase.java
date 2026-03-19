package com.futela.api.domain.port.in.auth;

import com.futela.api.application.dto.request.auth.LoginRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;

public interface LoginUseCase {
    AuthResponse execute(LoginRequest request);
}
