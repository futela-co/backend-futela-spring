# Phase 4 - User Stories : Reservation + Review

> Convention : `{MODULE}-{##}` | Priorité : P0 (critique) → P3 (bonus)

---

## Reservation

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| RES-01   | P0       | En tant qu'utilisateur, je veux réserver une propriété pour des dates spécifiques             | - Validation : dates futures, endDate > startDate - Vérification pas de chevauchement avec réservations existantes - Calcul automatique du prix total - Statut initial : PENDING - Émission ReservationCreatedEvent |
| RES-02   | P0       | En tant que propriétaire, je veux confirmer une réservation                                   | - Transition PENDING → CONFIRMED - Seul le propriétaire de la propriété peut confirmer - Émission ReservationConfirmedEvent |
| RES-03   | P0       | En tant qu'utilisateur, je veux annuler ma réservation                                       | - Transition PENDING/CONFIRMED → CANCELLED - cancelReason obligatoire - L'utilisateur ou le propriétaire peut annuler    |
| RES-04   | P1       | En tant que propriétaire, je veux marquer une réservation comme terminée                     | - Transition CONFIRMED → COMPLETED - Seul le propriétaire peut compléter                                                |
| RES-05   | P0       | En tant qu'utilisateur, je veux voir le détail d'une réservation                             | - Accès si userId = user courant OU ownerId de la propriété = user courant - Inclut property info, dates, prix, statut   |
| RES-06   | P1       | En tant que système, je veux vérifier la disponibilité d'une propriété                       | - Pas de réservation CONFIRMED ou PENDING qui chevauche les dates demandées                                              |

---

## Visit

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| VIS-01   | P1       | En tant qu'utilisateur, je veux programmer une visite d'une propriété                         | - scheduledAt doit être dans le futur - Statut initial : SCHEDULED - Notification au propriétaire                         |
| VIS-02   | P1       | En tant que propriétaire, je veux confirmer une visite                                        | - Transition SCHEDULED → CONFIRMED - Notification au visiteur                                                            |
| VIS-03   | P1       | En tant qu'utilisateur ou propriétaire, je veux annuler une visite                            | - Transition SCHEDULED/CONFIRMED → CANCELLED                                                                             |

---

## Review

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| REV-01   | P1       | En tant qu'utilisateur, je veux laisser un avis sur une propriété                             | - Rating obligatoire (1-5) - Comment optionnel - Un seul avis par user par property - Le propriétaire ne peut pas s'auto-évaluer - isApproved = false par défaut |
| REV-02   | P1       | En tant qu'utilisateur, je veux voir les avis d'une propriété                                 | - Uniquement les avis approuvés (isApproved = true) - Triés par date décroissante - Paginés - Note moyenne incluse       |
| REV-03   | P1       | En tant qu'utilisateur, je veux supprimer mon avis                                            | - Soft delete - Seul l'auteur ou un admin peut supprimer                                                                |
| REV-04   | P2       | En tant qu'admin, je veux approuver un avis                                                   | - Transition isApproved = true - L'avis devient visible publiquement                                                    |
| REV-05   | P2       | En tant qu'admin, je veux rejeter un avis                                                     | - Soft delete de l'avis rejeté                                                                                          |
| REV-06   | P2       | En tant qu'utilisateur, je veux signaler un avis inapproprié                                  | - isFlagged = true - Notification admin - Émission ReviewFlaggedEvent                                                   |
| REV-07   | P2       | En tant qu'utilisateur, je veux voir tous mes avis                                            | - Liste paginée de mes avis (tous statuts) - Triés par date                                                             |
