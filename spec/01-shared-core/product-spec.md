# Phase 1 - Shared + Core + Auth/User

> Fondations du projet : patterns partagés, multi-tenancy (Company), authentification JWT et gestion des utilisateurs.

---

## 1. Vision

Mettre en place les fondations techniques sur lesquelles reposent tous les autres modules :
- **Shared** : BaseEntity, Soft Delete, Tenant Filter, ApiResponse, Pagination
- **Core** : Company (tenant root), PlatformSettings
- **Auth** : JWT tokens, refresh tokens, device sessions, Google OAuth
- **User** : Profils utilisateurs avec rôles et vérification

---

## 2. Modèles du Domaine

### 2.1 Company (Tenant Root)

| Champ         | Type     | Contrainte       | Description                    |
|---------------|----------|------------------|--------------------------------|
| id            | UUID     | PK               | Identifiant unique             |
| name          | String   | Not Null         | Nom de l'agence                |
| slug          | String   | Unique, Not Null | URL-friendly identifier        |
| email         | String   | Nullable         | Email de l'agence              |
| phone         | String   | Nullable         | Téléphone                      |
| logo          | String   | Nullable         | URL du logo                    |
| isActive      | Boolean  | Not Null         | Agence active                  |
| createdAt     | Instant  | Not Null         | Date de création               |
| updatedAt     | Instant  | Not Null         | Dernière modification          |

### 2.2 PlatformSetting

| Champ         | Type     | Contrainte       | Description                    |
|---------------|----------|------------------|--------------------------------|
| id            | UUID     | PK               | Identifiant unique             |
| key           | String   | Unique, Not Null | Clé du paramètre               |
| value         | String   | Not Null         | Valeur (JSON pour les complexes)|
| category      | String   | Not Null         | Catégorie                      |
| description   | String   | Nullable         | Description                    |
| updatedAt     | Instant  | Not Null         | Dernière modification          |

### 2.3 User

| Champ            | Type       | Contrainte       | Description                    |
|------------------|------------|------------------|--------------------------------|
| id               | UUID       | PK               | Identifiant unique             |
| email            | String     | Unique, Not Null | Email de connexion             |
| passwordHash     | String     | Not Null         | Mot de passe (bcrypt)          |
| firstName        | String     | Not Null         | Prénom                         |
| lastName         | String     | Not Null         | Nom                            |
| phone            | String     | Nullable         | Téléphone (+243...)            |
| avatar           | String     | Nullable         | URL photo de profil            |
| role             | UserRole   | Not Null         | Rôle utilisateur               |
| status           | UserStatus | Not Null         | Statut du compte               |
| isVerified       | Boolean    | Not Null         | Email vérifié                  |
| isAvailable      | Boolean    | Not Null         | Disponible                     |
| profileCompleted | Boolean    | Not Null         | Profil complet                 |
| emailVerifiedAt  | Instant    | Nullable         | Date vérification email        |
| lastLoginAt      | Instant    | Nullable         | Dernière connexion             |
| companyId        | UUID       | FK → Company     | Tenant                         |
| createdAt        | Instant    | Not Null         | Date de création               |
| updatedAt        | Instant    | Not Null         | Dernière modification          |
| deletedAt        | Instant    | Nullable         | Soft delete                    |

### 2.4 DeviceSession

| Champ         | Type     | Contrainte       | Description                    |
|---------------|----------|------------------|--------------------------------|
| id            | UUID     | PK               | Identifiant unique             |
| userId        | UUID     | FK → User        | Utilisateur                    |
| deviceName    | String   | Not Null         | Nom de l'appareil              |
| deviceType    | String   | Nullable         | Type (mobile, desktop, etc.)   |
| ipAddress     | String   | Nullable         | Adresse IP                     |
| userAgent     | String   | Nullable         | User-Agent du navigateur       |
| lastActiveAt  | Instant  | Not Null         | Dernière activité              |
| createdAt     | Instant  | Not Null         | Date de création               |

### 2.5 RefreshToken

| Champ         | Type     | Contrainte       | Description                    |
|---------------|----------|------------------|--------------------------------|
| id            | UUID     | PK               | Identifiant unique             |
| token         | String   | Unique, Not Null | Token opaque (UUID)            |
| userId        | UUID     | FK → User        | Utilisateur                    |
| deviceSessionId | UUID   | FK → DeviceSession | Session associée             |
| expiresAt     | Instant  | Not Null         | Date d'expiration              |
| revokedAt     | Instant  | Nullable         | Date de révocation             |
| createdAt     | Instant  | Not Null         | Date de création               |

---

## 3. Enums

### UserRole
```java
public enum UserRole {
    SUPER_ADMIN,  // Gestion globale multi-tenant
    ADMIN,        // Admin d'une agence (company)
    OWNER,        // Propriétaire de biens
    TENANT,       // Locataire
    USER          // Utilisateur standard
}
```

### UserStatus
```java
public enum UserStatus {
    ACTIVE,       // Compte actif
    INACTIVE,     // Compte inactif
    SUSPENDED     // Compte suspendu
}
```

---

## 4. Use Cases

### Auth (12 use cases)

| Use Case                    | Description                                    |
|-----------------------------|------------------------------------------------|
| LoginUseCase                | Connexion email + mot de passe                 |
| RegisterUseCase             | Inscription d'un nouvel utilisateur            |
| RefreshAccessTokenUseCase   | Rafraîchir le token JWT                        |
| LogoutUseCase               | Déconnexion (invalider refresh token)          |
| GoogleAuthUseCase           | Connexion via Google OAuth                     |
| GetCurrentUserUseCase       | Récupérer le profil connecté                   |
| GetActiveDevicesUseCase     | Lister les sessions actives                    |
| LogoutDeviceUseCase         | Déconnecter un appareil spécifique             |
| RevokeAllSessionsUseCase    | Révoquer toutes les sessions                   |
| ConfirmEmailUseCase         | Confirmer email via code                       |
| SendEmailCodeUseCase        | Envoyer code de confirmation email             |
| SendPhoneCodeUseCase        | Envoyer code de confirmation téléphone         |

---

## 5. Endpoints API

### 5.1 Authentification

| Méthode | Path                          | Description                         | Auth |
|---------|-------------------------------|-------------------------------------|------|
| POST    | /api/v1/auth/login            | Connexion                           | Non  |
| POST    | /api/v1/auth/register         | Inscription                         | Non  |
| POST    | /api/v1/auth/refresh          | Rafraîchir le token                 | Non  |
| POST    | /api/v1/auth/logout           | Déconnexion                         | Oui  |
| POST    | /api/v1/auth/google           | Connexion Google                    | Non  |
| GET     | /api/v1/auth/me               | Profil connecté                     | Oui  |
| PUT     | /api/v1/auth/me               | Modifier son profil                 | Oui  |
| GET     | /api/v1/auth/devices          | Sessions actives                    | Oui  |
| DELETE  | /api/v1/auth/devices/{id}     | Déconnecter un appareil             | Oui  |
| DELETE  | /api/v1/auth/devices          | Révoquer toutes les sessions        | Oui  |
| POST    | /api/v1/auth/confirm-email    | Confirmer email                     | Oui  |
| POST    | /api/v1/auth/confirm-phone    | Confirmer téléphone                 | Oui  |
| POST    | /api/v1/auth/send-email-code  | Envoyer code email                  | Oui  |
| POST    | /api/v1/auth/send-phone-code  | Envoyer code téléphone              | Oui  |

---

## 6. Shared Patterns à implémenter

### 6.1 BaseEntity

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    // @PrePersist, @PreUpdate, softDelete(), isDeleted()
}
```

### 6.2 TenantAwareEntity

```java
@MappedSuperclass
public abstract class TenantAwareEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;
}
```

### 6.3 Hibernate Filters

- **TenantFilter** : `WHERE company_id = :companyId`
- **SoftDeleteFilter** : `WHERE deleted_at IS NULL`

### 6.4 ApiResponse Envelope

```java
public record ApiResponse<T>(boolean success, T data, String message, ErrorDetail error, Instant timestamp)
```

### 6.5 PagedResponse

```java
public record PagedResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages)
```

### 6.6 GlobalExceptionHandler

- ResourceNotFoundException → 404
- DuplicateResourceException → 409
- ValidationException → 400
- UnauthorizedException → 401
- TenantMismatchException → 403
- Generic → 500

---

## 7. Sécurité JWT

### Configuration

| Paramètre              | Valeur     |
|-------------------------|-----------|
| Algorithme              | HS256     |
| Access Token TTL        | 15 min    |
| Refresh Token TTL       | 7 jours   |
| Password Hashing        | bcrypt 12 |
| Account Lockout         | 5 échecs → 15 min |

### Classes à implémenter

- `JwtTokenProvider` : Génération et validation des tokens
- `JwtAuthenticationFilter` : Extraction et validation par requête
- `JwtProperties` : Configuration via application.yml
- `CustomUserDetailsService` : Chargement utilisateur depuis la BDD
- `SecurityConfiguration` : Configuration Spring Security
- `RolePermissions` : Mapping rôle → permissions
