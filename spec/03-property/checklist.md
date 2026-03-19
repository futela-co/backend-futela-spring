# Phase 3 - Checklist : Property

> Total : 85 points.
> **Dernière vérification : 2026-03-19**

---

## 1. Domain Models (15 points) — 15/15

- [x] Record `Property` avec tous les champs de base (id, title, type, status, price, slug, etc.)
- [x] Records spécifiques conceptuels ou stratégie de composition pour Apartment, House, Land, Car, EventHall
- [x] Record `Photo` avec propertyId, url, position, isPrimary
- [x] Record `Listing` avec userId, propertyId
- [x] 6 enums : PropertyType, PropertyStatus, ListingType, FuelType, Transmission, LandType
- [x] Aucune dépendance framework dans les records

---

## 2. Single Table Inheritance (15 points) — 15/15

### JPA Configuration

- [x] `PropertyEntity` abstraite avec `@Inheritance(strategy = SINGLE_TABLE)`
- [x] `@DiscriminatorColumn(name = "type")` sur PropertyEntity
- [x] `ApartmentEntity` avec `@DiscriminatorValue("APARTMENT")`
- [x] `HouseEntity` avec `@DiscriminatorValue("HOUSE")`
- [x] `LandEntity` avec `@DiscriminatorValue("LAND")`
- [x] `CarEntity` avec `@DiscriminatorValue("CAR")`
- [x] `EventHallEntity` avec `@DiscriminatorValue("EVENT_HALL")`

### Colonnes et JSON

- [x] Colonnes partagées (bedrooms, bathrooms, squareMeters) sur PropertyEntity
- [x] Colonnes spécifiques Car (brand, model, year, mileage, fuelType, transmission)
- [x] Colonne `attributes` en `jsonb` PostgreSQL avec `@JdbcTypeCode(SqlTypes.JSON)`
- [x] Méthodes d'accès typées pour les attributs JSON

---

## 3. Ports & Use Cases (15 points) — 15/15

- [x] `PropertyRepositoryPort` : save, findById, findBySlug, findByOwnerId, search, softDelete
- [x] `PhotoRepositoryPort` : save, findByPropertyId, delete, countByPropertyId
- [x] `ListingRepositoryPort` : save, delete, findByUserId, existsByUserIdAndPropertyId
- [x] 7 use cases Property implémentés
- [x] 4 use cases Photo implémentés
- [x] 3 use cases Listing implémentés
- [x] `SearchPropertiesUseCase` avec JPA Specification pour filtrage dynamique

---

## 4. Infrastructure (10 points) — 10/10

- [x] `PropertyEntity` extends `TenantAwareEntity`
- [x] `PhotoEntity` extends `BaseEntity` (pas tenant-aware, lié via property)
- [x] `ListingEntity` extends `TenantAwareEntity`
- [x] `JpaPropertyRepository` avec méthodes custom (findBySlug, findByOwnerId)
- [x] `PropertySpecification` pour filtrage dynamique (type, prix, ville, chambres, etc.)
- [x] `PropertyPersistenceMapper` gère le mapping STI (dispatch par type)
- [x] Migration Flyway `V003__create_property_schema.sql`

---

## 5. Cloudinary Integration (10 points) — 10/10

- [x] `FileStoragePort` interface dans domain/port/out/common/
- [x] `CloudinaryStorageAdapter` implémente FileStoragePort
- [x] Upload multipart via REST endpoint
- [x] Suppression fichier Cloudinary lors de deletePhoto
- [x] Validation : max 10 photos par propriété, max 5 Mo par photo
- [x] Dossier Cloudinary : `futela/properties/{propertyId}/`

---

## 6. Controllers & DTOs (10 points) — 10/10

- [x] `CreatePropertyRequest` record avec validation Jakarta (`@NotBlank title, @NotNull type, @Positive price`)
- [x] `PropertyResponse` record complet (inclut photos, adresse, attributs décodés)
- [x] `PropertySummaryResponse` pour les listes (sans détails)
- [x] `PhotoResponse` record
- [x] `PropertyController` avec endpoints publics et authentifiés
- [x] `FavoriteController` avec endpoints authentifiés
- [x] Query parameters pour la recherche (type, minPrice, maxPrice, cityId, etc.)

---

## 7. Tests (10 points) — 1/10

- [ ] Tests CreatePropertyService (par type)
- [ ] Tests SearchPropertiesUseCase (filtres)
- [ ] Tests PublishPropertyUseCase (validation)
- [ ] Tests UploadPropertyPhotoUseCase
- [ ] Tests AddToFavoritesUseCase (unicité)
- [x] `mvn clean compile` → BUILD SUCCESS

---

## Résumé

| Section              | Points | Score    | Status  |
|----------------------|--------|----------|---------|
| 1. Domain Models     | 15     | 15/15    | Done    |
| 2. STI               | 15     | 15/15    | Done    |
| 3. Ports & Use Cases | 15     | 15/15    | Done    |
| 4. Infrastructure    | 10     | 10/10    | Done    |
| 5. Cloudinary        | 10     | 10/10    | Done    |
| 6. Controllers & DTOs| 10     | 10/10    | Done    |
| 7. Tests             | 10     |  1/10    | Partial |
| **TOTAL**            | **85** | **76/85**| —       |
