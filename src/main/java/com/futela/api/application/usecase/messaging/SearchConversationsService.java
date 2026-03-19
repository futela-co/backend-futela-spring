package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.response.messaging.ConversationResponse;
import com.futela.api.domain.port.in.messaging.SearchConversationsUseCase;
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
public class SearchConversationsService implements SearchConversationsUseCase {

    private final JpaConversationRepository conversationRepository;

    @Override
    public List<ConversationResponse> execute(UUID userId, String query, UUID propertyId, boolean includeArchived) {
        return conversationRepository.searchByUserId(userId, query, propertyId, includeArchived).stream()
                .map(c -> ConversationPersistenceMapper.toResponse(c, 0))
                .toList();
    }
}
