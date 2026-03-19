package com.futela.api.infrastructure.event;

import com.futela.api.domain.event.*;
import com.futela.api.domain.port.out.common.EmailServicePort;
import com.futela.api.domain.port.out.common.SmsServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessagingEventListener {

    private final EmailServicePort emailService;
    private final SmsServicePort smsService;

    @Async
    @EventListener
    public void handleMessageSent(MessageSentEvent event) {
        log.info("Événement MessageSent reçu : message={}, conversation={}, sender={}",
                event.messageId(), event.conversationId(), event.senderId());
    }

    @Async
    @EventListener
    public void handleMessageRead(MessageReadEvent event) {
        log.info("Événement MessageRead reçu : message={}, conversation={}, readBy={}",
                event.messageId(), event.conversationId(), event.readByUserId());
    }

    @Async
    @EventListener
    public void handleConversationCreated(ConversationCreatedEvent event) {
        log.info("Événement ConversationCreated reçu : conversation={}, participants={}",
                event.conversationId(), event.participantIds());
    }

    @Async
    @EventListener
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        log.info("Événement NotificationCreated reçu : notification={}, user={}, type={}, channel={}",
                event.notificationId(), event.userId(), event.type(), event.channel());

        // Dispatch based on channel
        switch (event.channel()) {
            case EMAIL -> emailService.sendEmail(
                    event.userId().toString(),
                    event.title(),
                    event.body()
            );
            case SMS -> smsService.sendSms(
                    event.userId().toString(),
                    event.body()
            );
            default -> log.debug("Canal {} géré en interne (in-app/push)", event.channel());
        }
    }

    @Async
    @EventListener
    public void handleContactFormSubmitted(ContactFormSubmittedEvent event) {
        log.info("Événement ContactFormSubmitted reçu : contact={}, email={}, sujet={}",
                event.contactId(), event.email(), event.subject());

        // Notify admins via email
        emailService.sendEmail(
                "admin@futela.com",
                "Nouveau formulaire de contact : " + event.subject(),
                "De : " + event.name() + " (" + event.email() + ")"
        );
    }
}
