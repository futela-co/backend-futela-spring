# Phase 5 - Rent (Gestion des loyers)

> Module le plus complexe : baux longue durée, facturation mensuelle, paiements, échéanciers et rappels.

---

## 1. Vision

Le module Rent gère le cycle de vie complet de la location longue durée :
1. **Lease** : Contrat de bail entre propriétaire et locataire
2. **RentInvoice** : Factures mensuelles auto-générées
3. **RentPayment** : Paiements de loyer (lié à une facture)
4. **PaymentSchedule** : Échéancier de paiement
5. **RentReminder** : Rappels automatiques (avant, le jour, après échéance)

---

## 2. Modèles du Domaine

### 2.1 Lease

| Champ          | Type         | Contrainte       | Description                    |
|----------------|--------------|------------------|--------------------------------|
| id             | UUID         | PK               | Identifiant unique             |
| propertyId     | UUID         | FK → Property    | Propriété louée                |
| tenantId       | UUID         | FK → User        | Locataire                      |
| landlordId     | UUID         | FK → User        | Propriétaire/bailleur          |
| status         | LeaseStatus  | Not Null         | Statut du bail                 |
| monthlyRent    | BigDecimal   | Not Null         | Loyer mensuel                  |
| depositAmount  | BigDecimal   | Not Null         | Caution                        |
| startDate      | LocalDate    | Not Null         | Date de début                  |
| endDate        | LocalDate    | Not Null         | Date de fin                    |
| paymentDayOfMonth | Integer   | Not Null         | Jour de paiement (1-28)        |
| notes          | String       | Nullable         | Notes                          |
| terminatedAt   | Instant      | Nullable         | Date de résiliation            |
| terminationReason | String    | Nullable         | Raison de résiliation          |
| companyId      | UUID         | FK → Company     | Tenant                         |
| createdAt      | Instant      | Not Null         | Date de création               |
| updatedAt      | Instant      | Not Null         | Dernière modification          |
| deletedAt      | Instant      | Nullable         | Soft delete                    |

### 2.2 RentInvoice

| Champ          | Type          | Contrainte       | Description                    |
|----------------|---------------|------------------|--------------------------------|
| id             | UUID          | PK               | Identifiant unique             |
| leaseId        | UUID          | FK → Lease       | Bail associé                   |
| invoiceNumber  | String        | Unique, Not Null | Numéro de facture              |
| amount         | BigDecimal    | Not Null         | Montant dû                     |
| paidAmount     | BigDecimal    | Not Null         | Montant payé (défaut: 0)       |
| status         | PaymentStatus | Not Null         | Statut du paiement             |
| dueDate        | LocalDate     | Not Null         | Date d'échéance                |
| periodStart    | LocalDate     | Not Null         | Début de la période couverte   |
| periodEnd      | LocalDate     | Not Null         | Fin de la période couverte     |
| lateFee        | BigDecimal    | Nullable         | Pénalité de retard             |
| companyId      | UUID          | FK → Company     | Tenant                         |
| createdAt      | Instant       | Not Null         | Date de création               |
| updatedAt      | Instant       | Not Null         | Dernière modification          |

### 2.3 RentPayment

| Champ          | Type          | Contrainte       | Description                    |
|----------------|---------------|------------------|--------------------------------|
| id             | UUID          | PK               | Identifiant unique             |
| invoiceId      | UUID          | FK → RentInvoice | Facture associée               |
| leaseId        | UUID          | FK → Lease       | Bail (raccourci)               |
| amount         | BigDecimal    | Not Null         | Montant payé                   |
| paymentDate    | LocalDate     | Not Null         | Date du paiement               |
| paymentMethod  | String        | Nullable         | Méthode (cash, mobile, bank)   |
| reference      | String        | Nullable         | Référence externe              |
| notes          | String        | Nullable         | Notes                          |
| companyId      | UUID          | FK → Company     | Tenant                         |
| createdAt      | Instant       | Not Null         | Date de création               |

### 2.4 PaymentSchedule

| Champ          | Type          | Contrainte       | Description                    |
|----------------|---------------|------------------|--------------------------------|
| id             | UUID          | PK               | Identifiant unique             |
| leaseId        | UUID          | FK → Lease       | Bail associé                   |
| dueDate        | LocalDate     | Not Null         | Date d'échéance                |
| amount         | BigDecimal    | Not Null         | Montant prévu                  |
| status         | PaymentStatus | Not Null         | Statut                         |
| invoiceId      | UUID          | FK → RentInvoice | Facture générée (nullable)     |
| companyId      | UUID          | FK → Company     | Tenant                         |
| createdAt      | Instant       | Not Null         | Date de création               |

### 2.5 RentReminder

| Champ          | Type          | Contrainte       | Description                    |
|----------------|---------------|------------------|--------------------------------|
| id             | UUID          | PK               | Identifiant unique             |
| invoiceId      | UUID          | FK → RentInvoice | Facture concernée              |
| leaseId        | UUID          | FK → Lease       | Bail                           |
| type           | ReminderType  | Not Null         | Type de rappel                 |
| sentAt         | Instant       | Not Null         | Date d'envoi                   |
| channel        | String        | Not Null         | Canal (email, sms, push)       |
| companyId      | UUID          | FK → Company     | Tenant                         |
| createdAt      | Instant       | Not Null         | Date de création               |

---

## 3. Enums

```java
public enum LeaseStatus {
    ACTIVE,      // Bail en cours
    TERMINATED,  // Résilié
    RENEWED,     // Renouvelé
    EXPIRED      // Expiré
}

public enum PaymentStatus {
    PENDING,   // En attente
    PAID,      // Payé
    OVERDUE,   // En retard
    PARTIAL    // Partiellement payé
}

public enum ReminderType {
    BEFORE_DUE,  // 3 jours avant échéance
    ON_DUE,      // Le jour de l'échéance
    AFTER_DUE    // Après l'échéance (retard)
}
```

---

## 4. Use Cases (22 use cases)

### Lease Management

| Use Case                       | Description                                    |
|--------------------------------|------------------------------------------------|
| CreateLeaseUseCase             | Créer un bail + générer échéancier             |
| GetLeaseByIdUseCase            | Détail d'un bail                               |
| GetLandlordLeasesUseCase       | Baux d'un propriétaire                         |
| GetTenantLeasesUseCase         | Baux d'un locataire                            |
| GetActiveLeasesUseCase         | Tous les baux actifs                           |
| RenewLeaseUseCase              | Renouveler un bail                             |
| TerminateLeaseUseCase          | Résilier un bail                               |

### Invoice & Payment

| Use Case                       | Description                                    |
|--------------------------------|------------------------------------------------|
| GenerateRentInvoiceUseCase     | Générer une facture mensuelle                  |
| GetRentInvoiceByIdUseCase      | Détail d'une facture                           |
| GetRentInvoicesByLeaseUseCase  | Factures d'un bail                             |
| GetUnpaidInvoicesUseCase       | Factures impayées                              |
| PayRentInvoiceUseCase          | Enregistrer un paiement                        |
| RecordRentPaymentUseCase       | Enregistrer un paiement manuel                 |
| GetRentPaymentsByLeaseUseCase  | Paiements d'un bail                            |

### Dashboard & Reports

| Use Case                          | Description                                 |
|-----------------------------------|---------------------------------------------|
| GetLandlordDashboardUseCase       | Dashboard propriétaire (KPIs)               |
| GetMonthlyIncomeReportUseCase     | Rapport revenus mensuels                    |
| GetOverduePaymentsUseCase         | Paiements en retard                         |
| GetPendingPaymentsUseCase         | Paiements en attente                        |
| GetPaymentHistoryUseCase          | Historique des paiements                    |
| GetPropertyPerformanceUseCase     | Performance par propriété                   |
| GetReminderHistoryUseCase         | Historique des rappels                      |

---

## 5. Endpoints API

### 5.1 Lease

| Méthode | Path                                       | Description                     | Auth |
|---------|--------------------------------------------|---------------------------------|------|
| POST    | /api/v1/leases                             | Créer un bail                   | Oui  |
| GET     | /api/v1/leases/{id}                        | Détail d'un bail                | Oui  |
| GET     | /api/v1/leases/landlord                    | Mes baux (propriétaire)         | Oui  |
| GET     | /api/v1/leases/tenant                      | Mes baux (locataire)            | Oui  |
| PATCH   | /api/v1/leases/{id}/renew                  | Renouveler                      | Oui  |
| PATCH   | /api/v1/leases/{id}/terminate              | Résilier                        | Oui  |

### 5.2 Invoice

| Méthode | Path                                       | Description                     | Auth |
|---------|--------------------------------------------|---------------------------------|------|
| POST    | /api/v1/leases/{id}/invoices               | Générer facture                 | Oui  |
| GET     | /api/v1/leases/{id}/invoices               | Factures d'un bail              | Oui  |
| GET     | /api/v1/invoices/{id}                      | Détail facture                  | Oui  |
| GET     | /api/v1/invoices/unpaid                    | Factures impayées               | Oui  |

### 5.3 Payment

| Méthode | Path                                       | Description                     | Auth |
|---------|--------------------------------------------|---------------------------------|------|
| POST    | /api/v1/invoices/{id}/pay                  | Payer une facture               | Oui  |
| POST    | /api/v1/leases/{id}/payments               | Enregistrer paiement manuel     | Oui  |
| GET     | /api/v1/leases/{id}/payments               | Paiements d'un bail             | Oui  |

### 5.4 Dashboard

| Méthode | Path                                       | Description                     | Auth |
|---------|--------------------------------------------|---------------------------------|------|
| GET     | /api/v1/landlord/dashboard                 | Dashboard KPIs                  | Oui  |
| GET     | /api/v1/landlord/income/monthly            | Revenus mensuels                | Oui  |
| GET     | /api/v1/landlord/payments/overdue          | Paiements en retard             | Oui  |
| GET     | /api/v1/landlord/payments/pending          | Paiements en attente            | Oui  |

---

## 6. Scheduled Tasks (Cron Jobs)

| Scheduler                    | Fréquence    | Description                                    |
|------------------------------|--------------|------------------------------------------------|
| RentInvoiceScheduler         | 1er du mois  | Génère les factures pour tous les baux actifs   |
| RentReminderScheduler        | Quotidien    | Envoie les rappels (avant, jour, après dueDate) |
| OverduePaymentScheduler      | Quotidien    | Marque les factures en retard (PENDING → OVERDUE) |
| LateFeeScheduler             | Hebdomadaire | Calcule les pénalités de retard                |

---

## 7. Dashboard KPIs (LandlordDashboard)

| KPI                     | Calcul                                          |
|-------------------------|-------------------------------------------------|
| totalProperties         | Count propriétés du landlord                    |
| propertiesRented        | Count propriétés avec bail actif                |
| monthlyIncome           | Sum loyers mensuels des baux actifs             |
| yearlyIncome            | Sum paiements de l'année en cours               |
| overduePayments         | Count factures OVERDUE                          |
| pendingPayments         | Count factures PENDING                          |
| occupancyRate           | propertiesRented / totalProperties * 100        |

---

## 8. Règles métier

1. **Création bail** : La propriété doit être PUBLISHED, pas déjà louée (pas de bail ACTIVE)
2. **Génération facture** : Automatique chaque mois pour les baux ACTIVE
3. **Paiement partiel** : Un paiement peut être partiel → status = PARTIAL
4. **Paiement total** : Quand paidAmount >= amount → status = PAID
5. **Retard** : Si dueDate passée et status = PENDING → OVERDUE
6. **Pénalité** : 5% du montant après 7 jours de retard
7. **Renouvellement** : Crée un nouveau bail basé sur l'ancien, termine l'ancien
8. **Résiliation** : Met le statut TERMINATED, enregistre la raison et la date
