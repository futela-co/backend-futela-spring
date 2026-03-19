package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.UnreadCountResponse;
import com.futela.api.domain.port.in.messaging.GetUnreadNotificationsCountUseCase;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUnreadNotificationsCountService implements GetUnreadNotificationsCountUseCase {

    private final JpaNotificationRepository notificationRepository;

    @Override
    public UnreadCountResponse execute(UUID userId) {
        long count = notificationRepository.countUnreadByUserId(userId);
        return new UnreadCountResponse(count);
    }
}
