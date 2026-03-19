package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.NotificationResponse;
import com.futela.api.domain.enums.NotificationChannel;
import com.futela.api.domain.enums.NotificationStatus;
import com.futela.api.domain.enums.NotificationType;
import com.futela.api.domain.event.NotificationCreatedEvent;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.CreateNotificationUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.NotificationEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.NotificationPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateNotificationService implements CreateNotificationUseCase {

    private final JpaNotificationRepository notificationRepository;
    private final EntityManager entityManager;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public NotificationResponse execute(UUID userId, NotificationType type, String title, String body,
                                         NotificationChannel channel, Map<String, Object> data,
                                         UUID relatedEntityId, String relatedEntityType) {
        UserEntity user = entityManager.find(UserEntity.class, userId);
        if (user == null) {
            throw new ResourceNotFoundException("User", userId.toString());
        }

        NotificationEntity entity = new NotificationEntity();
        entity.setUser(user);
        entity.setType(type);
        entity.setStatus(NotificationStatus.UNREAD);
        entity.setTitle(title);
        entity.setBody(body);
        entity.setChannel(channel);
        entity.setData(data != null ? data : Map.of());
        entity.setRelatedEntityId(relatedEntityId);
        entity.setRelatedEntityType(relatedEntityType);
        entity.setRead(false);
        entity.setCompany(user.getCompany());

        NotificationEntity saved = notificationRepository.save(entity);

        log.info("Notification créée : {} pour utilisateur {}", saved.getId(), userId);

        eventPublisher.publishEvent(new NotificationCreatedEvent(
                saved.getId(), userId, type, channel, title, body
        ));

        return NotificationPersistenceMapper.toResponse(saved);
    }
}
