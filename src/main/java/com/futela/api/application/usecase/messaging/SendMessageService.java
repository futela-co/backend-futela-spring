package com.futela.api.application.usecase.messaging;

import com.futela.api.application.dto.request.messaging.SendMessageRequest;
import com.futela.api.application.dto.response.messaging.MessageResponse;
import com.futela.api.domain.enums.NotificationChannel;
import com.futela.api.domain.enums.NotificationStatus;
import com.futela.api.domain.enums.NotificationType;
import com.futela.api.domain.event.MessageSentEvent;
import com.futela.api.domain.exception.InvalidOperationException;
import com.futela.api.domain.exception.ResourceNotFoundException;
import com.futela.api.domain.port.in.messaging.SendMessageUseCase;
import com.futela.api.infrastructure.persistence.entity.messaging.ConversationEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.MessageEntity;
import com.futela.api.infrastructure.persistence.entity.messaging.NotificationEntity;
import com.futela.api.infrastructure.persistence.entity.user.UserEntity;
import com.futela.api.infrastructure.persistence.mapper.messaging.MessagePersistenceMapper;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaConversationRepository;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaMessageRepository;
import com.futela.api.infrastructure.persistence.repository.messaging.JpaNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SendMessageService implements SendMessageUseCase {

    private final JpaConversationRepository conversationRepository;
    private final JpaMessageRepository messageRepository;
    private final JpaNotificationRepository notificationRepository;
    private final EntityManager entityManager;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public MessageResponse execute(SendMessageRequest request, UUID senderId) {
        // Load conversation
        ConversationEntity conversation = conversationRepository.findById(UUID.fromString(request.content()))
                .orElse(null);

        // The conversationId comes from the path, not the request body.
        // We need a different approach - the controller passes the conversationId.
        // For now, we handle it via an overloaded method pattern.
        throw new UnsupportedOperationException("Use execute(conversationId, request, senderId) instead");
    }

    public MessageResponse execute(UUID conversationId, SendMessageRequest request, UUID senderId) {
        // Step 1: Load conversation
        ConversationEntity conversation = conversationRepository.findById(conversationId)
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", conversationId.toString()));

        // Step 2: Validate sender is participant
        UserEntity sender = entityManager.find(UserEntity.class, senderId);
        if (sender == null) {
            throw new ResourceNotFoundException("User", senderId.toString());
        }

        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(senderId));
        if (!isParticipant) {
            throw new InvalidOperationException("L'utilisateur ne participe pas à cette conversation");
        }

        // Step 3: Create message
        MessageEntity message = new MessageEntity();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(request.content());
        message.setType(request.type());
        message.setRead(false);
        message.setCompany(sender.getCompany());

        MessageEntity saved = messageRepository.save(message);

        // Step 4: Update conversation.lastMessageAt
        conversation.setLastMessageAt(Instant.now());
        conversationRepository.save(conversation);

        // Step 5: Create notifications for other participants
        createParticipantNotifications(saved, sender, conversation);

        log.info("Message envoyé : {} dans conversation {}", saved.getId(), conversationId);

        // Step 6: Publish event
        eventPublisher.publishEvent(new MessageSentEvent(
                saved.getId(),
                conversationId,
                senderId,
                sender.getFirstName() + " " + sender.getLastName(),
                truncate(request.content(), 100),
                sender.getCompany().getId()
        ));

        return MessagePersistenceMapper.toResponse(saved);
    }

    private void createParticipantNotifications(MessageEntity message, UserEntity sender, ConversationEntity conversation) {
        for (UserEntity participant : conversation.getParticipants()) {
            if (participant.getId().equals(sender.getId())) {
                continue;
            }

            NotificationEntity notification = new NotificationEntity();
            notification.setUser(participant);
            notification.setType(NotificationType.MESSAGE);
            notification.setStatus(NotificationStatus.UNREAD);
            notification.setTitle("Nouveau message de " + sender.getFirstName() + " " + sender.getLastName());
            notification.setBody(truncate(message.getContent(), 100));
            notification.setChannel(NotificationChannel.IN_APP);
            notification.setRelatedEntityId(message.getId());
            notification.setRelatedEntityType("message");
            notification.setCompany(sender.getCompany());

            Map<String, Object> data = new HashMap<>();
            data.put("conversation_id", conversation.getId().toString());
            data.put("sender_id", sender.getId().toString());
            data.put("sender_name", sender.getFirstName() + " " + sender.getLastName());
            notification.setData(data);

            notificationRepository.save(notification);
        }
    }

    private String truncate(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}
