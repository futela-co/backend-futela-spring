# Phase 2 - Checklist : Address + Category

> Total : 70 points.
> **Dernière vérification : 2026-03-19**

---

## 1. Domain Models (15 points) — 15/15

- [x] Records Java pour Country, Province, City, Town, District, Address (6 records)
- [x] Record Java pour Category
- [x] Aucune dépendance framework dans les records
- [x] Relations hiérarchiques correctes (Country→Province→City→Town→District)
- [x] Address contient les FK vers tous les niveaux géographiques

---

## 2. Ports & Use Cases (15 points) — 15/15

### Ports sortants

- [x] `CountryRepositoryPort` : findAll, findById, save, existsByCode
- [x] `ProvinceRepositoryPort` : findByCountryId, findById, save
- [x] `CityRepositoryPort` : findByProvinceId, findById, save
- [x] `TownRepositoryPort` : findByCityId, findById, save
- [x] `DistrictRepositoryPort` : findByTownId, findByCityId, findById, save, deleteById
- [x] `AddressRepositoryPort` : findById, save, search
- [x] `CategoryRepositoryPort` : findAll, findById, save, softDelete

### Use Cases implémentés

- [x] 20 use cases Address (CRUD pour chaque niveau + search)
- [x] 4 use cases Category (CRUD)
- [x] Chaque use case = 1 interface + 1 service

---

## 3. Infrastructure Persistence (15 points) — 15/15

### Entités JPA

- [x] `CountryEntity`, `ProvinceEntity`, `CityEntity`, `TownEntity`, `DistrictEntity` extends BaseEntity
- [x] `AddressEntity` extends BaseEntity (pas tenant-aware)
- [x] `CategoryEntity` extends TenantAwareEntity (tenant-aware)
- [x] Relations JPA `@ManyToOne` / `@OneToMany` correctes
- [x] Cascade et orphanRemoval configurés

### Repositories & Adapters

- [x] 7 JpaRepository (un par entité)
- [x] 7 RepositoryAdapter implémentant les ports
- [x] 7 PersistenceMapper (static toDomain/toEntity)

### Flyway

- [x] Migration `V002__create_address_category_schema.sql`
- [x] Tables : countries, provinces, cities, towns, districts, addresses, categories
- [x] FK et index appropriés
- [x] Données de seed RDC (26 provinces, villes principales)

---

## 4. Controllers & DTOs (15 points) — 15/15

### DTOs

- [x] Request/Response records pour chaque niveau géographique
- [x] `CreateAddressRequest` avec validation Jakarta
- [x] `CategoryRequest` / `CategoryResponse` records
- [x] SearchAddressResponse avec résultats paginés

### Controllers

- [x] `AddressController` avec endpoints publics (GET) et admin (POST/PUT/DELETE)
- [x] `CategoryController` avec endpoints publics et admin
- [x] Pagination sur les listes
- [x] Réponses wrappées dans ApiResponse

### Validation

- [x] `@NotBlank` sur les noms obligatoires
- [x] Validation FK existe (countryId, provinceId, etc.)
- [x] Erreur 404 si entité parente non trouvée

---

## 5. Tests (10 points) — 1/10

- [ ] Tests unitaires des use cases Address (min 5 tests)
- [ ] Tests unitaires des use cases Category (min 3 tests)
- [ ] Test de la hiérarchie géographique (cascade correcte)
- [ ] Test de la recherche d'adresses
- [x] `mvn clean compile` → BUILD SUCCESS

---

## Résumé

| Section              | Points | Score   | Status |
|----------------------|--------|---------|--------|
| 1. Domain Models     | 15     | 15/15   | Done   |
| 2. Ports & Use Cases | 15     | 15/15   | Done   |
| 3. Infrastructure    | 15     | 15/15   | Done   |
| 4. Controllers & DTOs| 15     | 15/15   | Done   |
| 5. Tests             | 10     |  1/10   | Partial|
| **TOTAL**            | **70** | **61/70**| —     |
