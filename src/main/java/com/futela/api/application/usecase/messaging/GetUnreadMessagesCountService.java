package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.UnreadCountResponse;
import com.futela.api.domain.port.in.messaging.GetUnreadMessagesCountUseCase;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUnreadMessagesCountService implements GetUnreadMessagesCountUseCase {

    private final JpaMessageRepository messageRepository;

    @Override
    public UnreadCountResponse execute(UUID userId) {
        long count = messageRepository.countUnreadByUserId(userId);
        return new UnreadCountResponse(count);
    }
}
