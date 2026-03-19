# Phase 2 - Address + Category

> Hiérarchie géographique de la RDC et catégorisation des propriétés.

---

## 1. Vision

Deux modules fondamentaux pour le catalogue immobilier :
- **Address** : Hiérarchie géographique complète (Country → Province → City → Town → District → Address)
- **Category** : Catégorisation des propriétés par type d'usage

---

## 2. Modèles du Domaine

### 2.1 Country

| Champ     | Type    | Contrainte       | Description              |
|-----------|---------|------------------|--------------------------|
| id        | UUID    | PK               | Identifiant unique       |
| name      | String  | Unique, Not Null | Nom du pays              |
| code      | String  | Unique, Not Null | Code ISO (CD, CG, etc.) |
| createdAt | Instant | Not Null         | Date de création         |
| updatedAt | Instant | Not Null         | Dernière modification    |

### 2.2 Province

| Champ     | Type    | Contrainte       | Description              |
|-----------|---------|------------------|--------------------------|
| id        | UUID    | PK               | Identifiant unique       |
| name      | String  | Not Null         | Nom de la province       |
| countryId | UUID    | FK → Country     | Pays parent              |
| createdAt | Instant | Not Null         | Date de création         |
| updatedAt | Instant | Not Null         | Dernière modification    |

### 2.3 City

| Champ      | Type    | Contrainte       | Description              |
|------------|---------|------------------|--------------------------|
| id         | UUID    | PK               | Identifiant unique       |
| name       | String  | Not Null         | Nom de la ville          |
| provinceId | UUID    | FK → Province    | Province parente         |
| createdAt  | Instant | Not Null         | Date de création         |
| updatedAt  | Instant | Not Null         | Dernière modification    |

### 2.4 Town

| Champ     | Type    | Contrainte       | Description              |
|-----------|---------|------------------|--------------------------|
| id        | UUID    | PK               | Identifiant unique       |
| name      | String  | Not Null         | Nom de la commune        |
| cityId    | UUID    | FK → City        | Ville parente            |
| createdAt | Instant | Not Null         | Date de création         |
| updatedAt | Instant | Not Null         | Dernière modification    |

### 2.5 District

| Champ     | Type    | Contrainte       | Description              |
|-----------|---------|------------------|--------------------------|
| id        | UUID    | PK               | Identifiant unique       |
| name      | String  | Not Null         | Nom du quartier          |
| townId    | UUID    | FK → Town        | Commune parente          |
| cityId    | UUID    | FK → City        | Ville (raccourci)        |
| createdAt | Instant | Not Null         | Date de création         |
| updatedAt | Instant | Not Null         | Dernière modification    |

### 2.6 Address

| Champ      | Type    | Contrainte       | Description              |
|------------|---------|------------------|--------------------------|
| id         | UUID    | PK               | Identifiant unique       |
| street     | String  | Nullable         | Rue                      |
| number     | String  | Nullable         | Numéro                   |
| latitude   | Double  | Nullable         | Coordonnée GPS           |
| longitude  | Double  | Nullable         | Coordonnée GPS           |
| districtId | UUID    | FK → District    | Quartier                 |
| townId     | UUID    | FK → Town        | Commune                  |
| cityId     | UUID    | FK → City        | Ville                    |
| provinceId | UUID    | FK → Province    | Province                 |
| countryId  | UUID    | FK → Country     | Pays                     |
| createdAt  | Instant | Not Null         | Date de création         |
| updatedAt  | Instant | Not Null         | Dernière modification    |

### 2.7 Category

| Champ       | Type    | Contrainte       | Description              |
|-------------|---------|------------------|--------------------------|
| id          | UUID    | PK               | Identifiant unique       |
| name        | String  | Not Null         | Nom de la catégorie      |
| slug        | String  | Unique, Not Null | URL-friendly             |
| description | String  | Nullable         | Description              |
| icon        | String  | Nullable         | Nom de l'icône           |
| companyId   | UUID    | FK → Company     | Tenant                   |
| createdAt   | Instant | Not Null         | Date de création         |
| updatedAt   | Instant | Not Null         | Dernière modification    |
| deletedAt   | Instant | Nullable         | Soft delete              |

---

## 3. Use Cases

### Address (20 use cases)

| Use Case                       | Description                                |
|--------------------------------|--------------------------------------------|
| CreateCountryUseCase           | Créer un pays                              |
| GetCountriesUseCase            | Lister tous les pays                       |
| GetCountryByIdUseCase          | Détail d'un pays                           |
| UpdateCountryUseCase           | Modifier un pays                           |
| CreateProvinceUseCase          | Créer une province                         |
| GetProvincesByCountryUseCase   | Provinces d'un pays                        |
| GetProvinceByIdUseCase         | Détail d'une province                      |
| UpdateProvinceUseCase          | Modifier une province                      |
| CreateCityUseCase              | Créer une ville                            |
| GetCitiesByProvinceUseCase     | Villes d'une province                      |
| GetCityByIdUseCase             | Détail d'une ville                         |
| UpdateCityUseCase              | Modifier une ville                         |
| CreateTownUseCase              | Créer une commune                          |
| GetTownsByCityUseCase          | Communes d'une ville                       |
| GetTownByIdUseCase             | Détail d'une commune                       |
| UpdateTownUseCase              | Modifier une commune                       |
| CreateDistrictUseCase          | Créer un quartier                          |
| GetDistrictsByTownUseCase      | Quartiers d'une commune                    |
| DeleteDistrictUseCase          | Supprimer un quartier                      |
| CreateAddressUseCase           | Créer une adresse complète                 |
| UpdateAddressUseCase           | Modifier une adresse                       |
| SearchAddressesUseCase         | Rechercher des adresses                    |

### Category (4 use cases)

| Use Case                       | Description                                |
|--------------------------------|--------------------------------------------|
| CreateCategoryUseCase          | Créer une catégorie                        |
| GetCategoriesUseCase           | Lister les catégories                      |
| GetCategoryByIdUseCase         | Détail d'une catégorie                     |
| UpdateCategoryUseCase          | Modifier une catégorie                     |

---

## 4. Endpoints API

### 4.1 Address

| Méthode | Path                                          | Description                     | Auth |
|---------|-----------------------------------------------|---------------------------------|------|
| GET     | /api/v1/countries                             | Lister les pays                 | Non  |
| GET     | /api/v1/countries/{id}                        | Détail d'un pays                | Non  |
| POST    | /api/v1/admin/countries                       | Créer un pays                   | Oui  |
| PUT     | /api/v1/admin/countries/{id}                  | Modifier un pays                | Oui  |
| GET     | /api/v1/countries/{id}/provinces              | Provinces d'un pays             | Non  |
| POST    | /api/v1/admin/provinces                       | Créer une province              | Oui  |
| PUT     | /api/v1/admin/provinces/{id}                  | Modifier une province           | Oui  |
| GET     | /api/v1/provinces/{id}/cities                 | Villes d'une province           | Non  |
| POST    | /api/v1/admin/cities                          | Créer une ville                 | Oui  |
| PUT     | /api/v1/admin/cities/{id}                     | Modifier une ville              | Oui  |
| GET     | /api/v1/cities/{id}/towns                     | Communes d'une ville            | Non  |
| POST    | /api/v1/admin/towns                           | Créer une commune               | Oui  |
| PUT     | /api/v1/admin/towns/{id}                      | Modifier une commune            | Oui  |
| GET     | /api/v1/towns/{id}/districts                  | Quartiers d'une commune         | Non  |
| GET     | /api/v1/cities/{id}/districts                 | Quartiers d'une ville           | Non  |
| POST    | /api/v1/admin/districts                       | Créer un quartier               | Oui  |
| DELETE  | /api/v1/admin/districts/{id}                  | Supprimer un quartier           | Oui  |
| POST    | /api/v1/addresses                             | Créer une adresse               | Oui  |
| PUT     | /api/v1/addresses/{id}                        | Modifier une adresse            | Oui  |
| GET     | /api/v1/addresses/search?q=                   | Rechercher des adresses         | Non  |

### 4.2 Category

| Méthode | Path                                          | Description                     | Auth |
|---------|-----------------------------------------------|---------------------------------|------|
| GET     | /api/v1/categories                            | Lister les catégories           | Non  |
| GET     | /api/v1/categories/{id}                       | Détail d'une catégorie          | Non  |
| POST    | /api/v1/admin/categories                      | Créer une catégorie             | Oui  |
| PUT     | /api/v1/admin/categories/{id}                 | Modifier une catégorie          | Oui  |

---

## 5. Seed Data

### Données géographiques RDC

La migration doit inclure les données de base :
- 1 pays : République Démocratique du Congo (CD)
- 26 provinces
- Villes principales (Kinshasa, Lubumbashi, Goma, Bukavu, etc.)
- Communes de Kinshasa (24 communes)
- Quartiers principaux

→ Commande : `SeedRDCGeographicDataCommand` (équivalent Symfony migré)

---

## 6. Notes techniques

- Les entités Address ne sont **pas tenant-aware** (données partagées entre agences)
- Category **est tenant-aware** (chaque agence a ses propres catégories)
- Hiérarchie cascade : `Country → Province → City → Town → District → Address`
- GPS coordinates (latitude/longitude) sont optionnels mais utiles pour le mobile
