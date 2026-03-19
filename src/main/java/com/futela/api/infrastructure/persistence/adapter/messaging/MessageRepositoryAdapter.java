package com.futela.api.infrastructure.persistence.adapter.messaging;

import com.futela.api.domain.model.messaging.Message;
import com.futela.api.domain.port.out.messaging.MessageRepositoryPort;
import com.futela.api.infrastructure.persistence.entity.messaging.MessageEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.MessagePersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageRepositoryAdapter implements MessageRepositoryPort {

    private final JpaMessageRepository jpaRepository;

    @Override
    public Message save(Message message) {
        MessageEntity entity = jpaRepository.findById(message.id())
                .orElse(new MessageEntity());
        entity.setContent(message.content());
        entity.setType(message.type());
        entity.setRead(message.isRead());
        entity.setReadAt(message.readAt());
        return MessagePersistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return jpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(MessagePersistenceMapper::toDomain);
    }

    @Override
    public Page<Message> findByConversationId(UUID conversationId, Pageable pageable) {
        return jpaRepository.findByConversationId(conversationId, pageable)
                .map(MessagePersistenceMapper::toDomain);
    }

    @Override
    public long countUnreadByUserId(UUID userId) {
        return jpaRepository.countUnreadByUserId(userId);
    }

    @Override
    public void softDelete(UUID id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.softDelete();
            jpaRepository.save(entity);
        });
    }
}
