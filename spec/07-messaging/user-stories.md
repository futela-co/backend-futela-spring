# Phase 7 - User Stories : Messaging

> Convention : `{MODULE}-{##}` | Priorité : P0 (critique) → P3 (bonus)

---

## Conversation & Message

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| MSG-01   | P0       | En tant qu'utilisateur, je veux créer une conversation avec un propriétaire                   | - Vérifie que les 2 participants existent - Si conversation existante entre les 2 users : retourne celle-ci - Optionnel : lier à une propertyId |
| MSG-02   | P0       | En tant qu'utilisateur, je veux envoyer un message dans une conversation                      | - Contenu non vide - Type : TEXT, IMAGE, FILE - Status initial : SENT - Met à jour lastMessageAt de la conversation - Émission MessageSentEvent |
| MSG-03   | P0       | En tant qu'utilisateur, je veux voir mes conversations                                        | - Triées par lastMessageAt desc (plus récente en premier) - Inclut dernier message, autre participant, property info - Indique le nombre de messages non lus par conversation |
| MSG-04   | P0       | En tant qu'utilisateur, je veux voir les messages d'une conversation                          | - Paginés (les plus récents d'abord) - Seul un participant de la conversation peut accéder - Marque automatiquement les messages reçus comme DELIVERED |
| MSG-05   | P1       | En tant qu'utilisateur, je veux marquer un message comme lu                                   | - Status → READ - readAt = now - Émission MessageReadEvent                                                               |
| MSG-06   | P1       | En tant qu'utilisateur, je veux supprimer un message                                          | - Soft delete (visible uniquement par l'expéditeur) - Le destinataire ne voit plus le message                            |
| MSG-07   | P1       | En tant qu'utilisateur, je veux archiver une conversation                                     | - isArchived = true - N'apparaît plus dans la liste principale - Accessible via filtre "archivées"                       |
| MSG-08   | P2       | En tant qu'utilisateur, je veux rechercher dans mes conversations                             | - Recherche textuelle dans les messages - Filtre par participant                                                         |
| MSG-09   | P0       | En tant qu'utilisateur, je veux voir le nombre de messages non lus                            | - Count total de messages avec status != READ où je suis le destinataire                                                 |

---

## Notification

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| NOTIF-01 | P0       | En tant qu'utilisateur, je veux recevoir des notifications in-app                             | - Créées automatiquement par les domain events - Type : RESERVATION, PAYMENT, MESSAGE, SYSTEM - Status initial : UNREAD  |
| NOTIF-02 | P0       | En tant qu'utilisateur, je veux voir mes notifications                                        | - Paginées, triées par date desc - Filtrable par type et status - Inclut titre, corps, type, date                        |
| NOTIF-03 | P0       | En tant qu'utilisateur, je veux marquer une notification comme lue                            | - Status → READ, readAt = now                                                                                            |
| NOTIF-04 | P1       | En tant qu'utilisateur, je veux marquer toutes mes notifications comme lues                   | - Bulk update status → READ pour toutes les UNREAD de l'utilisateur                                                     |
| NOTIF-05 | P0       | En tant qu'utilisateur, je veux voir le compteur de notifications non lues                    | - Count de notifications UNREAD - Utilisé pour le badge dans l'interface                                                 |
| NOTIF-06 | P2       | En tant qu'utilisateur, je veux supprimer une notification                                    | - Soft delete                                                                                                            |
| NOTIF-07 | P2       | En tant qu'utilisateur, je veux recevoir des notifications push sur mobile                    | - Intégration Firebase Cloud Messaging (FCM) - Envoi asynchrone après création notification                              |
| NOTIF-08 | P2       | En tant que système, je veux envoyer des notifications par email                              | - Pour les événements critiques (paiement, réservation) - Template email avec le détail                                  |
| NOTIF-09 | P2       | En tant que système, je veux envoyer des SMS pour les rappels de loyer                        | - Uniquement pour ReminderType AFTER_DUE - Numéro de téléphone du locataire                                             |

---

## Contact (Formulaire)

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| CONT-01  | P1       | En tant que visiteur non-inscrit, je veux soumettre un formulaire de contact                  | - Pas d'authentification requise - Validation : firstName, lastName, email, subject, message obligatoires - Status : NEW - Émission ContactFormSubmittedEvent |
| CONT-02  | P1       | En tant qu'admin, je veux voir les soumissions de contact                                     | - Liste paginée, triée par date desc - Filtrable par status (NEW, RESPONDED, ARCHIVED)                                   |
| CONT-03  | P2       | En tant qu'admin, je veux répondre à un formulaire de contact                                 | - Envoie un email de réponse - Status → RESPONDED - respondedAt = now, respondedBy = admin courant                       |
