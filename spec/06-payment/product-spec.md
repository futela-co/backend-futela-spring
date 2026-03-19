# Phase 6 - Payment (FlexPay)

> Transactions de paiement mobile via FlexPay (Mpesa, Airtel Money, Orange Money) pour la RDC.

---

## 1. Vision

Le module Payment gère les transactions financières via **FlexPay**, la passerelle de paiement mobile money leader en RDC :
- **Transaction** : Enregistrement de chaque paiement/remboursement
- **Currency** : Gestion multi-devises (CDF, USD)
- **PaymentMethod** : Méthodes de paiement supportées
- **FlexPay Gateway** : Intégration API REST FlexPay

---

## 2. Modèles du Domaine

### 2.1 Transaction

| Champ           | Type              | Contrainte       | Description                    |
|-----------------|-------------------|------------------|--------------------------------|
| id              | UUID              | PK               | Identifiant unique             |
| reference       | String            | Unique, Not Null | Référence interne              |
| externalRef     | String            | Nullable         | Référence FlexPay              |
| type            | TransactionType   | Not Null         | Payment ou Refund              |
| status          | TransactionStatus | Not Null         | Statut de la transaction       |
| amount          | BigDecimal        | Not Null         | Montant                        |
| currency        | String            | Not Null         | Code devise (CDF, USD)         |
| phoneNumber     | String            | Not Null         | Numéro de téléphone (+243...)  |
| provider        | String            | Nullable         | Opérateur (MPESA, AIRTEL, etc.)|
| userId          | UUID              | FK → User        | Utilisateur payeur             |
| description     | String            | Nullable         | Description du paiement        |
| metadata        | JSON              | Nullable         | Données complémentaires        |
| failureReason   | String            | Nullable         | Raison d'échec                 |
| processedAt     | Instant           | Nullable         | Date de traitement             |
| companyId       | UUID              | FK → Company     | Tenant                         |
| createdAt       | Instant           | Not Null         | Date de création               |
| updatedAt       | Instant           | Not Null         | Dernière modification          |

### 2.2 Currency

| Champ       | Type       | Contrainte       | Description                    |
|-------------|------------|------------------|--------------------------------|
| id          | UUID       | PK               | Identifiant unique             |
| code        | String     | Unique, Not Null | Code ISO (CDF, USD)           |
| name        | String     | Not Null         | Nom (Franc Congolais, Dollar)  |
| symbol      | String     | Not Null         | Symbole (FC, $)               |
| exchangeRate| BigDecimal | Not Null         | Taux par rapport au USD        |
| isActive    | Boolean    | Not Null         | Devise active                  |
| createdAt   | Instant    | Not Null         | Date de création               |
| updatedAt   | Instant    | Not Null         | Dernière modification          |

### 2.3 PaymentMethod

| Champ       | Type    | Contrainte       | Description                    |
|-------------|---------|------------------|--------------------------------|
| id          | UUID    | PK               | Identifiant unique             |
| name        | String  | Not Null         | Nom (Mpesa, Airtel Money)      |
| code        | String  | Unique, Not Null | Code (MPESA, AIRTEL, ORANGE)   |
| provider    | String  | Not Null         | Fournisseur                    |
| isActive    | Boolean | Not Null         | Méthode active                 |
| logo        | String  | Nullable         | URL du logo                    |
| createdAt   | Instant | Not Null         | Date de création               |

---

## 3. Enums

```java
public enum TransactionType {
    PAYMENT,   // Paiement
    REFUND     // Remboursement
}

public enum TransactionStatus {
    PENDING,    // En attente de confirmation FlexPay
    COMPLETED,  // Confirmé et traité
    FAILED,     // Échec
    CANCELLED   // Annulé
}
```

---

## 4. Use Cases (13 use cases)

### Transaction

| Use Case                            | Description                                    |
|-------------------------------------|------------------------------------------------|
| InitiatePaymentUseCase             | Initier un paiement via FlexPay                |
| ConfirmPaymentUseCase              | Confirmer un paiement (callback FlexPay)       |
| CancelPaymentUseCase               | Annuler un paiement en attente                 |
| RefundPaymentUseCase               | Initier un remboursement                       |
| GetTransactionByIdUseCase          | Détail d'une transaction                       |
| GetUserTransactionsUseCase         | Transactions d'un utilisateur                  |
| GetFilteredUserTransactionsUseCase | Recherche avec filtres                         |
| GetPendingTransactionsUseCase      | Transactions en attente                        |

### Currency

| Use Case                            | Description                                    |
|-------------------------------------|------------------------------------------------|
| CreateCurrencyUseCase              | Créer une devise                               |
| UpdateCurrencyUseCase              | Modifier taux de change                        |
| GetCurrenciesUseCase               | Lister les devises                             |
| GetActiveCurrenciesUseCase         | Devises actives uniquement                     |
| ConvertCurrencyUseCase             | Convertir un montant entre devises             |

---

## 5. Endpoints API

### 5.1 Payment

| Méthode | Path                                       | Description                     | Auth |
|---------|--------------------------------------------|---------------------------------|------|
| POST    | /api/v1/payments/initiate                  | Initier un paiement FlexPay     | Oui  |
| GET     | /api/v1/transactions/{id}                  | Détail transaction              | Oui  |
| GET     | /api/v1/transactions/my                    | Mes transactions                | Oui  |
| GET     | /api/v1/transactions/pending               | Transactions en attente         | Oui  |
| PATCH   | /api/v1/transactions/{id}/cancel           | Annuler                         | Oui  |
| POST    | /api/v1/payments/refund                    | Initier remboursement           | Oui  |

### 5.2 Webhook FlexPay

| Méthode | Path                                       | Description                     | Auth     |
|---------|--------------------------------------------|---------------------------------|----------|
| POST    | /api/v1/webhooks/flexpay                   | Callback FlexPay                | Signature|

### 5.3 Currency

| Méthode | Path                                       | Description                     | Auth |
|---------|--------------------------------------------|---------------------------------|------|
| GET     | /api/v1/currencies                         | Lister les devises              | Non  |
| GET     | /api/v1/currencies/active                  | Devises actives                 | Non  |
| POST    | /api/v1/admin/currencies                   | Créer une devise                | Oui  |
| PUT     | /api/v1/admin/currencies/{id}              | Modifier (taux de change)       | Oui  |
| GET     | /api/v1/currencies/convert?from=CDF&to=USD&amount=100 | Convertir  | Non  |

---

## 6. Intégration FlexPay

### 6.1 Flux de paiement

```
1. Client → POST /payments/initiate (amount, phone, currency)
2. Backend → Persiste Transaction (status: PENDING)
3. Backend → Appelle API FlexPay (initier le paiement)
4. FlexPay → Envoie USSD au téléphone du client
5. Client → Confirme sur son téléphone
6. FlexPay → POST /webhooks/flexpay (callback)
7. Backend → Met à jour Transaction (status: COMPLETED)
8. Backend → Émet PaymentCompletedEvent
```

### 6.2 Configuration FlexPay

```yaml
flexpay:
  base-url: ${FLEXPAY_BASE_URL}
  merchant: ${FLEXPAY_MERCHANT}
  api-key: ${FLEXPAY_API_KEY}
  callback-url: ${FLEXPAY_CALLBACK_URL}
```

### 6.3 Préfixe téléphone

Le numéro doit être normalisé avec le préfixe `243` (RDC) avant l'appel FlexPay.
Exemples : `0812345678` → `243812345678`

### 6.4 Sécurité Webhook

- Vérification de la signature FlexPay dans le header
- Validation que la transaction existe en base
- Idempotence : ignorer les callbacks déjà traités

---

## 7. Domain Events

- `PaymentInitiatedEvent` : Transaction créée, en attente FlexPay
- `PaymentCompletedEvent` : Paiement confirmé par FlexPay
- `PaymentFailedEvent` : Échec du paiement
- `PaymentRefundedEvent` : Remboursement effectué

---

## 8. Scheduled Tasks

| Scheduler                    | Fréquence      | Description                                    |
|------------------------------|----------------|------------------------------------------------|
| FlexPaySyncScheduler         | Toutes les 5 min | Vérifie le statut des transactions PENDING     |
| CleanupPendingPayments       | Quotidien      | Annule les transactions PENDING > 24h          |
