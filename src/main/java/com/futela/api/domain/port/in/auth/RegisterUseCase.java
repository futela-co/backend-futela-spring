package com.futela.api.domain.port.in.auth;

import com.futela.api.application.dto.request.auth.RegisterRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;

public interface RegisterUseCase {
    AuthResponse execute(RegisterRequest request);
}
