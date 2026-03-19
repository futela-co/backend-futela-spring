# Phase 1 - User Stories : Shared + Core + Auth/User

> Convention : `{MODULE}-{##}` | Priorité : P0 (critique) → P3 (bonus)

---

## Authentification

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| AUTH-01  | P0       | En tant qu'utilisateur, je veux m'inscrire avec email et mot de passe                         | - Validation email unique - Mot de passe min 8 chars - Création compte avec rôle USER - Envoi code confirmation email    |
| AUTH-02  | P0       | En tant qu'utilisateur, je veux me connecter avec mes identifiants                            | - Retourne access token JWT + refresh token - Crée une DeviceSession - Enregistre lastLoginAt - Erreur si compte inactif |
| AUTH-03  | P0       | En tant qu'utilisateur, je veux rafraîchir mon token expiré                                   | - Valide le refresh token - Rotation : ancien token révoqué, nouveau généré - Erreur si token expiré/révoqué             |
| AUTH-04  | P0       | En tant qu'utilisateur, je veux me déconnecter                                                | - Révoque le refresh token de la session courante - Supprime la DeviceSession                                            |
| AUTH-05  | P1       | En tant qu'utilisateur, je veux me connecter via Google                                       | - Valide le token Google - Crée le compte si nouveau (auto-register) - Retourne JWT si compte existant                   |
| AUTH-06  | P0       | En tant qu'utilisateur connecté, je veux voir mon profil                                      | - Retourne toutes les infos du profil - Inclut companyId et companyName - Inclut les rôles                               |
| AUTH-07  | P1       | En tant qu'utilisateur, je veux modifier mon profil                                           | - Mise à jour firstName, lastName, phone, avatar - Recalcul profileCompleted                                             |
| AUTH-08  | P1       | En tant qu'utilisateur, je veux voir mes sessions actives                                     | - Liste toutes les DeviceSessions actives - Affiche deviceName, deviceType, ipAddress, lastActiveAt                      |
| AUTH-09  | P1       | En tant qu'utilisateur, je veux déconnecter un appareil spécifique                            | - Révoque le refresh token de la session - Supprime la DeviceSession ciblée                                              |
| AUTH-10  | P2       | En tant qu'utilisateur, je veux déconnecter tous mes appareils                                | - Révoque tous les refresh tokens - Supprime toutes les DeviceSessions sauf la courante                                  |
| AUTH-11  | P1       | En tant qu'utilisateur, je veux confirmer mon email via un code                               | - Valide le code envoyé par email - Met à jour emailVerifiedAt et isVerified                                             |
| AUTH-12  | P2       | En tant qu'utilisateur, je veux confirmer mon numéro de téléphone                             | - Valide le code envoyé par SMS - Met à jour le statut de vérification téléphone                                         |

---

## Sécurité

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| SEC-01   | P0       | En tant que système, je veux bloquer les comptes après 5 tentatives échouées                  | - Compteur de tentatives par email - Blocage 15 min après 5 échecs - Reset du compteur après connexion réussie           |
| SEC-02   | P0       | En tant que système, je veux valider le tenant sur chaque requête authentifiée                 | - Extraction companyId du JWT - Activation du TenantFilter Hibernate - Rejet si tenant incohérent                        |
| SEC-03   | P0       | En tant que système, je veux filtrer automatiquement les entités soft-deleted                  | - SoftDeleteFilter activé par défaut - `deleted_at IS NULL` sur toutes les requêtes                                      |
| SEC-04   | P0       | En tant que système, je veux appliquer le rate limiting                                       | - 100 req/min pour les endpoints publics - 300 req/min pour les endpoints authentifiés - 429 Too Many Requests            |

---

## Core (Company)

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| CORE-01  | P0       | En tant que super-admin, je veux créer une nouvelle agence (Company)                          | - Validation nom unique - Génération slug automatique - Création Company avec isActive=true                              |
| CORE-02  | P1       | En tant que super-admin, je veux activer/désactiver une agence                                | - Toggle isActive - Si désactivée : tous les users de la company ne peuvent plus se connecter                            |
| CORE-03  | P2       | En tant que super-admin, je veux gérer les paramètres de la plateforme                        | - CRUD sur PlatformSettings - Clé unique par paramètre                                                                  |

---

## Infrastructure technique

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| INFRA-01 | P0       | En tant que développeur, je veux un BaseEntity avec UUID, timestamps et soft delete            | - id UUID auto-généré - createdAt/updatedAt via @PrePersist/@PreUpdate - deletedAt nullable pour soft delete              |
| INFRA-02 | P0       | En tant que développeur, je veux un TenantAwareEntity qui étend BaseEntity                    | - Ajoute relation ManyToOne vers Company - Utilisé par toutes les entités multi-tenant                                   |
| INFRA-03 | P0       | En tant que développeur, je veux un format de réponse API standard                            | - ApiResponse<T> avec success, data, error, timestamp - PagedResponse<T> pour les listes paginées                        |
| INFRA-04 | P0       | En tant que développeur, je veux un GlobalExceptionHandler centralisé                         | - Mapping exception → HTTP status - Format d'erreur avec code, message, details - Pas de stack traces en prod            |
| INFRA-05 | P0       | En tant que développeur, je veux la documentation OpenAPI/Swagger                             | - Swagger UI accessible à /swagger-ui.html - Schémas auto-générés depuis les DTOs                                       |
| INFRA-06 | P0       | En tant que développeur, je veux des migrations Flyway                                        | - V001 pour le schéma initial (Company, User, Auth tables) - Versionnées et reproductibles                               |
