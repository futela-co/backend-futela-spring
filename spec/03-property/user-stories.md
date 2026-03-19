# Phase 3 - User Stories : Property

> Convention : `{MODULE}-{##}` | Priorité : P0 (critique) → P3 (bonus)

---

## Property - Gestion des biens

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| PROP-01  | P0       | En tant que propriétaire, je veux créer une propriété (appartement)                           | - Choix du type (APARTMENT) - Champs obligatoires : title, price, listingType - Colonnes spécifiques : bedrooms, bathrooms, squareMeters - Statut initial : DRAFT |
| PROP-02  | P0       | En tant que propriétaire, je veux créer une maison                                            | - Type HOUSE - Mêmes champs base + hasGarden, hasGarage en JSON attributes                                              |
| PROP-03  | P0       | En tant que propriétaire, je veux créer un terrain                                            | - Type LAND - Champs : landType, surfaceArea - Pas de bedrooms/bathrooms                                                |
| PROP-04  | P1       | En tant que propriétaire, je veux créer une voiture                                           | - Type CAR - Champs : brand, model, year, mileage, fuelType, transmission                                               |
| PROP-05  | P1       | En tant que propriétaire, je veux créer une salle d'événement                                 | - Type EVENT_HALL - Champs : capacity - JSON : hasKitchen, hasParking, hasSoundSystem                                    |
| PROP-06  | P0       | En tant que propriétaire, je veux modifier ma propriété                                       | - Mise à jour de tous les champs modifiables - Seul le propriétaire ou un admin peut modifier - Pas de changement de type |
| PROP-07  | P0       | En tant que propriétaire, je veux publier ma propriété                                        | - Transition DRAFT → PUBLISHED - Validation : au moins 1 photo, adresse renseignée, prix > 0 - Émission PropertyPublishedEvent |
| PROP-08  | P1       | En tant que propriétaire, je veux dépublier ma propriété                                      | - Transition PUBLISHED → DRAFT - Émission PropertyUnpublishedEvent                                                      |
| PROP-09  | P0       | En tant que propriétaire, je veux voir toutes mes propriétés                                  | - Filtre par ownerId (auto depuis JWT) - Tous les statuts (DRAFT inclus) - Paginé, trié par date                        |
| PROP-10  | P0       | En tant qu'utilisateur, je veux rechercher des propriétés publiées                            | - Uniquement statut PUBLISHED - Filtres : type, listingType, prix min/max, ville, quartier, chambres, meublé - Pagination |
| PROP-11  | P0       | En tant qu'utilisateur, je veux voir le détail d'une propriété                                | - Accès par slug - Inclut photos (ordonnées), adresse complète, infos propriétaire - Attributs JSON décodés              |

---

## Photo

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| PHOTO-01 | P0       | En tant que propriétaire, je veux uploader une photo de ma propriété                          | - Upload multipart vers Cloudinary - Enregistrement URL en base - Position auto-incrémentée - Max 10 photos par propriété |
| PHOTO-02 | P0       | En tant que propriétaire, je veux supprimer une photo                                         | - Suppression Cloudinary + BDD - Réindexation des positions                                                             |
| PHOTO-03 | P1       | En tant que propriétaire, je veux définir la photo principale                                 | - isPrimary = true sur la photo sélectionnée - isPrimary = false sur l'ancienne principale                              |
| PHOTO-04 | P2       | En tant que propriétaire, je veux réordonner mes photos                                       | - Réception d'une liste ordonnée d'IDs - Mise à jour des positions                                                      |

---

## Favoris (Listing)

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| FAV-01   | P1       | En tant qu'utilisateur, je veux ajouter une propriété en favori                               | - Création Listing (userId + propertyId) - Unicité : pas de doublon (même user + même property) - Erreur si déjà en favori |
| FAV-02   | P1       | En tant qu'utilisateur, je veux retirer une propriété de mes favoris                          | - Suppression du Listing - Hard delete (pas de soft delete pour les favoris)                                             |
| FAV-03   | P1       | En tant qu'utilisateur, je veux voir tous mes favoris                                         | - Liste paginée des propriétés en favori - Inclut les infos de base de chaque propriété                                 |
