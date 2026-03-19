package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.ConversationResponse;
import com.futela.api.domain.port.in.messaging.GetUserConversationsUseCase;
import com.futela.api.infrastructure.persistence.mapper.messaging.ConversationPersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserConversationsService implements GetUserConversationsUseCase {

    private final JpaConversationRepository conversationRepository;

    @Override
    public List<ConversationResponse> execute(UUID userId, boolean includeArchived) {
        return conversationRepository.findByParticipantId(userId).stream()
                .filter(c -> includeArchived || !c.isArchived())
                .map(c -> ConversationPersistenceMapper.toResponse(c, 0))
                .toList();
    }
}
