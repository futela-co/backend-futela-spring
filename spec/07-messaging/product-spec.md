# Phase 7 - Messaging

> Conversations, messages temps réel, notifications et formulaire de contact.

---

## 1. Vision

Le module Messaging gère toute la communication dans la plateforme :
- **Conversation** : Fil de discussion entre deux utilisateurs (propriétaire ↔ client)
- **Message** : Messages texte, images ou fichiers dans une conversation
- **Notification** : Notifications multi-canal (in-app, push, email, SMS)
- **Contact** : Formulaire de contact pour les visiteurs non-inscrits

---

## 2. Modèles du Domaine

### 2.1 Conversation

| Champ          | Type    | Contrainte       | Description                    |
|----------------|---------|------------------|--------------------------------|
| id             | UUID    | PK               | Identifiant unique             |
| participant1Id | UUID    | FK → User        | Premier participant            |
| participant2Id | UUID    | FK → User        | Second participant             |
| propertyId     | UUID    | FK → Property    | Propriété concernée (nullable) |
| lastMessageAt  | Instant | Nullable         | Dernier message                |
| isArchived     | Boolean | Not Null         | Archivée                       |
| companyId      | UUID    | FK → Company     | Tenant                         |
| createdAt      | Instant | Not Null         | Date de création               |
| updatedAt      | Instant | Not Null         | Dernière modification          |
| deletedAt      | Instant | Nullable         | Soft delete                    |

### 2.2 Message

| Champ          | Type          | Contrainte       | Description                    |
|----------------|---------------|------------------|--------------------------------|
| id             | UUID          | PK               | Identifiant unique             |
| conversationId | UUID          | FK → Conversation | Conversation                  |
| senderId       | UUID          | FK → User        | Expéditeur                     |
| type           | MessageType   | Not Null         | TEXT, IMAGE, FILE              |
| content        | String        | Not Null         | Contenu du message             |
| status         | MessageStatus | Not Null         | SENT, DELIVERED, READ          |
| readAt         | Instant       | Nullable         | Date de lecture                |
| companyId      | UUID          | FK → Company     | Tenant                         |
| createdAt      | Instant       | Not Null         | Date d'envoi                   |
| deletedAt      | Instant       | Nullable         | Soft delete                    |

### 2.3 Notification

| Champ          | Type               | Contrainte       | Description                    |
|----------------|--------------------|------------------|--------------------------------|
| id             | UUID               | PK               | Identifiant unique             |
| userId         | UUID               | FK → User        | Destinataire                   |
| type           | NotificationType   | Not Null         | RESERVATION, PAYMENT, MESSAGE, SYSTEM |
| status         | NotificationStatus | Not Null         | UNREAD, READ, ARCHIVED         |
| title          | String             | Not Null         | Titre                          |
| body           | String             | Not Null         | Contenu                        |
| channel        | NotificationChannel| Not Null         | EMAIL, SMS, PUSH, IN_APP       |
| data           | JSON               | Nullable         | Données complémentaires        |
| readAt         | Instant            | Nullable         | Date de lecture                |
| companyId      | UUID               | FK → Company     | Tenant                         |
| createdAt      | Instant            | Not Null         | Date de création               |

### 2.4 Contact

| Champ          | Type          | Contrainte       | Description                    |
|----------------|---------------|------------------|--------------------------------|
| id             | UUID          | PK               | Identifiant unique             |
| firstName      | String        | Not Null         | Prénom                         |
| lastName       | String        | Not Null         | Nom                            |
| email          | String        | Not Null         | Email                          |
| phone          | String        | Nullable         | Téléphone                      |
| subject        | String        | Not Null         | Sujet                          |
| message        | String        | Not Null         | Corps du message               |
| status         | ContactStatus | Not Null         | NEW, RESPONDED, ARCHIVED       |
| respondedAt    | Instant       | Nullable         | Date de réponse                |
| respondedBy    | UUID          | FK → User        | Admin qui a répondu            |
| companyId      | UUID          | FK → Company     | Tenant                         |
| createdAt      | Instant       | Not Null         | Date de soumission             |

---

## 3. Enums

```java
public enum MessageType { TEXT, IMAGE, FILE }
public enum MessageStatus { SENT, DELIVERED, READ }
public enum NotificationType { RESERVATION, PAYMENT, MESSAGE, SYSTEM }
public enum NotificationStatus { UNREAD, READ, ARCHIVED }
public enum NotificationChannel { EMAIL, SMS, PUSH, IN_APP }
public enum ContactStatus { NEW, RESPONDED, ARCHIVED }
```

---

## 4. Use Cases (22 use cases)

### Conversation & Message

| Use Case                          | Description                                    |
|-----------------------------------|------------------------------------------------|
| CreateConversationUseCase         | Créer une conversation entre 2 users           |
| GetUserConversationsUseCase       | Lister mes conversations                       |
| GetConversationByIdUseCase        | Détail d'une conversation                      |
| GetConversationMessagesUseCase    | Messages d'une conversation                    |
| SendMessageUseCase                | Envoyer un message                             |
| MarkMessageAsReadUseCase          | Marquer un message comme lu                    |
| DeleteMessageUseCase              | Supprimer un message                           |
| DeleteConversationUseCase         | Supprimer une conversation                     |
| ArchiveConversationUseCase        | Archiver une conversation                      |
| SearchConversationsUseCase        | Rechercher dans les conversations              |
| GetUnreadMessagesCountUseCase     | Nombre de messages non lus                     |

### Notification

| Use Case                             | Description                                 |
|--------------------------------------|---------------------------------------------|
| CreateNotificationUseCase            | Créer une notification                      |
| GetUserNotificationsUseCase          | Lister mes notifications                    |
| MarkNotificationAsReadUseCase        | Marquer comme lue                           |
| MarkAllNotificationsAsReadUseCase    | Tout marquer comme lu                       |
| DeleteNotificationUseCase            | Supprimer                                   |
| GetUnreadNotificationsCountUseCase   | Nombre de non lues                          |

### Contact

| Use Case                          | Description                                    |
|-----------------------------------|------------------------------------------------|
| SubmitContactFormUseCase          | Soumettre le formulaire                        |
| GetContactSubmissionsUseCase      | Lister les soumissions (admin)                 |
| RespondToContactUseCase           | Répondre à un contact                          |

---

## 5. Endpoints API

### 5.1 Conversation & Message

| Méthode | Path                                            | Description                     | Auth |
|---------|-------------------------------------------------|---------------------------------|------|
| POST    | /api/v1/conversations                           | Créer une conversation          | Oui  |
| GET     | /api/v1/conversations                           | Mes conversations               | Oui  |
| GET     | /api/v1/conversations/{id}                      | Détail conversation             | Oui  |
| GET     | /api/v1/conversations/{id}/messages             | Messages d'une conversation     | Oui  |
| POST    | /api/v1/conversations/{id}/messages             | Envoyer un message              | Oui  |
| PATCH   | /api/v1/messages/{id}/read                      | Marquer comme lu                | Oui  |
| DELETE  | /api/v1/messages/{id}                           | Supprimer un message            | Oui  |
| DELETE  | /api/v1/conversations/{id}                      | Supprimer conversation          | Oui  |
| PATCH   | /api/v1/conversations/{id}/archive              | Archiver                        | Oui  |
| GET     | /api/v1/messages/unread/count                   | Nb messages non lus             | Oui  |

### 5.2 Notification

| Méthode | Path                                            | Description                     | Auth |
|---------|-------------------------------------------------|---------------------------------|------|
| GET     | /api/v1/notifications                           | Mes notifications               | Oui  |
| GET     | /api/v1/notifications/unread                    | Notifications non lues          | Oui  |
| GET     | /api/v1/notifications/unread/count              | Compteur non lues               | Oui  |
| PATCH   | /api/v1/notifications/{id}/read                 | Marquer comme lue               | Oui  |
| PATCH   | /api/v1/notifications/read-all                  | Tout marquer comme lu           | Oui  |
| DELETE  | /api/v1/notifications/{id}                      | Supprimer                       | Oui  |

### 5.3 Contact

| Méthode | Path                                            | Description                     | Auth |
|---------|-------------------------------------------------|---------------------------------|------|
| POST    | /api/v1/contact                                 | Soumettre formulaire            | Non  |
| GET     | /api/v1/admin/contacts                          | Lister soumissions              | Oui  |
| GET     | /api/v1/admin/contacts/{id}                     | Détail soumission               | Oui  |
| POST    | /api/v1/admin/contacts/{id}/respond             | Répondre                        | Oui  |

---

## 6. Temps réel (WebSocket / SSE)

### Notifications temps réel

Pour les messages et notifications, deux options :
1. **Server-Sent Events (SSE)** : Plus simple, unidirectionnel (serveur → client)
2. **WebSocket** : Bidirectionnel (pour le chat en temps réel)

### Implémentation recommandée

- **SSE** pour les notifications (plus simple)
- **WebSocket** (optionnel Phase 2) pour le chat temps réel

```
GET /api/v1/notifications/stream  → SSE stream pour les notifications live
```

---

## 7. Notification Multi-Canal

### Stratégie de routage

| Événement                    | In-App | Push | Email | SMS |
|------------------------------|--------|------|-------|-----|
| Nouvelle réservation         | ✅     | ✅   | ✅    | —   |
| Paiement confirmé            | ✅     | ✅   | ✅    | ✅  |
| Nouveau message              | ✅     | ✅   | —     | —   |
| Rappel loyer                 | ✅     | ✅   | ✅    | ✅  |
| Avis sur propriété           | ✅     | —    | ✅    | —   |
| Contact formulaire           | ✅     | —    | ✅    | —   |

### Ports de sortie

- `EmailServicePort` : Envoi d'emails transactionnels
- `SmsServicePort` : Envoi de SMS
- `PushNotificationPort` : Notifications push mobile
- `RealTimeNotificationPort` : SSE / WebSocket

---

## 8. Domain Events

- `MessageSentEvent` → Notification in-app + push au destinataire
- `MessageReadEvent` → Mise à jour UI en temps réel
- `ConversationCreatedEvent` → Log
- `NotificationCreatedEvent` → Dispatch multi-canal
- `ContactFormSubmittedEvent` → Email admin + notification
