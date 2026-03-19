package com.futela.api.application.usecase.messaging;

import com.futela.api.domain.port.in.messaging.MarkAllNotificationsAsReadUseCase;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import com.futela.api.domain.enums.NotificationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MarkAllNotificationsAsReadService implements MarkAllNotificationsAsReadUseCase {

    private final JpaNotificationRepository notificationRepository;

    @Override
    public void execute(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId, Instant.now(), NotificationStatus.READ);
        log.info("Toutes les notifications marquées comme lues pour l'utilisateur {}", userId);
    }
}
