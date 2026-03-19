# FUTELA - Spécification Backend Spring Boot

> Migration complète du backend Futela de Symfony 7.4 / PHP 8.4 vers Spring Boot 3.4 / Java 21.
> Plateforme immobilière multi-tenant (SaaS) pour la RDC.

---

## 1. Vision

Un **projet Spring Boot unique** exposant une API REST qui sert l'application mobile et le dashboard web :

- **API Mobile** : Endpoints pour les locataires, propriétaires et visiteurs
- **API Admin** : Endpoints CRUD protégés par JWT pour la gestion des propriétés, baux et paiements

L'architecture suit le pattern **hexagonal (ports & adapters)** avec support **multi-tenant** (une instance, plusieurs agences immobilières).

---

## 2. Stack Technique

| Technologie       | Version | Rôle                                    |
|-------------------|---------|-----------------------------------------|
| Java              | 21 LTS  | Langage (Virtual Threads)               |
| Spring Boot       | 3.4+    | Framework principal                     |
| Spring Security   | 6.x     | Authentification JWT + RBAC             |
| Spring Data JPA   | 3.x     | Accès données (Hibernate 6)             |
| PostgreSQL        | 16+     | Base de données relationnelle           |
| Flyway            | 10+     | Migrations de schéma                    |
| MapStruct         | 1.6+    | Mapping DTO compile-time                |
| Lombok            | 1.18+   | Réduction du boilerplate                |
| jjwt              | 0.12+   | Génération/validation JWT               |
| SpringDoc OpenAPI | 2.8+    | Documentation API (Swagger UI)          |
| Redis             | 7+      | Cache applicatif                        |
| Cloudinary        | 2.x     | Stockage fichiers/photos                |
| WebSocket/SSE     | -       | Notifications temps réel                |
| Maven             | 3.9+    | Build tool                              |

---

## 3. Modules Métier

| # | Module          | Description                                           | Entités Principales                |
|---|-----------------|-------------------------------------------------------|------------------------------------|
| 1 | **Core**        | Company (tenant root), PlatformSettings               | Company, PlatformSetting           |
| 2 | **User/Auth**   | Utilisateurs, JWT, sessions, Google OAuth              | User, DeviceSession, RefreshToken  |
| 3 | **Address**     | Hiérarchie géographique RDC                            | Country→Province→City→Town→District→Address |
| 4 | **Category**    | Catégorisation des propriétés                          | Category                           |
| 5 | **Property**    | Gestion propriétés (5 types STI)                       | Property, Apartment, House, Land, Car, EventHall, Photo, Listing |
| 6 | **Reservation** | Réservations courte durée + visites                    | Reservation, Visit                 |
| 7 | **Rent**        | Baux longue durée, facturation, paiements              | Lease, RentInvoice, RentPayment, PaymentSchedule, RentReminder |
| 8 | **Payment**     | Transactions FlexPay (mobile money)                    | Transaction, Currency, PaymentMethod |
| 9 | **Messaging**   | Conversations, messages, notifications, contacts       | Conversation, Message, Notification, Contact |
| 10| **Review**      | Avis et notes sur les propriétés                       | Review                             |

---

## 4. Multi-Tenancy

### Architecture

- **Company** = Tenant root (agence immobilière)
- Chaque entité tenant-aware porte un `companyId`
- Filtrage automatique via **Hibernate Filter** `@FilterDef` / `@Filter`
- Le tenant est extrait du JWT token à chaque requête

### Règles

1. Toutes les entités métier (sauf Address, Currency) sont tenant-aware
2. Un utilisateur appartient à une seule Company
3. Les données sont strictement isolées entre tenants
4. Le super-admin peut accéder à tous les tenants

---

## 5. Sécurité

### Authentification JWT

- **Access Token** : JWT signé (HS256), durée 15 min
- **Refresh Token** : UUID opaque stocké en base, durée 7 jours
- **Device Sessions** : Suivi multi-appareils avec fingerprint
- **Google OAuth** : Connexion sociale

### Rôles & Permissions

| Rôle            | Permissions                                                  |
|-----------------|--------------------------------------------------------------|
| SUPER_ADMIN     | Gestion de toutes les companies + admin global               |
| ADMIN           | Gestion complète d'une company                               |
| OWNER           | Gestion de ses propriétés, baux, paiements                   |
| TENANT          | Consultation baux, paiements, réservations                   |
| USER            | Navigation, favoris, réservations                            |

### Protection API

- Rate Limiting : 100 req/min public, 300 req/min authentifié
- CORS configuré par domaine
- Jakarta Bean Validation sur tous les DTOs
- Requêtes paramétrées (JPA) contre SQL injection
- Soft delete sur toutes les entités

---

## 6. Conventions de Nommage

### Packages

```
com.futela.api
├── domain/model/{module}/          # Records Java immutables
├── domain/enums/                   # Enums métier
├── domain/port/in/{module}/        # Use case interfaces
├── domain/port/out/{module}/       # Repository port interfaces
├── domain/event/                   # Événements métier
├── domain/exception/               # Exceptions métier
├── application/usecase/{module}/   # Use case implémentations (@Service)
├── application/dto/request/{module}/ # DTOs d'entrée (records)
├── application/dto/response/{module}/ # DTOs de sortie (records)
├── application/mapper/{module}/    # MapStruct mappers
├── application/service/            # Services transverses
├── infrastructure/persistence/entity/{module}/    # Entités JPA
├── infrastructure/persistence/repository/{module}/ # JPA Repositories
├── infrastructure/persistence/adapter/{module}/   # Port adapters
├── infrastructure/persistence/mapper/{module}/    # Entity↔Domain mappers
├── infrastructure/persistence/specification/      # JPA Specifications
├── infrastructure/security/        # JWT, filters, config
├── infrastructure/config/          # Spring configurations
├── infrastructure/integration/     # Services externes (FlexPay, etc.)
├── presentation/controller/{module}/ # REST controllers
├── presentation/advice/            # Exception handlers
└── presentation/filter/            # Request filters
```

### Naming Patterns

| Élément              | Pattern                          | Exemple                            |
|----------------------|----------------------------------|------------------------------------|
| Domain Model         | `{Entity}`                       | `Property`, `Lease`                |
| Enum                 | `{Entity}{Concept}`              | `PropertyStatus`, `LeaseStatus`    |
| Use Case Interface   | `{Action}{Entity}UseCase`        | `CreatePropertyUseCase`            |
| Use Case Impl        | `{Action}{Entity}Service`        | `CreatePropertyService`            |
| Repository Port      | `{Entity}RepositoryPort`         | `PropertyRepositoryPort`           |
| JPA Entity           | `{Entity}Entity`                 | `PropertyEntity`                   |
| JPA Repository       | `Jpa{Entity}Repository`          | `JpaPropertyRepository`            |
| Repository Adapter   | `{Entity}RepositoryAdapter`      | `PropertyRepositoryAdapter`        |
| Persistence Mapper   | `{Entity}PersistenceMapper`      | `PropertyPersistenceMapper`        |
| Request DTO          | `{Action}{Entity}Request`        | `CreatePropertyRequest`            |
| Response DTO         | `{Entity}Response`               | `PropertyResponse`                 |
| Controller           | `{Entity}Controller`             | `PropertyController`               |
| MapStruct Mapper     | `{Entity}Mapper`                 | `PropertyMapper`                   |
| JPA Specification    | `{Entity}Specification`          | `PropertySpecification`            |

---

## 7. Phases d'Implémentation

| Phase | Dossier Spec            | Modules                                | Priorité |
|-------|-------------------------|-----------------------------------------|----------|
| 1     | `01-shared-core`        | Shared patterns + Core (Company) + User/Auth | P0       |
| 2     | `02-address-category`   | Address (hiérarchie géo) + Category      | P0       |
| 3     | `03-property`           | Property (5 types STI) + Photo + Listing | P0       |
| 4     | `04-reservation-review` | Reservation + Visit + Review             | P1       |
| 5     | `05-rent`               | Lease + Invoice + Payment + Schedule     | P1       |
| 6     | `06-payment`            | Transaction + Currency + FlexPay         | P1       |
| 7     | `07-messaging`          | Conversation + Message + Notification + Contact | P2   |

---

## 8. Contraintes Techniques

| Contrainte              | Valeur                                               |
|-------------------------|------------------------------------------------------|
| Format API              | `application/json` (pas JSON-LD)                     |
| Identifiants            | UUID v4 partout                                      |
| Timestamps              | `Instant` (UTC) pour createdAt/updatedAt             |
| Soft Delete             | `deletedAt` nullable sur toutes les entités          |
| Pagination              | `page`, `size`, `sort` avec max 500 items            |
| Réponse standard        | `ApiResponse<T>` envelope `{ success, data, error }` |
| Validation              | Jakarta Bean Validation (`@Valid`)                    |
| Tests                   | JUnit 5 + Mockito + H2 (intégration)                |
| Documentation           | SpringDoc OpenAPI (Swagger UI)                       |
| Logs                    | SLF4J + Logback (JSON en production)                 |
| Monitoring              | Spring Boot Actuator + Micrometer                    |
| Messages d'erreur       | Français                                             |
