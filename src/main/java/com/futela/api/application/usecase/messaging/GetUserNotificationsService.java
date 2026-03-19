package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.NotificationResponse;
import com.futela.api.domain.port.in.messaging.GetUserNotificationsUseCase;
import com.futela.api.infrastructure.persistence.mapper.messaging.NotificationPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserNotificationsService implements GetUserNotificationsUseCase {

    private final JpaNotificationRepository notificationRepository;

    @Override
    public Page<NotificationResponse> execute(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(NotificationPersistenceMapper::toResponse);
    }
}
