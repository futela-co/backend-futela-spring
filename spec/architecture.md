# FUTELA - Architecture Backend (Deep Dive)

> Architecture détaillée du backend Spring Boot avec diagrammes ASCII.
> Inspirée de l'architecture d'astuces-backend, adaptée au multi-tenant immobilier.

---

## 1. Architecture Hexagonale - Vue d'Ensemble

L'architecture hexagonale (ports & adapters) isole le domaine métier de toute dépendance technique.
La règle fondamentale : **les dépendances pointent toujours vers l'intérieur** (vers le domaine).

```
                         ┌──────────────────────────────────┐
                         │        PRESENTATION              │
                         │   (REST Controllers, Filters)    │
                         │                                  │
                         │  ┌────────────────────────────┐  │
                         │  │       APPLICATION           │  │
                         │  │  (Use Cases, DTOs, Mappers) │  │
                         │  │                             │  │
                         │  │  ┌──────────────────────┐   │  │
                         │  │  │      DOMAIN           │  │  │
                         │  │  │                       │  │  │
                         │  │  │  Models    Enums      │  │  │
                         │  │  │  Ports     Events     │  │  │
                         │  │  │  Exceptions           │  │  │
                         │  │  │                       │  │  │
                         │  │  └──────────────────────┘   │  │
                         │  │                             │  │
                         │  └────────────────────────────┘  │
                         │                                  │
                         │        INFRASTRUCTURE            │
                         │  (JPA, Redis, FlexPay, Cloudinary)│
                         └──────────────────────────────────┘
```

### Diagramme Ports & Adapters

```
┌─────────────────────┐                              ┌─────────────────────┐
│  Adaptateur Entrant │                              │  Adaptateur Sortant │
│                     │                              │                     │
│  ┌───────────────┐  │    ┌───────────────────┐     │  ┌───────────────┐  │
│  │ REST          │──┼───►│  Port Entrant     │     │  │ JPA           │  │
│  │ Controller    │  │    │  (Use Case IF)    │     │  │ Repository    │  │
│  └───────────────┘  │    └───────┬───────────┘     │  │ Adapter       │  │
│                     │            │                  │  └───────┬───────┘  │
│  ┌───────────────┐  │    ┌───────▼───────────┐     │          │          │
│  │ Event         │──┼───►│  Application       │     │  ┌───────▼───────┐  │
│  │ Listener      │  │    │  Service           │     │  │ PostgreSQL    │  │
│  └───────────────┘  │    │  (Use Case Impl)   │     │  └───────────────┘  │
│                     │    └───────┬───────────┘     │                     │
│                     │            │                  │  ┌───────────────┐  │
│                     │    ┌───────▼───────────┐     │  │ FlexPay       │  │
│                     │    │  Domain            │     │  │ Gateway       │  │
│                     │    │  Model / Logic     │◄────┼──│ Adapter       │  │
│                     │    └───────┬───────────┘     │  └───────────────┘  │
│                     │            │                  │                     │
│                     │    ┌───────▼───────────┐     │  ┌───────────────┐  │
│                     │    │  Port Sortant     │────►│  │ Cloudinary    │  │
│                     │    │  (Repository IF)  │     │  │ Adapter       │  │
│                     │    └───────────────────┘     │  └───────────────┘  │
│                     │                              │                     │
│                     │                              │  ┌───────────────┐  │
│                     │                              │  │ Redis Cache   │  │
│                     │                              │  │ Adapter       │  │
│                     │                              │  └───────────────┘  │
│                     │                              │                     │
│                     │                              │  ┌───────────────┐  │
│                     │                              │  │ WebSocket/SSE │  │
│                     │                              │  │ Adapter       │  │
│                     │                              │  └───────────────┘  │
└─────────────────────┘                              └─────────────────────┘
```

---

## 2. Arborescence des Packages (Détaillée)

```
com.futela.api
│
├── domain/                              # Coeur métier - AUCUNE dépendance externe
│   ├── model/
│   │   ├── common/
│   │   │   ├── PageResult.java          # record(content, page, size, totalElements)
│   │   │   └── Pageable.java            # record(page, size, sortBy, sortDirection)
│   │   ├── core/
│   │   │   ├── Company.java             # record - Tenant root
│   │   │   └── PlatformSetting.java     # record
│   │   ├── auth/
│   │   │   ├── User.java               # record
│   │   │   ├── DeviceSession.java      # record
│   │   │   └── RefreshToken.java       # record
│   │   ├── address/
│   │   │   ├── Country.java            # record
│   │   │   ├── Province.java           # record
│   │   │   ├── City.java              # record
│   │   │   ├── Town.java             # record
│   │   │   ├── District.java          # record
│   │   │   └── Address.java           # record
│   │   ├── property/
│   │   │   ├── Property.java           # record (base)
│   │   │   ├── Apartment.java          # record extends conceptuel
│   │   │   ├── House.java             # record
│   │   │   ├── Land.java             # record
│   │   │   ├── Car.java              # record
│   │   │   ├── EventHall.java         # record
│   │   │   ├── Category.java          # record
│   │   │   ├── Photo.java            # record
│   │   │   └── Listing.java           # record (Favoris)
│   │   ├── reservation/
│   │   │   ├── Reservation.java        # record
│   │   │   └── Visit.java            # record
│   │   ├── rent/
│   │   │   ├── Lease.java             # record
│   │   │   ├── RentInvoice.java        # record
│   │   │   ├── RentPayment.java        # record
│   │   │   ├── PaymentSchedule.java    # record
│   │   │   └── RentReminder.java       # record
│   │   ├── payment/
│   │   │   ├── Transaction.java        # record
│   │   │   ├── Currency.java          # record
│   │   │   └── PaymentMethod.java      # record
│   │   ├── messaging/
│   │   │   ├── Conversation.java       # record
│   │   │   ├── Message.java           # record
│   │   │   ├── Notification.java       # record
│   │   │   └── Contact.java           # record
│   │   └── review/
│   │       └── Review.java            # record
│   │
│   ├── enums/
│   │   ├── UserRole.java              # SUPER_ADMIN, ADMIN, OWNER, TENANT, USER
│   │   ├── UserStatus.java            # ACTIVE, INACTIVE, SUSPENDED
│   │   ├── PropertyType.java          # APARTMENT, HOUSE, LAND, CAR, EVENT_HALL
│   │   ├── PropertyStatus.java        # DRAFT, PUBLISHED, RENTED, UNAVAILABLE
│   │   ├── ListingType.java           # RENT, SALE, SHORT_TERM
│   │   ├── FuelType.java             # DIESEL, GASOLINE, ELECTRIC, HYBRID
│   │   ├── Transmission.java          # MANUAL, AUTOMATIC
│   │   ├── LandType.java             # RESIDENTIAL, COMMERCIAL, AGRICULTURAL
│   │   ├── ReservationStatus.java     # PENDING, CONFIRMED, CANCELLED, COMPLETED
│   │   ├── VisitStatus.java           # SCHEDULED, CONFIRMED, CANCELLED, COMPLETED
│   │   ├── LeaseStatus.java           # ACTIVE, TERMINATED, RENEWED, EXPIRED
│   │   ├── PaymentStatus.java         # PENDING, PAID, OVERDUE, PARTIAL
│   │   ├── ReminderType.java          # BEFORE_DUE, ON_DUE, AFTER_DUE
│   │   ├── TransactionType.java       # PAYMENT, REFUND
│   │   ├── TransactionStatus.java     # PENDING, COMPLETED, FAILED, CANCELLED
│   │   ├── MessageType.java           # TEXT, IMAGE, FILE
│   │   ├── MessageStatus.java         # SENT, DELIVERED, READ
│   │   ├── ContactStatus.java         # NEW, RESPONDED, ARCHIVED
│   │   ├── NotificationType.java      # RESERVATION, PAYMENT, MESSAGE, SYSTEM
│   │   ├── NotificationStatus.java    # UNREAD, READ, ARCHIVED
│   │   ├── NotificationChannel.java   # EMAIL, SMS, PUSH, IN_APP
│   │   └── Rating.java               # ONE, TWO, THREE, FOUR, FIVE
│   │
│   ├── port/
│   │   ├── in/                        # --- Ports entrants (use case interfaces) ---
│   │   │   ├── auth/                  # LoginUseCase, RegisterUseCase, etc.
│   │   │   ├── address/               # CreateCountryUseCase, etc.
│   │   │   ├── property/              # CreatePropertyUseCase, etc.
│   │   │   ├── reservation/           # CreateReservationUseCase, etc.
│   │   │   ├── rent/                  # CreateLeaseUseCase, etc.
│   │   │   ├── payment/               # InitiatePaymentUseCase, etc.
│   │   │   ├── messaging/             # SendMessageUseCase, etc.
│   │   │   └── review/               # CreateReviewUseCase, etc.
│   │   │
│   │   └── out/                       # --- Ports sortants ---
│   │       ├── auth/                  # UserRepositoryPort, etc.
│   │       ├── address/               # CountryRepositoryPort, etc.
│   │       ├── property/              # PropertyRepositoryPort, etc.
│   │       ├── reservation/           # ReservationRepositoryPort, etc.
│   │       ├── rent/                  # LeaseRepositoryPort, etc.
│   │       ├── payment/               # TransactionRepositoryPort, etc.
│   │       ├── messaging/             # ConversationRepositoryPort, etc.
│   │       ├── review/               # ReviewRepositoryPort, etc.
│   │       └── common/               # Service ports (EmailPort, FileStoragePort, etc.)
│   │
│   ├── event/
│   │   ├── PropertyPublishedEvent.java
│   │   ├── ReservationCreatedEvent.java
│   │   ├── LeaseCreatedEvent.java
│   │   ├── PaymentCompletedEvent.java
│   │   ├── MessageSentEvent.java
│   │   ├── ReviewCreatedEvent.java
│   │   └── ... (30+ événements)
│   │
│   └── exception/
│       ├── DomainException.java           # Base abstraite avec code
│       ├── ResourceNotFoundException.java
│       ├── DuplicateResourceException.java
│       ├── ValidationException.java
│       ├── TenantMismatchException.java
│       ├── InvalidOperationException.java
│       └── UnauthorizedException.java
│
├── application/
│   ├── usecase/
│   │   ├── auth/                      # LoginService, RegisterService, etc.
│   │   ├── address/                   # CreateCountryService, etc.
│   │   ├── property/                  # CreatePropertyService, etc.
│   │   ├── reservation/               # CreateReservationService, etc.
│   │   ├── rent/                      # CreateLeaseService, etc.
│   │   ├── payment/                   # InitiatePaymentService, etc.
│   │   ├── messaging/                 # SendMessageService, etc.
│   │   └── review/                   # CreateReviewService, etc.
│   │
│   ├── dto/
│   │   ├── request/
│   │   │   ├── auth/                  # LoginRequest, RegisterRequest, etc.
│   │   │   ├── address/               # CreateCountryRequest, etc.
│   │   │   ├── property/              # CreatePropertyRequest, etc.
│   │   │   ├── reservation/           # CreateReservationRequest, etc.
│   │   │   ├── rent/                  # CreateLeaseRequest, etc.
│   │   │   ├── payment/               # InitiatePaymentRequest, etc.
│   │   │   ├── messaging/             # SendMessageRequest, etc.
│   │   │   └── review/               # CreateReviewRequest, etc.
│   │   └── response/
│   │       ├── common/                # ApiResponse, PagedResponse
│   │       ├── auth/                  # AuthResponse, UserResponse, etc.
│   │       ├── address/               # CountryResponse, etc.
│   │       ├── property/              # PropertyResponse, etc.
│   │       ├── reservation/           # ReservationResponse, etc.
│   │       ├── rent/                  # LeaseResponse, etc.
│   │       ├── payment/               # TransactionResponse, etc.
│   │       ├── messaging/             # ConversationResponse, etc.
│   │       └── review/               # ReviewResponse, etc.
│   │
│   ├── mapper/
│   │   ├── auth/                      # UserMapper (MapStruct)
│   │   ├── address/                   # AddressMapper
│   │   ├── property/                  # PropertyMapper
│   │   ├── reservation/               # ReservationMapper
│   │   ├── rent/                      # LeaseMapper
│   │   ├── payment/                   # TransactionMapper
│   │   ├── messaging/                 # MessageMapper
│   │   └── review/                   # ReviewMapper
│   │
│   └── service/
│       ├── SecurityService.java       # getCurrentUserId(), getCurrentCompanyId()
│       ├── SlugGeneratorService.java   # Génération de slugs uniques
│       └── TenantContextService.java  # Gestion du contexte tenant
│
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/
│   │   │   ├── common/
│   │   │   │   └── BaseEntity.java    # id, createdAt, updatedAt, deletedAt (mapped superclass)
│   │   │   ├── core/                  # CompanyEntity, PlatformSettingEntity
│   │   │   ├── auth/                  # UserEntity, DeviceSessionEntity, RefreshTokenEntity
│   │   │   ├── address/               # CountryEntity, ProvinceEntity, etc.
│   │   │   ├── property/              # PropertyEntity (STI), CategoryEntity, PhotoEntity, etc.
│   │   │   ├── reservation/           # ReservationEntity, VisitEntity
│   │   │   ├── rent/                  # LeaseEntity, RentInvoiceEntity, etc.
│   │   │   ├── payment/               # TransactionEntity, CurrencyEntity, etc.
│   │   │   ├── messaging/             # ConversationEntity, MessageEntity, etc.
│   │   │   └── review/               # ReviewEntity
│   │   │
│   │   ├── repository/               # JpaXxxRepository extends JpaRepository
│   │   │   ├── auth/
│   │   │   ├── address/
│   │   │   ├── property/
│   │   │   ├── reservation/
│   │   │   ├── rent/
│   │   │   ├── payment/
│   │   │   ├── messaging/
│   │   │   └── review/
│   │   │
│   │   ├── adapter/                   # XxxRepositoryAdapter implements XxxRepositoryPort
│   │   │   ├── auth/
│   │   │   ├── address/
│   │   │   ├── property/
│   │   │   ├── reservation/
│   │   │   ├── rent/
│   │   │   ├── payment/
│   │   │   ├── messaging/
│   │   │   └── review/
│   │   │
│   │   ├── mapper/                    # XxxPersistenceMapper (static toDomain/toEntity)
│   │   │   ├── auth/
│   │   │   ├── address/
│   │   │   ├── property/
│   │   │   ├── reservation/
│   │   │   ├── rent/
│   │   │   ├── payment/
│   │   │   ├── messaging/
│   │   │   └── review/
│   │   │
│   │   ├── specification/             # JPA Specifications pour filtrage dynamique
│   │   │   ├── PropertySpecification.java
│   │   │   ├── LeaseSpecification.java
│   │   │   ├── TransactionSpecification.java
│   │   │   └── ReservationSpecification.java
│   │   │
│   │   └── filter/                    # Hibernate Filters
│   │       ├── TenantFilter.java      # Filtrage automatique par company_id
│   │       └── SoftDeleteFilter.java  # Filtrage automatique deleted_at IS NULL
│   │
│   ├── security/
│   │   ├── SecurityConfiguration.java
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtProperties.java         # @ConfigurationProperties (record)
│   │   ├── CustomUserDetailsService.java
│   │   ├── CustomPermissionEvaluator.java
│   │   ├── RolePermissions.java
│   │   └── CorsProperties.java
│   │
│   ├── config/
│   │   ├── WebConfiguration.java
│   │   ├── AppConfiguration.java
│   │   ├── CacheConfiguration.java
│   │   ├── CloudinaryConfiguration.java
│   │   ├── OpenApiConfiguration.java
│   │   ├── TenantFilterConfiguration.java
│   │   └── AsyncConfiguration.java
│   │
│   ├── integration/
│   │   ├── flexpay/
│   │   │   ├── FlexPayConfig.java
│   │   │   ├── FlexPayGatewayAdapter.java    # Implémente PaymentGatewayPort
│   │   │   └── FlexPayWebhookHandler.java
│   │   ├── cloudinary/
│   │   │   ├── CloudinaryConfig.java
│   │   │   └── CloudinaryStorageAdapter.java  # Implémente FileStoragePort
│   │   ├── email/
│   │   │   ├── EmailConfig.java
│   │   │   └── EmailServiceAdapter.java       # Implémente EmailServicePort
│   │   └── sms/
│   │       └── SmsServiceAdapter.java         # Implémente SmsServicePort
│   │
│   ├── event/
│   │   ├── PropertyEventListener.java
│   │   ├── ReservationEventListener.java
│   │   ├── RentEventListener.java
│   │   ├── PaymentEventListener.java
│   │   ├── MessagingEventListener.java
│   │   └── ReviewEventListener.java
│   │
│   └── scheduler/
│       ├── RentInvoiceScheduler.java          # Génération mensuelle factures
│       ├── RentReminderScheduler.java         # Rappels paiements
│       ├── OverduePaymentScheduler.java       # Détection retards
│       ├── TokenCleanupScheduler.java         # Nettoyage tokens expirés
│       └── FlexPaySyncScheduler.java          # Synchronisation FlexPay
│
└── presentation/
    ├── controller/
    │   ├── auth/                      # AuthController (login, register, refresh, logout)
    │   ├── address/                   # AddressController (CRUD géographique)
    │   ├── property/                  # PropertyController (CRUD propriétés)
    │   ├── reservation/               # ReservationController
    │   ├── rent/                      # RentController, LeaseController
    │   ├── payment/                   # PaymentController, CurrencyController
    │   ├── messaging/                 # MessageController, NotificationController
    │   ├── review/                    # ReviewController
    │   └── webhook/                   # FlexPayWebhookController
    │
    ├── advice/
    │   └── GlobalExceptionHandler.java
    │
    └── filter/
        ├── TenantContextFilter.java   # Extraction tenant du JWT
        └── RequestLoggingFilter.java
```

---

## 3. Couche Domain - Principes

### 3.1 Règles strictes

- **AUCUNE dépendance framework** : Pas d'annotations Spring, JPA, Jackson, Lombok
- **Objets immutables** : Records Java purs
- **Logique métier encapsulée** : Règles de validation dans le domaine
- **Ports = interfaces** : Contrats entre le domaine et l'extérieur

### 3.2 Domain Model (Record)

```java
public record Property(
    UUID id,
    String title,
    String description,
    PropertyType type,
    PropertyStatus status,
    ListingType listingType,
    BigDecimal price,
    String slug,
    UUID ownerId,
    UUID companyId,
    UUID addressId,
    Map<String, Object> attributes,  // JSON attributes par type
    List<Photo> photos,
    Instant createdAt,
    Instant updatedAt,
    Instant deletedAt
) {}
```

### 3.3 Port Entrant (Use Case Interface)

```java
public interface CreatePropertyUseCase {
    Property execute(CreatePropertyCommand command);
}
```

### 3.4 Port Sortant (Repository Interface)

```java
public interface PropertyRepositoryPort {
    Property save(Property property);
    Optional<Property> findById(UUID id);
    Optional<Property> findBySlug(String slug);
    PageResult<Property> findAll(PropertyFilter filter, Pageable pageable);
    void softDelete(UUID id);
    boolean existsBySlug(String slug);
}
```

---

## 4. Couche Infrastructure - Patterns

### 4.1 BaseEntity (Mapped Superclass)

```java
@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column
    private Instant deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
```

### 4.2 Tenant-Aware Entity

```java
@MappedSuperclass
@Getter @Setter
public abstract class TenantAwareEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;
}
```

### 4.3 Persistence Mapper (Static)

```java
public final class PropertyPersistenceMapper {
    private PropertyPersistenceMapper() {}

    public static Property toDomain(PropertyEntity entity) {
        return new Property(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getType(),
            // ...
        );
    }

    public static PropertyEntity toEntity(Property domain) {
        PropertyEntity entity = new PropertyEntity();
        entity.setTitle(domain.title());
        // ...
        return entity;
    }
}
```

### 4.4 Repository Adapter

```java
@Component
@RequiredArgsConstructor
public class PropertyRepositoryAdapter implements PropertyRepositoryPort {
    private final JpaPropertyRepository jpaRepository;

    @Override
    public Property save(Property property) {
        PropertyEntity entity = PropertyPersistenceMapper.toEntity(property);
        PropertyEntity saved = jpaRepository.save(entity);
        return PropertyPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Property> findById(UUID id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id)
            .map(PropertyPersistenceMapper::toDomain);
    }
}
```

---

## 5. Multi-Tenancy - Implémentation

### 5.1 Hibernate Filter

```java
@FilterDef(name = "tenantFilter",
    parameters = @ParamDef(name = "companyId", type = UUID.class))
@Filter(name = "tenantFilter", condition = "company_id = :companyId")
```

### 5.2 Activation automatique

```java
@Component
@RequiredArgsConstructor
public class TenantContextFilter extends OncePerRequestFilter {
    private final EntityManager entityManager;
    private final SecurityService securityService;

    @Override
    protected void doFilterInternal(...) {
        UUID companyId = securityService.getCurrentCompanyId();
        if (companyId != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter")
                .setParameter("companyId", companyId);
            session.enableFilter("softDeleteFilter");
        }
        filterChain.doFilter(request, response);
    }
}
```

---

## 6. Réponse API Standard

```java
public record ApiResponse<T>(
    boolean success,
    T data,
    String message,
    ErrorDetail error,
    Instant timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null, Instant.now());
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null, Instant.now());
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, null,
            new ErrorDetail(code, message, null), Instant.now());
    }

    public record ErrorDetail(
        String code,
        String message,
        List<FieldError> details
    ) {}

    public record FieldError(
        String field,
        String message
    ) {}
}
```
