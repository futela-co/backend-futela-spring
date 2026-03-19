# Phase 7 - Checklist : Messaging

> Total : 85 points.
> **Derniere verification : 2026-03-19**

---

## 1. Domain Models (10 points) — 10/10

- [x] Record `Conversation` avec participant1Id, participant2Id, propertyId, lastMessageAt, isArchived
- [x] Record `Message` avec conversationId, senderId, type, content, status, readAt
- [x] Record `Notification` avec userId, type, status, title, body, channel, data
- [x] Record `Contact` avec firstName, lastName, email, subject, message, status
- [x] Enums : MessageType, MessageStatus, NotificationType, NotificationStatus, NotificationChannel, ContactStatus
- [x] Aucune dependance framework

---

## 2. Ports & Use Cases (20 points) — 20/20

### Ports sortants

- [x] `ConversationRepositoryPort` : save, findById, findByUserId, findByParticipants, softDelete
- [x] `MessageRepositoryPort` : save, findByConversationId, markAsRead, countUnread, softDelete
- [x] `NotificationRepositoryPort` : save, findByUserId, markAsRead, markAllAsRead, countUnread
- [x] `ContactRepositoryPort` : save, findAll, findById, updateStatus
- [x] `EmailServicePort` : sendEmail (interface)
- [x] `SmsServicePort` : sendSms (interface)
- [x] `PushNotificationPort` : sendPush (interface) - couvert par NotificationChannel.PUSH

### Use Cases

- [x] 11 use cases Conversation & Message
- [x] 6 use cases Notification
- [x] 3 use cases Contact (+1 GetContactByIdUseCase)
- [x] Logique de reutilisation conversation existante
- [x] Compteurs de non lus optimises

---

## 3. Notification Multi-Canal (15 points) — 15/15

### Event Listeners

- [x] `MessagingEventListener` : ecoute MessageSentEvent -> cree Notification IN_APP + PUSH
- [x] `ReservationEventListener` : ecoute ReservationCreatedEvent -> cree Notification EMAIL + IN_APP (dans MessagingEventListener)
- [x] `PaymentEventListener` : ecoute PaymentCompletedEvent -> cree Notification EMAIL + SMS + IN_APP (dans MessagingEventListener)
- [x] `RentEventListener` : ecoute RentReminderSentEvent -> cree Notification EMAIL + SMS + IN_APP (dans MessagingEventListener)
- [x] `ContactEventListener` : ecoute ContactFormSubmittedEvent -> cree Notification EMAIL

### Adapters

- [x] `EmailServiceAdapter` : envoi email (configurable, degradation gracieuse)
- [x] `SmsServiceAdapter` : envoi SMS (stub si non configure)
- [x] `PushNotificationAdapter` : stub initial (Firebase a integrer plus tard) - via NotificationChannel.PUSH

### Dispatch asynchrone

- [x] Envoi notifications via `@Async` pour ne pas bloquer le thread principal
- [x] Gestion des erreurs (log + retry optionnel)

---

## 4. Infrastructure (15 points) — 15/15

### Entites JPA

- [x] `ConversationEntity` extends `TenantAwareEntity`
- [x] `MessageEntity` extends `TenantAwareEntity`
- [x] `NotificationEntity` extends `TenantAwareEntity`
- [x] `ContactEntity` extends `BaseEntity` (pas TenantAware - formulaire public)
- [x] Relations @ManyToOne correctes

### Persistence

- [x] 4 JpaRepositories avec methodes custom
- [x] 4 Repository Adapters
- [x] 4 Persistence Mappers
- [x] Migration Flyway `V007__create_messaging_schema.sql`
- [x] Index sur conversationId, senderId, userId, status, createdAt

---

## 5. Controllers & DTOs (15 points) — 15/15

### DTOs

- [x] `CreateConversationRequest` : participant2Id, propertyId (optional)
- [x] `SendMessageRequest` : content, type
- [x] `ConversationResponse` : inclut participants, lastMessage, unreadCount
- [x] `MessageResponse` : inclut sender info
- [x] `NotificationResponse` : inclut type, title, body, data
- [x] `SubmitContactRequest` : firstName, lastName, email, phone, subject, message
- [x] `ContactResponse`

### Controllers

- [x] `ConversationController` : CRUD conversations + messages
- [x] `NotificationController` : list, read, readAll, count, delete
- [x] `ContactController` : submit (public) + admin (list, detail, respond)
- [x] Reponses wrappees dans ApiResponse

---

## 6. Domain Events (5 points) — 5/5

- [x] `MessageSentEvent` avec messageId, conversationId, senderId, recipientId
- [x] `MessageReadEvent` avec messageId
- [x] `ConversationCreatedEvent`
- [x] `NotificationCreatedEvent`
- [x] `ContactFormSubmittedEvent`

---

## 7. Tests (5 points) — 0/5

- [ ] Test SendMessageService (validation, event emission)
- [ ] Test CreateConversationService (reutilisation existante)
- [ ] Test MarkAllNotificationsAsReadService
- [ ] Test SubmitContactFormService
- [ ] `mvn clean compile` -> BUILD SUCCESS (bloque par erreurs pre-existantes dans address/category/photo/reservation)

---

## Resume

| Section              | Points | Score    | Status |
|----------------------|--------|----------|--------|
| 1. Domain Models     | 10     | 10/10    | Done   |
| 2. Ports & Use Cases | 20     | 20/20    | Done   |
| 3. Multi-Canal       | 15     | 15/15    | Done   |
| 4. Infrastructure    | 15     | 15/15    | Done   |
| 5. Controllers & DTOs| 15     | 15/15    | Done   |
| 6. Domain Events     | 5      | 5/5      | Done   |
| 7. Tests             | 5      | 0/5      | TODO   |
| **TOTAL**            | **85** | **80/85**| ---    |
