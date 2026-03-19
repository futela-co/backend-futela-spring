package com.futela.api.application.usecase.messaging;

import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.DeleteNotificationUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.NotificationEntity;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteNotificationService implements DeleteNotificationUseCase {

    private final JpaNotificationRepository notificationRepository;

    @Override
    public void execute(UUID notificationId, UUID userId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId.toString()));

        if (!notification.getUser().getId().equals(userId)) {
            throw new InvalidOperationException("Accès refusé : vous ne pouvez supprimer que vos propres notifications");
        }

        notification.softDelete();
        notificationRepository.save(notification);

        log.info("Notification supprimée : {} par utilisateur {}", notificationId, userId);
    }
}
