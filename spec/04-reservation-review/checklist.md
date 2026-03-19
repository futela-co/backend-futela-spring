# Phase 4 - Checklist : Reservation + Review

> Total : 75 points.
> **Dernière vérification : 2026-03-19**

---

## 1. Domain Models (10 points) — 10/10

- [x] Record `Reservation` avec tous les champs (id, propertyId, userId, status, dates, totalPrice, etc.)
- [x] Record `Visit` avec tous les champs (id, propertyId, userId, status, scheduledAt, etc.)
- [x] Record `Review` avec tous les champs (id, propertyId, userId, rating, comment, isApproved, etc.)
- [x] Enums : ReservationStatus, VisitStatus, Rating
- [x] Aucune dépendance framework

---

## 2. Ports & Use Cases (15 points) — 15/15

### Ports sortants

- [x] `ReservationRepositoryPort` : save, findById, findOverlapping, findByUserId, findByPropertyId
- [x] `VisitRepositoryPort` : save, findById, findByPropertyId
- [x] `ReviewRepositoryPort` : save, findById, findByPropertyId (approved only), findByUserId, existsByUserIdAndPropertyId, softDelete

### Use Cases

- [x] 8 use cases Reservation implémentés
- [x] 8 use cases Review implémentés
- [x] Vérification disponibilité (pas de chevauchement dates)
- [x] Vérification unicité review (1 user = 1 review par property)
- [x] Calcul prix total réservation

---

## 3. Domain Events (10 points) — 10/10

- [x] `ReservationCreatedEvent` publié lors de la création
- [x] `ReservationConfirmedEvent` publié lors de la confirmation
- [x] `ReservationCancelledEvent` publié lors de l'annulation
- [x] `VisitScheduledEvent` publié lors de la programmation
- [x] `ReviewCreatedEvent` publié lors de la création
- [x] `ReviewFlaggedEvent` publié lors du signalement

---

## 4. Infrastructure (15 points) — 15/15

### Entités JPA

- [x] `ReservationEntity` extends `TenantAwareEntity`
- [x] `VisitEntity` extends `TenantAwareEntity`
- [x] `ReviewEntity` extends `TenantAwareEntity`
- [x] Relations JPA correctes (@ManyToOne vers PropertyEntity, UserEntity)

### Persistence

- [x] JpaReservationRepository, JpaVisitRepository, JpaReviewRepository
- [x] Repository Adapters (3)
- [x] Persistence Mappers (3)
- [x] Migration Flyway `V004__create_reservation_review_schema.sql`
- [x] Index sur propertyId, userId, status

---

## 5. Controllers & DTOs (15 points) — 15/15

### DTOs

- [x] `CreateReservationRequest` : propertyId, startDate, endDate, guestCount, notes
- [x] `ReservationResponse` : inclut property info, user info, statut
- [x] `ScheduleVisitRequest` : propertyId, scheduledAt, notes
- [x] `VisitResponse`
- [x] `CreateReviewRequest` : propertyId, rating, comment
- [x] `ReviewResponse` : inclut user info, property info
- [x] Validation Jakarta sur tous les requests

### Controllers

- [x] `ReservationController` : CRUD + transitions de statut
- [x] `VisitController` : create + transitions
- [x] `ReviewController` : CRUD + modération
- [x] Réponses wrappées dans ApiResponse

---

## 6. Tests (10 points) — 8/10

- [x] Test CreateReservationService (dates, chevauchement)
- [x] Test ConfirmReservationService (permissions)
- [x] Test CreateReviewService (unicité, auto-évaluation)
- [x] Test calcul prix total
- [ ] `mvn clean compile` → BUILD SUCCESS (bloqué par erreurs pré-existantes dans messaging/address/category)

---

## Résumé

| Section              | Points | Score    | Status |
|----------------------|--------|----------|--------|
| 1. Domain Models     | 10     | 10/10    | OK     |
| 2. Ports & Use Cases | 15     | 15/15    | OK     |
| 3. Domain Events     | 10     | 10/10    | OK     |
| 4. Infrastructure    | 15     | 15/15    | OK     |
| 5. Controllers & DTOs| 15     | 15/15    | OK     |
| 6. Tests             | 10     |  8/10    | PARTIAL|
| **TOTAL**            | **75** | **73/75**| --     |
