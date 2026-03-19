package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.request.auth.GoogleAuthRequest;
import com.futela.api.application.dto.response.auth.AuthResponse;
import com.futela.api.application.dto.response.auth.UserResponse;
import com.futela.api.domain.enums.UserRole;
import com.futela.api.domain.enums.UserStatus;
import com.futela.api.domain.exception.UnauthorizedException;
import com.futela.api.domain.model.auth.DeviceSession;
import com.futela.api.domain.model.auth.RefreshToken;
import com.futela.api.domain.model.auth.User;
import com.futela.api.domain.model.core.Company;
import com.futela.api.domain.port.in.auth.GoogleAuthUseCase;
import com.futela.api.domain.port.out.auth.DeviceSessionRepositoryPort;
import com.futela.api.domain.port.out.auth.RefreshTokenRepositoryPort;
import com.futela.api.domain.port.out.auth.UserRepositoryPort;
import com.futela.api.domain.port.out.common.CompanyRepositoryPort;
import com.futela.api.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GoogleAuthService implements GoogleAuthUseCase {

    private final UserRepositoryPort userRepository;
    private final CompanyRepositoryPort companyRepository;
    private final DeviceSessionRepositoryPort deviceSessionRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse execute(GoogleAuthRequest request) {
        // NOTE: Google token verification should be implemented with Google API client
        // For now, this is a placeholder that will be completed when Google OAuth is configured
        throw new UnauthorizedException("L'authentification Google n'est pas encore configurée");
    }
}
