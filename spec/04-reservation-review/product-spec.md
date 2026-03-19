# Phase 4 - Reservation + Review

> Réservations courte durée, visites programmées et avis sur les propriétés.

---

## 1. Vision

Deux modules complémentaires pour l'expérience utilisateur :
- **Reservation** : Réservation de propriétés (courte durée) + programmation de visites
- **Review** : Avis et notes sur les propriétés (modération par le propriétaire/admin)

---

## 2. Modèles du Domaine

### 2.1 Reservation

| Champ         | Type               | Contrainte       | Description                    |
|---------------|--------------------|------------------|--------------------------------|
| id            | UUID               | PK               | Identifiant unique             |
| propertyId    | UUID               | FK → Property    | Propriété réservée             |
| userId        | UUID               | FK → User        | Utilisateur qui réserve        |
| status        | ReservationStatus  | Not Null         | Statut de la réservation       |
| startDate     | LocalDate          | Not Null         | Date de début                  |
| endDate       | LocalDate          | Not Null         | Date de fin                    |
| totalPrice    | BigDecimal         | Not Null         | Prix total calculé             |
| guestCount    | Integer            | Not Null         | Nombre de personnes            |
| notes         | String             | Nullable         | Notes du client                |
| cancelReason  | String             | Nullable         | Raison d'annulation            |
| companyId     | UUID               | FK → Company     | Tenant                         |
| createdAt     | Instant            | Not Null         | Date de création               |
| updatedAt     | Instant            | Not Null         | Dernière modification          |
| deletedAt     | Instant            | Nullable         | Soft delete                    |

### 2.2 Visit

| Champ         | Type          | Contrainte       | Description                    |
|---------------|---------------|------------------|--------------------------------|
| id            | UUID          | PK               | Identifiant unique             |
| propertyId    | UUID          | FK → Property    | Propriété à visiter            |
| userId        | UUID          | FK → User        | Visiteur                       |
| status        | VisitStatus   | Not Null         | Statut de la visite            |
| scheduledAt   | Instant       | Not Null         | Date et heure de la visite     |
| notes         | String        | Nullable         | Notes                          |
| companyId     | UUID          | FK → Company     | Tenant                         |
| createdAt     | Instant       | Not Null         | Date de création               |
| updatedAt     | Instant       | Not Null         | Dernière modification          |
| deletedAt     | Instant       | Nullable         | Soft delete                    |

### 2.3 Review

| Champ         | Type    | Contrainte       | Description                    |
|---------------|---------|------------------|--------------------------------|
| id            | UUID    | PK               | Identifiant unique             |
| propertyId    | UUID    | FK → Property    | Propriété évaluée              |
| userId        | UUID    | FK → User        | Auteur de l'avis               |
| rating        | Rating  | Not Null         | Note (1-5 étoiles)            |
| comment       | String  | Nullable         | Commentaire                    |
| isApproved    | Boolean | Not Null         | Approuvé par modération        |
| isFlagged     | Boolean | Not Null         | Signalé comme inapproprié      |
| companyId     | UUID    | FK → Company     | Tenant                         |
| createdAt     | Instant | Not Null         | Date de création               |
| updatedAt     | Instant | Not Null         | Dernière modification          |
| deletedAt     | Instant | Nullable         | Soft delete                    |

---

## 3. Enums

```java
public enum ReservationStatus {
    PENDING,     // En attente de confirmation
    CONFIRMED,   // Confirmée par le propriétaire
    CANCELLED,   // Annulée
    COMPLETED    // Terminée
}

public enum VisitStatus {
    SCHEDULED,   // Programmée
    CONFIRMED,   // Confirmée
    CANCELLED,   // Annulée
    COMPLETED    // Effectuée
}

public enum Rating {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);
    private final int value;
}
```

---

## 4. Use Cases

### Reservation (8 use cases)

| Use Case                       | Description                                    |
|--------------------------------|------------------------------------------------|
| CreateReservationUseCase       | Créer une réservation                          |
| ConfirmReservationUseCase      | Confirmer (propriétaire)                       |
| CancelReservationUseCase       | Annuler une réservation                        |
| CompleteReservationUseCase     | Marquer comme terminée                         |
| GetReservationByIdUseCase      | Détail d'une réservation                       |
| ScheduleVisitUseCase           | Programmer une visite                          |
| ConfirmVisitUseCase            | Confirmer une visite                           |
| CancelVisitUseCase             | Annuler une visite                             |

### Review (8 use cases)

| Use Case                       | Description                                    |
|--------------------------------|------------------------------------------------|
| CreateReviewUseCase            | Créer un avis                                  |
| DeleteReviewUseCase            | Supprimer un avis                              |
| GetReviewByIdUseCase           | Détail d'un avis                               |
| GetPropertyReviewsUseCase      | Avis d'une propriété                           |
| GetUserReviewsUseCase          | Avis d'un utilisateur                          |
| ApproveReviewUseCase           | Approuver un avis (modération)                 |
| RejectReviewUseCase            | Rejeter un avis                                |
| FlagReviewUseCase              | Signaler un avis                               |

---

## 5. Endpoints API

### 5.1 Reservation

| Méthode | Path                                       | Description                     | Auth |
|---------|--------------------------------------------|---------------------------------|------|
| POST    | /api/v1/reservations                       | Créer une réservation           | Oui  |
| GET     | /api/v1/reservations/{id}                  | Détail                          | Oui  |
| PATCH   | /api/v1/reservations/{id}/confirm          | Confirmer                       | Oui  |
| PATCH   | /api/v1/reservations/{id}/cancel           | Annuler                         | Oui  |
| PATCH   | /api/v1/reservations/{id}/complete         | Terminer                        | Oui  |

### 5.2 Visit

| Méthode | Path                                       | Description                     | Auth |
|---------|--------------------------------------------|---------------------------------|------|
| POST    | /api/v1/visits                             | Programmer une visite           | Oui  |
| PATCH   | /api/v1/visits/{id}/confirm                | Confirmer                       | Oui  |
| PATCH   | /api/v1/visits/{id}/cancel                 | Annuler                         | Oui  |

### 5.3 Review

| Méthode | Path                                       | Description                     | Auth |
|---------|--------------------------------------------|---------------------------------|------|
| POST    | /api/v1/reviews                            | Créer un avis                   | Oui  |
| GET     | /api/v1/properties/{id}/reviews            | Avis d'une propriété            | Non  |
| GET     | /api/v1/reviews/{id}                       | Détail d'un avis                | Non  |
| DELETE  | /api/v1/reviews/{id}                       | Supprimer                       | Oui  |
| PATCH   | /api/v1/admin/reviews/{id}/approve         | Approuver                       | Oui  |
| PATCH   | /api/v1/admin/reviews/{id}/reject          | Rejeter                         | Oui  |
| PATCH   | /api/v1/reviews/{id}/flag                  | Signaler                        | Oui  |

---

## 6. Règles métier

### Reservation

- Le prix total est calculé : `(endDate - startDate) * property.price`
- Une propriété ne peut pas avoir 2 réservations qui se chevauchent (mêmes dates)
- Seul le propriétaire peut confirmer/compléter une réservation
- L'utilisateur ou le propriétaire peut annuler

### Review

- Un utilisateur ne peut laisser qu'un seul avis par propriété
- Les avis ne sont visibles publiquement que si `isApproved = true`
- Le propriétaire ne peut pas laisser un avis sur sa propre propriété
- Note moyenne calculée dynamiquement (pas stockée)

### Domain Events

- `ReservationCreatedEvent` → Notification au propriétaire
- `ReservationConfirmedEvent` → Notification au client
- `ReviewCreatedEvent` → Notification au propriétaire
