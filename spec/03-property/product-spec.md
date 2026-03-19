# Phase 3 - Property (STI → @Inheritance)

> Gestion des propriétés immobilières avec 5 types : Apartment, House, Land, Car, EventHall.
> Migration du Single Table Inheritance Doctrine vers `@Inheritance(strategy = SINGLE_TABLE)` JPA.

---

## 1. Vision

Le module Property est le coeur du catalogue Futela. Il gère 5 types de biens via **Single Table Inheritance** :
- **Apartment** : Appartements avec étage, ascenseur, chambres
- **House** : Maisons avec jardin, garage, surface terrain
- **Land** : Terrains (résidentiel, commercial, agricole)
- **Car** : Véhicules avec marque, modèle, année
- **EventHall** : Salles d'événements avec capacité

Chaque propriété peut avoir des **photos** (avec ordre et photo principale) et être mise en **favoris** (Listing).

---

## 2. Modèles du Domaine

### 2.1 Property (Base - STI)

| Champ         | Type           | Contrainte       | Description                    |
|---------------|----------------|------------------|--------------------------------|
| id            | UUID           | PK               | Identifiant unique             |
| title         | String         | Not Null         | Titre de l'annonce             |
| description   | String         | Nullable         | Description détaillée          |
| type          | PropertyType   | Not Null         | Discriminateur STI             |
| status        | PropertyStatus | Not Null         | Statut de publication          |
| listingType   | ListingType    | Not Null         | Location, vente, court terme   |
| price         | BigDecimal     | Not Null         | Prix                           |
| slug          | String         | Unique, Not Null | URL-friendly                   |
| ownerId       | UUID           | FK → User        | Propriétaire                   |
| categoryId    | UUID           | FK → Category    | Catégorie                      |
| addressId     | UUID           | FK → Address     | Adresse du bien                |
| companyId     | UUID           | FK → Company     | Tenant                         |
| attributes    | JSON           | Nullable         | Attributs spécifiques au type  |
| createdAt     | Instant        | Not Null         | Date de création               |
| updatedAt     | Instant        | Not Null         | Dernière modification          |
| deletedAt     | Instant        | Nullable         | Soft delete                    |

### 2.2 Colonnes spécifiques par type (dans la même table)

#### Apartment & House (colonnes partagées)

| Champ         | Type    | Description              |
|---------------|---------|--------------------------|
| bedrooms      | Integer | Nombre de chambres       |
| bathrooms     | Integer | Nombre de salles de bain |
| squareMeters  | Integer | Surface en m²            |
| furnished     | Boolean | Meublé                   |

#### Car

| Champ         | Type         | Description              |
|---------------|--------------|--------------------------|
| brand         | String       | Marque                   |
| model         | String       | Modèle                   |
| year          | Integer      | Année                    |
| mileage       | Integer      | Kilométrage              |
| fuelType      | FuelType     | Type de carburant        |
| transmission  | Transmission | Boîte de vitesse         |

#### EventHall

| Champ         | Type    | Description              |
|---------------|---------|--------------------------|
| capacity      | Integer | Capacité max personnes   |

#### Land

| Champ         | Type     | Description              |
|---------------|----------|--------------------------|
| landType      | LandType | Type de terrain          |
| surfaceArea   | Integer  | Surface en m²            |

### 2.3 Attributs JSON (détails spécifiques non-filtrés)

| Type      | Attributs JSON                                           |
|-----------|----------------------------------------------------------|
| Apartment | floor, hasElevator, hasBalcony, hasParking               |
| House     | hasGarden, hasGarage, gardenSize, floors                 |
| Land      | hasWater, hasElectricity, isFenced                       |
| Car       | color, seats, engineSize, hasAirConditioning             |
| EventHall | hasKitchen, hasParking, parkingSpots, hasSoundSystem     |

### 2.4 Photo

| Champ         | Type     | Contrainte       | Description                    |
|---------------|----------|------------------|--------------------------------|
| id            | UUID     | PK               | Identifiant unique             |
| propertyId    | UUID     | FK → Property    | Propriété associée             |
| url           | String   | Not Null         | URL Cloudinary                 |
| caption       | String   | Nullable         | Légende                        |
| position      | Integer  | Not Null         | Ordre d'affichage              |
| isPrimary     | Boolean  | Not Null         | Photo principale               |
| createdAt     | Instant  | Not Null         | Date de création               |

### 2.5 Listing (Favoris)

| Champ         | Type     | Contrainte       | Description                    |
|---------------|----------|------------------|--------------------------------|
| id            | UUID     | PK               | Identifiant unique             |
| userId        | UUID     | FK → User        | Utilisateur                    |
| propertyId    | UUID     | FK → Property    | Propriété en favori            |
| companyId     | UUID     | FK → Company     | Tenant                         |
| createdAt     | Instant  | Not Null         | Date d'ajout                   |

---

## 3. Enums

```java
public enum PropertyType {
    APARTMENT, HOUSE, LAND, CAR, EVENT_HALL
}

public enum PropertyStatus {
    DRAFT, PUBLISHED, RENTED, UNAVAILABLE
}

public enum ListingType {
    RENT, SALE, SHORT_TERM
}

public enum FuelType {
    DIESEL, GASOLINE, ELECTRIC, HYBRID
}

public enum Transmission {
    MANUAL, AUTOMATIC
}

public enum LandType {
    RESIDENTIAL, COMMERCIAL, AGRICULTURAL, INDUSTRIAL
}
```

---

## 4. Use Cases

### Property (7 use cases)

| Use Case                    | Description                                    |
|-----------------------------|------------------------------------------------|
| CreatePropertyUseCase       | Créer une propriété (tout type)                |
| UpdatePropertyUseCase       | Modifier une propriété                         |
| GetPropertyByIdUseCase      | Détail d'une propriété                         |
| GetPropertiesByOwnerUseCase | Propriétés d'un propriétaire                   |
| SearchPropertiesUseCase     | Recherche avec filtres (type, prix, localisation) |
| PublishPropertyUseCase      | Publier une propriété (DRAFT → PUBLISHED)      |
| UnpublishPropertyUseCase    | Dépublier (PUBLISHED → DRAFT)                  |

### Photo (4 use cases)

| Use Case                    | Description                                    |
|-----------------------------|------------------------------------------------|
| UploadPropertyPhotoUseCase  | Uploader une photo (Cloudinary)                |
| DeletePropertyPhotoUseCase  | Supprimer une photo                            |
| SetPrimaryPhotoUseCase      | Définir la photo principale                    |
| ReorderPhotosUseCase        | Réordonner les photos                          |

### Listing (3 use cases)

| Use Case                    | Description                                    |
|-----------------------------|------------------------------------------------|
| AddToFavoritesUseCase       | Ajouter en favoris                             |
| RemoveFromFavoritesUseCase  | Retirer des favoris                            |
| GetUserFavoritesUseCase     | Lister les favoris d'un utilisateur            |

---

## 5. Endpoints API

### 5.1 Property

| Méthode | Path                                    | Description                         | Auth |
|---------|-----------------------------------------|-------------------------------------|------|
| GET     | /api/v1/properties                      | Recherche propriétés publiées       | Non  |
| GET     | /api/v1/properties/{slug}               | Détail d'une propriété              | Non  |
| GET     | /api/v1/properties/my                   | Mes propriétés (owner)              | Oui  |
| POST    | /api/v1/properties                      | Créer une propriété                 | Oui  |
| PUT     | /api/v1/properties/{id}                 | Modifier une propriété              | Oui  |
| PATCH   | /api/v1/properties/{id}/publish         | Publier                             | Oui  |
| PATCH   | /api/v1/properties/{id}/unpublish       | Dépublier                           | Oui  |

### 5.2 Photo

| Méthode | Path                                          | Description                     | Auth |
|---------|-----------------------------------------------|---------------------------------|------|
| POST    | /api/v1/properties/{id}/photos                | Uploader une photo              | Oui  |
| DELETE  | /api/v1/properties/{id}/photos/{photoId}      | Supprimer une photo             | Oui  |
| PATCH   | /api/v1/properties/{id}/photos/{photoId}/primary | Photo principale             | Oui  |
| PUT     | /api/v1/properties/{id}/photos/reorder        | Réordonner                      | Oui  |

### 5.3 Favoris

| Méthode | Path                                          | Description                     | Auth |
|---------|-----------------------------------------------|---------------------------------|------|
| GET     | /api/v1/favorites                             | Mes favoris                     | Oui  |
| POST    | /api/v1/favorites/{propertyId}                | Ajouter en favori               | Oui  |
| DELETE  | /api/v1/favorites/{propertyId}                | Retirer des favoris             | Oui  |

### Query Parameters (Recherche)

| Paramètre   | Type           | Description                         |
|--------------|----------------|-------------------------------------|
| type         | PropertyType   | Filtrer par type                    |
| listingType  | ListingType    | Location, vente, court terme        |
| minPrice     | BigDecimal     | Prix minimum                        |
| maxPrice     | BigDecimal     | Prix maximum                        |
| cityId       | UUID           | Filtrer par ville                   |
| districtId   | UUID           | Filtrer par quartier                |
| bedrooms     | Integer        | Nombre min de chambres              |
| furnished    | Boolean        | Meublé uniquement                   |
| sort         | String         | price_asc, price_desc, newest       |
| page         | Integer        | Page (défaut: 0)                    |
| size         | Integer        | Taille (défaut: 20, max: 50)        |

---

## 6. Notes techniques

### STI avec JPA

```java
@Entity
@Table(name = "properties")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class PropertyEntity extends TenantAwareEntity { ... }

@Entity
@DiscriminatorValue("APARTMENT")
public class ApartmentEntity extends PropertyEntity { ... }
```

### JSON Attributes

Les attributs JSON sont stockés dans une colonne `jsonb` PostgreSQL via `@JdbcTypeCode(SqlTypes.JSON)`.
Ils contiennent les détails spécifiques au type qui ne sont pas filtrés fréquemment.

### Cloudinary Upload

- Photos uploadées via multipart
- Cloudinary retourne l'URL publique
- Dossier : `futela/properties/{propertyId}/`
- Max : 10 photos par propriété, 5 Mo par photo
