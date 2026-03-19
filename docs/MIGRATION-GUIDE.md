# Guide de Migration Symfony → Spring Boot

> Document de référence pour la migration de Futela Backend.
> Réutilisable pour tout projet Symfony/API Platform → Spring Boot.

---

## 1. Vue d'ensemble

| Aspect | Symfony (Source) | Spring Boot (Cible) |
|--------|-----------------|---------------------|
| **Langage** | PHP 8.4 | Java 21 |
| **Framework** | Symfony 7.4 + API Platform 4.x | Spring Boot 3.4 + Spring Data JPA |
| **ORM** | Doctrine ORM | Hibernate 6 (JPA) |
| **Base de données** | PostgreSQL 16 | PostgreSQL 16 |
| **Auth** | Lexik JWT | jjwt + Spring Security |
| **Migrations** | Doctrine Migrations | Flyway |
| **API** | API Platform (auto-generated) | REST Controllers (manuels) |
| **Architecture** | Hexagonale (PHP) | Hexagonale (Java) |

---

## 2. Mapping Architecture Symfony → Spring Boot

### 2.1 Structure des dossiers

| Symfony | Spring Boot | Description |
|---------|-------------|-------------|
| `src/Domain/Entity/{Module}/` | `domain/model/{module}/` | Records Java immutables (pas d'annotations framework) |
| `src/Domain/Entity/{Module}/Enum/` | `domain/enums/` | Enums métier |
| `src/Domain/Event/` | `domain/event/` | Événements métier (records) |
| `src/Domain/Service/` | `domain/` (logique dans les records) | Logique métier pure |
| `src/Application/Port/In/{Module}/` | `domain/port/in/{module}/` | Interfaces use cases |
| `src/Application/Port/Out/Repository/` | `domain/port/out/{module}/` | Interfaces repository (ports sortants) |
| `src/Application/UseCase/{Module}/` | `application/usecase/{module}/` | Services `@Service` + `@Transactional` |
| `src/Application/DTO/{Module}/Request/` | `application/dto/request/{module}/` | Records avec Jakarta Validation |
| `src/Application/DTO/{Module}/Response/` | `application/dto/response/{module}/` | Records de sortie |
| `src/Infrastructure/Persistence/Doctrine/Repository/` | `infrastructure/persistence/repository/` | `JpaXxxRepository extends JpaRepository` |
| — | `infrastructure/persistence/adapter/` | Adapters implémentant les ports |
| — | `infrastructure/persistence/mapper/` | Mappers statiques `toDomain()`/`toEntity()` |
| — | `infrastructure/persistence/entity/` | Entités JPA (Lombok) |
| `src/Infrastructure/Service/` | `infrastructure/integration/` | Services externes (FlexPay, Cloudinary, etc.) |
| `src/Infrastructure/EventListener/` | `infrastructure/event/` | Event listeners Spring |
| `src/Infrastructure/Console/Command/` | `infrastructure/scheduler/` | `@Scheduled` tasks |
| `src/Presentation/Resource/` | — | Remplacé par des Controllers |
| `src/Presentation/State/Provider/` | `presentation/controller/` | Méthodes GET dans les controllers |
| `src/Presentation/State/Processor/` | `presentation/controller/` | Méthodes POST/PUT/PATCH/DELETE |
| `src/Shared/Domain/Aggregate/` | `infrastructure/persistence/entity/common/` | BaseEntity, TenantAwareEntity |

### 2.2 Naming Conventions

| Concept | Symfony | Spring Boot |
|---------|---------|-------------|
| Entité | `Property` | Domain: `Property` (record), Infra: `PropertyEntity` (JPA) |
| Repository Interface | `PropertyRepositoryInterface` | `PropertyRepositoryPort` |
| Repository Impl | `DoctrinePropertyRepository` | `PropertyRepositoryAdapter` + `JpaPropertyRepository` |
| Use Case | `CreatePropertyUseCase` (interface + impl) | Interface: `CreatePropertyUseCase`, Impl: `CreatePropertyService` |
| DTO Request | `CreatePropertyRequest` | `CreatePropertyRequest` (record) |
| DTO Response | `PropertyResponse` | `PropertyResponse` (record) |
| Resource | `PropertyResource` (API Platform) | `PropertyController` (REST) |
| Provider | `PropertyItemProvider` | `getPropertyById()` dans le controller |
| Processor | `CreatePropertyProcessor` | `createProperty()` dans le controller |
| Trait | `SoftDeletableTrait` | `BaseEntity` (mapped superclass) |
| Filter | `TenantFilter` (Doctrine) | `@FilterDef` / `@Filter` (Hibernate) |

---

## 3. Mapping des concepts techniques

### 3.1 Entités

**Symfony (Doctrine):**
```php
#[ORM\Entity]
#[ORM\Table(name: 'property')]
#[ORM\HasLifecycleCallbacks]
class Property implements IdentifiableInterface, TimestampableInterface, SoftDeletableInterface
{
    use IdentifiableTrait;      // UUID id
    use TimestampableTrait;     // createdAt, updatedAt
    use SoftDeletableTrait;     // deletedAt

    #[ORM\Column(length: 255)]
    private string $title;
}
```

**Spring Boot (JPA + Lombok):**
```java
@Entity
@Table(name = "properties")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PropertyEntity extends TenantAwareEntity {
    // id, createdAt, updatedAt, deletedAt hérités de BaseEntity
    // company hérité de TenantAwareEntity

    @Column(nullable = false)
    private String title;
}
```

### 3.2 Domain Model (Spring only)

```java
// Record Java pur - AUCUNE annotation framework
public record Property(
    UUID id,
    String title,
    BigDecimal price,
    PropertyStatus status,
    UUID companyId,
    Instant createdAt,
    Instant updatedAt
) {}
```

### 3.3 Persistence Mapper (Spring only)

```java
public final class PropertyPersistenceMapper {
    private PropertyPersistenceMapper() {}

    public static Property toDomain(PropertyEntity entity) {
        return new Property(
            entity.getId(),
            entity.getTitle(),
            entity.getPrice(),
            entity.getStatus(),
            entity.getCompany().getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public static PropertyEntity toEntity(Property domain) {
        PropertyEntity entity = new PropertyEntity();
        entity.setTitle(domain.title());
        entity.setPrice(domain.price());
        entity.setStatus(domain.status());
        return entity;
    }
}
```

### 3.4 API Platform → REST Controller

**Symfony (API Platform Resource):**
```php
#[ApiResource(
    operations: [
        new GetCollection(uriTemplate: '/properties'),
        new Get(uriTemplate: '/properties/{id}'),
        new Post(uriTemplate: '/properties', read: false),
    ],
    input: CreatePropertyRequest::class,
    output: PropertyResponse::class,
)]
class PropertyResource { }
```

**Spring Boot (Controller):**
```java
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {
    private final CreatePropertyUseCase createPropertyUseCase;

    @GetMapping
    public PagedResponse<PropertyResponse> search(...) { ... }

    @GetMapping("/{id}")
    public PropertyResponse getById(@PathVariable UUID id) { ... }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse create(@Valid @RequestBody CreatePropertyRequest request) { ... }
}
```

### 3.5 Single Table Inheritance

**Symfony (Doctrine STI):**
```php
#[ORM\InheritanceType("SINGLE_TABLE")]
#[ORM\DiscriminatorColumn(name: "type", type: "string")]
#[ORM\DiscriminatorMap(["apartment" => Apartment::class, ...])]
abstract class Property { }
```

**Spring Boot (JPA STI):**
```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class PropertyEntity extends TenantAwareEntity { }

@Entity
@DiscriminatorValue("APARTMENT")
public class ApartmentEntity extends PropertyEntity { }
```

### 3.6 Doctrine Filter → Hibernate Filter

**Symfony:**
```php
// doctrine.yaml
doctrine:
    orm:
        filters:
            tenant_filter:
                class: App\Infrastructure\Persistence\Doctrine\Filter\TenantFilter
```

**Spring Boot:**
```java
@MappedSuperclass
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "companyId", type = UUID.class))
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
public abstract class TenantAwareEntity extends BaseEntity { }
```

### 3.7 Console Commands → Scheduled Tasks

**Symfony:**
```php
#[AsCommand(name: 'app:generate-monthly-rent-invoices')]
class GenerateMonthlyRentInvoicesCommand extends Command { }
// cron: 0 0 1 * *
```

**Spring Boot:**
```java
@Component
public class RentInvoiceScheduler {
    @Scheduled(cron = "${scheduler.rent-invoice:0 0 0 1 * *}")
    public void generateMonthlyInvoices() { ... }
}
```

---

## 4. Mapping Base de Données

### 4.1 Conventions de nommage des tables

| Convention | Symfony (Doctrine) | Spring Boot (JPA) |
|------------|-------------------|-------------------|
| Tables | Singulier (`company`) | Pluriel (`companies`) |
| Jointures | `conversation_user` | `conversation_participants` |
| Préfixes | `auth_`, `address_`, `property_` | Pas de préfixe |
| Timestamps | `timestamp(0) without time zone` | `TIMESTAMP WITH TIME ZONE` |
| Money | `NUMERIC(10,2)` | `DECIMAL(12,2)` |
| Enums | VARCHAR stockage libre | VARCHAR avec validation Java |

### 4.2 Colonnes fréquemment renommées

| Symfony | Spring Boot | Raison |
|---------|-------------|--------|
| `code` (company) | `slug` | Convention URL-friendly |
| `password` | `password_hash` | Clarté |
| `roles` (JSON) | `role` (VARCHAR) | Un seul rôle par user |
| `guest_id` | `user_id` | Simplification |
| `visitor_id` | `user_id` | Simplification |
| `external_id` | `external_ref` | Clarté |
| `failed_reason` | `failure_reason` | Convention anglaise |
| `deposit` | `deposit_amount` | Clarté |
| `content` (notification) | `body` | Éviter confusion avec content-type |
| `name` (contact) | `first_name` + `last_name` | Normalisation |

---

## 5. Compatibilité Frontend

### 5.1 Changements critiques à éviter

Le frontend ne doit sentir **aucun changement**. Voici les points de vigilance :

| Aspect | Ce que le frontend attend | Comment Spring Boot le fournit |
|--------|--------------------------|-------------------------------|
| **Préfixe URL** | `/api/` (pas `/api/v1/`) | `@RequestMapping("/api/...")` |
| **Enveloppe réponse** | Données brutes (pas d'enveloppe `{success, data}`) | `SymfonyCompatResponseAdvice` qui unwrap |
| **Pagination** | `{member, totalItems, page, itemsPerPage, totalPages}` | `PagedResponse` customisé |
| **JSON case** | camelCase | Jackson configuré en camelCase |
| **Erreurs** | `{message, code, errors}` | `GlobalExceptionHandler` customisé |
| **Auth header** | `Authorization: Bearer <token>` | `JwtAuthenticationFilter` |
| **PATCH content-type** | `application/merge-patch+json` | `MergePatchJsonConfiguration` |
| **Sous-routes état** | `POST /items/{id}/confirm` | Pas `PATCH`, utiliser `POST` |
| **Port serveur** | `8001` | `server.port: 8001` |

### 5.2 Configuration Jackson

```yaml
spring:
  jackson:
    property-naming-strategy: LOWER_CAMEL_CASE
    default-property-inclusion: non_null
    serialization:
      write-dates-as-timestamps: false
    deserialization:
      fail-on-unknown-properties: false
```

### 5.3 CORS

```yaml
cors:
  allowed-origins: http://localhost:3006,http://localhost:3007
```

---

## 6. Script de Migration des Données

### 6.1 Prérequis

- Les deux BDD sur le même serveur PostgreSQL
- Extension `dblink` installée (`CREATE EXTENSION IF NOT EXISTS dblink;`)
- Flyway migrations appliquées sur la BDD cible

### 6.2 Ordre d'insertion (respect des FK)

```
1. companies (tenant root)
2. platform_settings
3. countries → provinces → cities → towns → districts → addresses
4. users
5. device_sessions → refresh_tokens
6. categories
7. properties → photos → listings
8. reservations → visits
9. reviews
10. leases → rent_invoices → rent_payments → payment_schedules → rent_reminders
11. currencies → payment_methods → transactions
12. conversations → conversation_participants → messages
13. notifications
14. contacts
```

### 6.3 Transformations de données

```sql
-- Timestamps: ajouter timezone UTC
source.created_at AT TIME ZONE 'UTC'

-- Roles JSON → rôle unique
CASE
    WHEN roles::text LIKE '%SUPER_ADMIN%' THEN 'SUPER_ADMIN'
    WHEN roles::text LIKE '%ADMIN%' THEN 'ADMIN'
    WHEN roles::text LIKE '%OWNER%' THEN 'OWNER'
    ELSE 'USER'
END

-- Statuts: majuscules
UPPER(status)

-- Contact: split name
SPLIT_PART(name, ' ', 1) AS first_name
SUBSTRING(name FROM POSITION(' ' IN name) + 1) AS last_name

-- Invoice period: month/year → dates
MAKE_DATE(year, month, 1) AS period_start
(MAKE_DATE(year, month, 1) + INTERVAL '1 month' - INTERVAL '1 day')::date AS period_end
```

---

## 7. Checklist de Migration

### Phase 0 : Préparation
- [ ] Analyser le schéma Symfony complet (`\dt` + `\d+ table`)
- [ ] Compter les lignes par table
- [ ] Identifier les colonnes custom vs standard
- [ ] Lister les endpoints API Platform utilisés par le frontend

### Phase 1 : Fondations Spring Boot
- [ ] Créer le projet Maven avec toutes les dépendances
- [ ] Implémenter BaseEntity, TenantAwareEntity
- [ ] Implémenter ApiResponse, PagedResponse
- [ ] Implémenter GlobalExceptionHandler
- [ ] Implémenter JWT (JwtTokenProvider, Filter, Security Config)
- [ ] Créer tous les enums
- [ ] Créer toutes les exceptions métier
- [ ] Écrire la migration Flyway V001

### Phase 2-N : Modules métier
Pour chaque module :
- [ ] Lire le code Symfony source (Entity, UseCase, DTO, Resource, State)
- [ ] Créer les domain records (modèles purs)
- [ ] Créer les ports (in: use case interfaces, out: repository ports)
- [ ] Créer les domain events
- [ ] Implémenter les use case services
- [ ] Créer les DTOs (request/response records)
- [ ] Créer les entités JPA (Lombok)
- [ ] Créer les JPA repositories
- [ ] Créer les adapters et persistence mappers
- [ ] Créer les controllers REST
- [ ] Écrire la migration Flyway
- [ ] Écrire les tests unitaires
- [ ] Cocher la checklist

### Phase finale : Données et Compatibilité
- [ ] Créer et exécuter le script de migration de données
- [ ] Créer les fixtures/seed data
- [ ] Vérifier la compatibilité frontend (URLs, format, pagination, erreurs)
- [ ] Configurer le port serveur pour matcher le frontend
- [ ] Exécuter `mvn clean test` → tous les tests passent
- [ ] Tester manuellement les endpoints critiques (login, search, paiement)

---

## 8. Pièges courants

| Piège | Solution |
|-------|----------|
| API Platform utilise JSON-LD | Spring retourne du JSON simple — configurer `application/json` |
| Symfony renvoie les données sans enveloppe | Créer un `ResponseBodyAdvice` qui strip l'enveloppe `ApiResponse` |
| Pagination Symfony: `member`, `totalItems` | Customiser `PagedResponse` pour matcher ce format |
| PATCH avec `application/merge-patch+json` | Ajouter un `HttpMessageConverter` custom |
| Symfony utilise POST pour les transitions d'état | Ne pas utiliser PATCH, garder POST |
| `password` column vs `password_hash` | Mapper correctement dans le script de migration |
| `roles` JSON array vs `role` string | Extraire le rôle le plus élevé |
| Timestamps sans timezone | Ajouter `AT TIME ZONE 'UTC'` lors de la migration |
| STI Doctrine vs JPA | Même pattern, syntaxe différente |
| Soft delete filter global | Hibernate `@Filter` + activation dans un request filter |

---

## 9. Métriques de la migration Futela

| Métrique | Valeur |
|----------|--------|
| Fichiers PHP (source) | 1 004 |
| Fichiers Java (cible) | 570+ |
| Tests Java | 305 |
| Migrations Flyway | 9 (7 schema + 1 data + 1 fixtures) |
| Modules métier | 10 |
| Use cases migrés | ~153 |
| Endpoints API | ~80 |
| Tables BDD | 31 |
| Lignes de données migrées | 431+ |
| Score checklist | 560/595 (94%) |
| Temps de compilation | 2.6s |
| Temps des tests | 7.2s |
