package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.NotificationResponse;
import com.futela.api.domain.port.in.messaging.GetUnreadNotificationsUseCase;
import com.futela.api.infrastructure.persistence.mapper.messaging.NotificationPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUnreadNotificationsService implements GetUnreadNotificationsUseCase {

    private final JpaNotificationRepository notificationRepository;

    @Override
    public List<NotificationResponse> execute(UUID userId) {
        return notificationRepository.findUnreadByUserId(userId).stream()
                .map(NotificationPersistenceMapper::toResponse)
                .toList();
    }
}
