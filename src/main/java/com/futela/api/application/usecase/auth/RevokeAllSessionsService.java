package com.futela.api.application.usecase.auth;

import com.futela.api.domain.port.in.auth.RevokeAllSessionsUseCase;
import com.futela.api.domain.port.out.auth.DeviceSessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RevokeAllSessionsService implements RevokeAllSessionsUseCase {

    private final DeviceSessionRepositoryPort deviceSessionRepository;

    @Override
    public int execute(UUID userId) {
        return deviceSessionRepository.revokeAllSessionsForUser(userId);
    }
}
