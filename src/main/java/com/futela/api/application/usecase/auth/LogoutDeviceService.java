package com.futela.api.application.usecase.auth;

import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.auth.LogoutDeviceUseCase;
import com.futela.api.domain.port.out.auth.DeviceSessionRepositoryPort;
import com.futela.api.domain.port.out.auth.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LogoutDeviceService implements LogoutDeviceUseCase {

    private final DeviceSessionRepositoryPort deviceSessionRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;

    @Override
    public void execute(UUID sessionId) {
        deviceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId.toString()));

        refreshTokenRepository.revokeAllTokensForSession(sessionId);
        deviceSessionRepository.revokeSession(sessionId);
    }
}
