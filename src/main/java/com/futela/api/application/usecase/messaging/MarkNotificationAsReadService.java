package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.NotificationResponse;
import com.futela.api.domain.enums.NotificationStatus;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.MarkNotificationAsReadUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.NotificationEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.NotificationPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkNotificationAsReadService implements MarkNotificationAsReadUseCase {

    private final JpaNotificationRepository notificationRepository;

    @Override
    public NotificationResponse execute(UUID notificationId, UUID userId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId.toString()));

        if (!notification.getUser().getId().equals(userId)) {
            throw new InvalidOperationException("Accès refusé : cette notification ne vous appartient pas");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(Instant.now());
            notification.setStatus(NotificationStatus.READ);
            notificationRepository.save(notification);
        }

        return NotificationPersistenceMapper.toResponse(notification);
    }
}
