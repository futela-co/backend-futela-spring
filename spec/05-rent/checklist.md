# Phase 5 - Checklist : Rent

> Total : 100 points.
> **Dernière vérification : 2026-03-19**

---

## 1. Domain Models (15 points) — 15/15

- [x] Record `Lease` avec tous les champs (property, tenant, landlord, dates, loyer, caution, etc.)
- [x] Record `RentInvoice` avec invoiceNumber, amount, paidAmount, dueDate, periodStart/End
- [x] Record `RentPayment` avec amount, paymentDate, method, reference
- [x] Record `PaymentSchedule` avec dueDate, amount, status, invoiceId
- [x] Record `RentReminder` avec type, sentAt, channel
- [x] Enums : LeaseStatus, PaymentStatus, ReminderType
- [x] Aucune dépendance framework

---

## 2. Ports & Use Cases (20 points) — 20/20

### Ports sortants

- [x] `LeaseRepositoryPort` : save, findById, findByLandlordId, findByTenantId, findActiveByPropertyId
- [x] `RentInvoiceRepositoryPort` : save, findById, findByLeaseId, findUnpaid, findOverdue
- [x] `RentPaymentRepositoryPort` : save, findByLeaseId, findByInvoiceId
- [x] `PaymentScheduleRepositoryPort` : save, findByLeaseId, generateForLease
- [x] `RentReminderRepositoryPort` : save, findByLeaseId, findByInvoiceId

### Use Cases (22)

- [x] 7 use cases Lease (CRUD + renew + terminate)
- [x] 4 use cases Invoice (generate, list, detail, unpaid)
- [x] 3 use cases Payment (pay, record, history)
- [x] 5 use cases Dashboard/Reports
- [x] Logique de calcul prix : paiement partiel, total, pénalité
- [x] Génération échéancier à la création du bail

---

## 3. Scheduled Tasks (15 points) — 15/15

- [x] `RentInvoiceScheduler` : génération mensuelle (cron `0 0 1 * *`)
- [x] `RentReminderScheduler` : rappels quotidiens (cron `0 8 * * *`)
- [x] `OverduePaymentScheduler` : détection retards quotidienne
- [x] `LateFeeScheduler` : calcul pénalités dans PayRentInvoiceService (5% after 7 days)
- [x] Tous les schedulers utilisent `@Scheduled` avec configuration externalisée
- [x] Logging des exécutions
- [x] Gestion des erreurs (ne pas bloquer le scheduler si un bail échoue)

---

## 4. Domain Events (10 points) — 10/10

- [x] `LeaseCreatedEvent` avec leaseId, propertyId, tenantId, landlordId
- [x] `LeaseRenewedEvent`
- [x] `LeaseTerminatedEvent`
- [x] `RentInvoiceGeneratedEvent` avec invoiceId, leaseId, amount, dueDate
- [x] `RentPaymentReceivedEvent` avec paymentId, invoiceId, amount
- [x] `RentPaymentOverdueEvent` avec invoiceId, daysOverdue
- [x] `RentReminderSentEvent` avec reminderId, type, channel

---

## 5. Infrastructure (15 points) — 15/15

### Entités JPA

- [x] `LeaseEntity` extends `TenantAwareEntity` avec toutes les relations
- [x] `RentInvoiceEntity` extends `TenantAwareEntity`
- [x] `RentPaymentEntity` extends `TenantAwareEntity`
- [x] `PaymentScheduleEntity` extends `TenantAwareEntity`
- [x] `RentReminderEntity` extends `TenantAwareEntity`
- [x] Relations @ManyToOne correctes (Lease→Property, Invoice→Lease, Payment→Invoice)

### Persistence

- [x] 5 JpaRepositories avec méthodes custom
- [x] 5 Repository Adapters
- [x] 5 Persistence Mappers
- [x] Migration Flyway `V005__create_rent_schema.sql`
- [x] Index sur leaseId, landlordId, tenantId, dueDate, status

---

## 6. Controllers & DTOs (15 points) — 15/15

### DTOs

- [x] `CreateLeaseRequest` avec validation (propertyId, tenantId, monthlyRent, dates, paymentDay)
- [x] `LeaseResponse` complet (property, tenant, landlord, invoices count, payments count)
- [x] `RentInvoiceResponse` avec statut, montants, paiements liés
- [x] `RentPaymentResponse`
- [x] `LandlordDashboardResponse` avec tous les KPIs
- [x] `MonthlyIncomeResponse` avec breakdown par mois

### Controllers

- [x] `LeaseController` : CRUD + renew + terminate
- [x] `RentInvoiceController` : list, detail, unpaid
- [x] `RentPaymentController` : pay, record, history
- [x] `LandlordDashboardController` : dashboard, income, overdue, pending
- [x] Réponses wrappées dans ApiResponse

---

## 7. Tests (10 points) — 10/10

- [x] Test CreateLeaseService (validation propriété, pas de bail actif existant)
- [x] Test GenerateRentInvoiceService (calcul montant, numéro facture)
- [x] Test PayRentInvoiceService (partiel, total, dépassement)
- [x] Test TerminateLeaseService (changement statut, propriété)
- [x] Test calcul pénalités de retard
- [x] `mvn clean compile` → BUILD SUCCESS

---

## Résumé

| Section              | Points  | Score     | Status |
|----------------------|---------|-----------|--------|
| 1. Domain Models     | 15      | 15/15     | DONE   |
| 2. Ports & Use Cases | 20      | 20/20     | DONE   |
| 3. Scheduled Tasks   | 15      | 15/15     | DONE   |
| 4. Domain Events     | 10      | 10/10     | DONE   |
| 5. Infrastructure    | 15      | 15/15     | DONE   |
| 6. Controllers & DTOs| 15      | 15/15     | DONE   |
| 7. Tests             | 10      | 10/10     | DONE   |
| **TOTAL**            | **100** | **100/100** | DONE |
