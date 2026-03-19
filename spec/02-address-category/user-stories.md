# Phase 2 - User Stories : Address + Category

> Convention : `{MODULE}-{##}` | Priorité : P0 (critique) → P3 (bonus)

---

## Address - Hiérarchie géographique

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| ADDR-01  | P0       | En tant qu'utilisateur, je veux voir la liste des pays                                        | - Retourne tous les pays - Trié par nom alphabétique                                                                    |
| ADDR-02  | P0       | En tant qu'utilisateur, je veux voir les provinces d'un pays                                  | - Filtre par countryId - Trié par nom                                                                                   |
| ADDR-03  | P0       | En tant qu'utilisateur, je veux voir les villes d'une province                                | - Filtre par provinceId - Trié par nom                                                                                  |
| ADDR-04  | P0       | En tant qu'utilisateur, je veux voir les communes d'une ville                                 | - Filtre par cityId - Trié par nom                                                                                      |
| ADDR-05  | P0       | En tant qu'utilisateur, je veux voir les quartiers d'une commune                              | - Filtre par townId - Trié par nom                                                                                      |
| ADDR-06  | P1       | En tant qu'utilisateur, je veux voir les quartiers d'une ville                                | - Filtre par cityId (raccourci) - Agrège tous les quartiers de toutes les communes                                      |
| ADDR-07  | P0       | En tant qu'admin, je veux créer un pays                                                       | - Validation nom et code uniques - Code ISO obligatoire                                                                 |
| ADDR-08  | P0       | En tant qu'admin, je veux créer une province dans un pays                                     | - Validation countryId existe - Nom obligatoire                                                                         |
| ADDR-09  | P0       | En tant qu'admin, je veux créer une ville dans une province                                   | - Validation provinceId existe - Nom obligatoire                                                                        |
| ADDR-10  | P0       | En tant qu'admin, je veux créer une commune dans une ville                                    | - Validation cityId existe - Nom obligatoire                                                                            |
| ADDR-11  | P0       | En tant qu'admin, je veux créer un quartier dans une commune                                  | - Validation townId existe - cityId auto-déduit depuis town - Nom obligatoire                                           |
| ADDR-12  | P1       | En tant qu'admin, je veux modifier les données géographiques                                  | - Update nom pour country, province, city, town, district                                                               |
| ADDR-13  | P1       | En tant qu'admin, je veux supprimer un quartier                                               | - Hard delete (pas de soft delete pour les données géo) - Vérification qu'aucune adresse ne référence ce quartier       |
| ADDR-14  | P0       | En tant qu'utilisateur, je veux créer une adresse complète                                    | - Validation : au minimum cityId requis - GPS coordinates optionnels - Cascade automatique des FK géographiques          |
| ADDR-15  | P1       | En tant qu'utilisateur, je veux rechercher une adresse                                        | - Recherche textuelle sur street, district name, town name, city name - Résultats paginés                               |
| ADDR-16  | P2       | En tant que système, je veux un seed de données géographiques RDC                             | - Commande CLI pour importer les 26 provinces, villes principales, communes de Kinshasa - Idempotent (re-exécutable)    |

---

## Category

| ID       | Priorité | Story                                                                                         | Critères d'acceptation                                                                                                   |
|----------|----------|-----------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| CAT-01   | P0       | En tant qu'admin, je veux créer une catégorie de propriété                                    | - Validation nom non vide - Génération slug automatique - Tenant-aware (companyId auto-injecté)                          |
| CAT-02   | P0       | En tant qu'utilisateur, je veux voir les catégories disponibles                               | - Filtre par company du tenant courant - Triées par nom                                                                 |
| CAT-03   | P1       | En tant qu'admin, je veux modifier une catégorie                                              | - Update nom, description, icon - Régénération slug si nom changé                                                       |
| CAT-04   | P2       | En tant qu'admin, je veux supprimer une catégorie                                             | - Soft delete - Vérification qu'aucune propriété n'utilise cette catégorie                                              |
