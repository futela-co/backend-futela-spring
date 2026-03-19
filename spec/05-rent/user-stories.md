# Phase 5 - User Stories : Rent

> Convention : `{MODULE}-{##}` | Priorité : P0 (critique) → P3 (bonus)

---

## Lease (Bail)

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| LEASE-01 | P0       | En tant que propriétaire, je veux créer un bail pour une de mes propriétés                    | - Propriété doit être PUBLISHED - Pas de bail ACTIVE existant pour cette propriété - Locataire doit exister - Génération automatique de l'échéancier (PaymentSchedule) - Property passe en status RENTED - Émission LeaseCreatedEvent |
| LEASE-02 | P0       | En tant que propriétaire, je veux voir tous mes baux                                          | - Filtre par landlordId (auto depuis JWT) - Inclut info propriété et locataire - Paginé, filtrable par status            |
| LEASE-03 | P0       | En tant que locataire, je veux voir mes baux                                                  | - Filtre par tenantId (auto depuis JWT) - Inclut info propriété et propriétaire - Paginé                                |
| LEASE-04 | P0       | En tant que propriétaire, je veux voir le détail d'un bail                                    | - Inclut : propriété, locataire, factures, paiements, échéancier - Accès si landlord ou tenant du bail                   |
| LEASE-05 | P1       | En tant que propriétaire, je veux renouveler un bail                                          | - Bail actuel doit être ACTIVE - Crée un nouveau bail avec nouvelles dates - Ancien bail passe à RENEWED - Nouveau prix possible |
| LEASE-06 | P1       | En tant que propriétaire, je veux résilier un bail                                            | - terminationReason obligatoire - Statut → TERMINATED - terminatedAt = now - Propriété repasse en PUBLISHED              |

---

## RentInvoice (Facture)

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| INV-01   | P0       | En tant que système, je veux générer les factures mensuelles automatiquement                  | - Scheduler le 1er du mois - Pour chaque bail ACTIVE - Numéro facture auto-incrémenté (FUT-YYYY-MMDD-####) - Statut PENDING - Émission RentInvoiceGeneratedEvent |
| INV-02   | P0       | En tant que propriétaire, je veux voir les factures d'un bail                                 | - Liste paginée des factures - Triées par dueDate desc - Inclut montant dû, payé, statut                                |
| INV-03   | P0       | En tant que propriétaire, je veux voir les factures impayées                                  | - Filtre status = PENDING ou OVERDUE - Toutes propriétés du landlord - Triées par dueDate asc (les plus anciennes en premier) |
| INV-04   | P1       | En tant que propriétaire, je veux voir le détail d'une facture                                | - Inclut : bail, propriété, locataire, paiements liés, pénalités                                                        |

---

## RentPayment (Paiement)

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| PAY-01   | P0       | En tant que locataire, je veux payer une facture                                              | - Montant > 0, montant ≤ reste dû - Mise à jour paidAmount sur la facture - Si paidAmount >= amount → status PAID - Si paidAmount < amount → status PARTIAL - Émission RentPaymentReceivedEvent |
| PAY-02   | P0       | En tant que propriétaire, je veux enregistrer un paiement reçu manuellement                  | - Même logique que PAY-01 - Possibilité d'ajouter method et reference - Notes optionnelles                               |
| PAY-03   | P0       | En tant que propriétaire, je veux voir l'historique des paiements d'un bail                   | - Liste paginée des RentPayment - Triés par paymentDate desc - Total cumulé                                              |

---

## Dashboard & Reports

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| DASH-01  | P0       | En tant que propriétaire, je veux voir mon dashboard avec les KPIs clés                       | - totalProperties, propertiesRented, monthlyIncome, yearlyIncome - overduePayments, pendingPayments, occupancyRate        |
| DASH-02  | P1       | En tant que propriétaire, je veux voir mon rapport de revenus mensuels                        | - Revenus par mois sur les 12 derniers mois - Graphique de tendance                                                     |
| DASH-03  | P0       | En tant que propriétaire, je veux voir les paiements en retard                                | - Factures OVERDUE avec nb jours de retard - Infos locataire et propriété - Triés par ancienneté                         |
| DASH-04  | P1       | En tant que propriétaire, je veux voir les paiements en attente                               | - Factures PENDING dont dueDate approche - Dans les 30 prochains jours                                                  |
| DASH-05  | P2       | En tant que propriétaire, je veux voir la performance de chaque propriété                     | - Revenus cumulés, taux d'occupation, historique des baux par propriété                                                  |

---

## Rappels automatiques

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| REM-01   | P1       | En tant que système, je veux envoyer un rappel 3 jours avant l'échéance                      | - Type BEFORE_DUE - Envoi notification au locataire (push/email) - Enregistrement dans RentReminder                      |
| REM-02   | P1       | En tant que système, je veux envoyer un rappel le jour de l'échéance                         | - Type ON_DUE - Si la facture est toujours PENDING                                                                      |
| REM-03   | P1       | En tant que système, je veux envoyer un rappel après l'échéance                              | - Type AFTER_DUE - Envoi au locataire ET au propriétaire - Transition facture PENDING → OVERDUE                          |
| REM-04   | P2       | En tant que propriétaire, je veux voir l'historique des rappels                               | - Liste des rappels envoyés par bail/facture - Date, type, canal                                                         |

---

## Pénalités

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| FEE-01   | P2       | En tant que système, je veux calculer les pénalités de retard                                 | - 5% du montant après 7 jours de retard - Ajouté au champ lateFee de la facture - Recalculé hebdomadairement             |
