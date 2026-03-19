# Phase 6 - User Stories : Payment (FlexPay)

> Convention : `{MODULE}-{##}` | Priorité : P0 (critique) → P3 (bonus)

---

## Transaction

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| PAY-01   | P0       | En tant qu'utilisateur, je veux payer via mobile money (FlexPay)                              | - Montant > 0 - Numéro téléphone normalisé (+243) - Création Transaction PENDING - Appel API FlexPay - Retourne transactionId pour suivi |
| PAY-02   | P0       | En tant que système, je veux recevoir la confirmation FlexPay via webhook                     | - Vérification signature du webhook - Mise à jour Transaction PENDING → COMPLETED - Idempotence (ignorer double callback) - Émission PaymentCompletedEvent |
| PAY-03   | P0       | En tant que système, je veux gérer les échecs de paiement                                    | - Si FlexPay retourne un échec → Transaction FAILED - Enregistrement failureReason - Émission PaymentFailedEvent         |
| PAY-04   | P1       | En tant qu'utilisateur, je veux annuler un paiement en attente                               | - Uniquement si status = PENDING - Transition → CANCELLED - Si déjà traité par FlexPay : erreur                          |
| PAY-05   | P1       | En tant qu'admin, je veux initier un remboursement                                           | - Transaction originale doit être COMPLETED - Crée une nouvelle Transaction type REFUND - Appel API FlexPay refund        |
| PAY-06   | P0       | En tant qu'utilisateur, je veux voir le détail d'une transaction                             | - Inclut : montant, devise, statut, date, numéro tel, provider - Inclut userInfo (nom, email)                            |
| PAY-07   | P0       | En tant qu'utilisateur, je veux voir l'historique de mes transactions                        | - Liste paginée, triée par date desc - Filtrable par type (PAYMENT/REFUND) et status                                    |
| PAY-08   | P1       | En tant qu'admin, je veux voir les transactions en attente                                   | - Filtre status = PENDING - Inclut durée en attente - Alerte si > 30 min                                                |

---

## Currency

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| CUR-01   | P0       | En tant qu'utilisateur, je veux voir les devises disponibles                                  | - Liste des devises actives - Inclut code, nom, symbole, taux                                                           |
| CUR-02   | P1       | En tant qu'admin, je veux créer une devise                                                    | - Code unique (ISO) - Taux de change par rapport au USD - isActive par défaut true                                       |
| CUR-03   | P1       | En tant qu'admin, je veux mettre à jour le taux de change                                    | - Modification du exchangeRate - Historique conservé (updatedAt)                                                         |
| CUR-04   | P1       | En tant qu'utilisateur, je veux convertir un montant entre devises                            | - Paramètres : from, to, amount - Calcul via exchangeRate - Retourne le montant converti et le taux utilisé              |

---

## Synchronisation FlexPay

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| SYNC-01  | P1       | En tant que système, je veux vérifier périodiquement les transactions en attente               | - Toutes les 5 min - Appel API FlexPay pour vérifier le statut - Mise à jour en base si changement                       |
| SYNC-02  | P1       | En tant que système, je veux nettoyer les transactions abandonnées                            | - Quotidien - Transactions PENDING > 24h → CANCELLED - Log des transactions nettoyées                                    |
