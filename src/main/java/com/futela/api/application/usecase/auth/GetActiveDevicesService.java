package com.futela.api.application.usecase.auth;

import com.futela.api.application.dto.response.auth.DeviceSessionResponse;
import com.futela.api.domain.model.auth.DeviceSession;
import com.futela.api.domain.port.in.auth.GetActiveDevicesUseCase;
import com.futela.api.domain.port.out.auth.DeviceSessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetActiveDevicesService implements GetActiveDevicesUseCase {

    private final DeviceSessionRepositoryPort deviceSessionRepository;

    @Override
    public List<DeviceSessionResponse> execute(UUID userId, UUID currentSessionId) {
        List<DeviceSession> sessions = deviceSessionRepository.findActiveSessionsByUser(userId);
        return sessions.stream()
                .map(session -> DeviceSessionResponse.fromDomain(
                        session,
                        currentSessionId != null && session.id().equals(currentSessionId)
                ))
                .toList();
    }
}
