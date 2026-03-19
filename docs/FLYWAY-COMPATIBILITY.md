# Flyway - Compatibilité Symfony ↔ Spring Boot

> Ce document décrit comment les migrations Flyway du projet Spring Boot correspondent au schéma Doctrine du projet Symfony.

---

## Migrations Flyway

| Version | Fichier | Équivalent Symfony | Tables créées |
|---------|---------|-------------------|---------------|
| V001 | `create_core_auth_schema.sql` | Versions 1-3 | companies, platform_settings, users, device_sessions, refresh_tokens |
| V002 | `create_address_category_schema.sql` | Versions 4 | countries, provinces, cities, towns, districts, addresses, categories + seed RDC |
| V003 | `create_property_schema.sql` | Versions 5-6 | properties (STI), photos, listings |
| V004 | `create_reservation_review_schema.sql` | Versions 7 | reservations, visits, reviews |
| V005 | `create_rent_schema.sql` | Versions 8 | leases, rent_invoices, rent_payments, payment_schedules, rent_reminders |
| V006 | `create_payment_schema.sql` | Versions 9 | transactions, currencies, payment_methods + seed currencies/methods |
| V007 | `create_messaging_schema.sql` | Versions 10 | conversations, conversation_participants, messages, notifications, contacts |
| V008 | `migrate_data_from_symfony.sql` | — | Migration des données de `futela_db` → `futela_spring` |
| V009 | `seed_fixtures.sql` | DataFixtures | Données de test (users, properties, leases, etc.) |

---

## Correspondance des tables

### Noms de tables

| Symfony (Doctrine) | Spring Boot (Flyway) | Notes |
|--------------------|---------------------|-------|
| `company` | `companies` | Pluralisé |
| `platform_setting` | `platform_settings` | Pluralisé |
| `users` | `users` | Identique |
| `auth_device_session` | `device_sessions` | Préfixe `auth_` supprimé |
| `auth_refresh_token` | `refresh_tokens` | Préfixe `auth_` supprimé |
| `address_country` | `countries` | Préfixe `address_` supprimé |
| `address_province` | `provinces` | Préfixe `address_` supprimé |
| `address_city` | `cities` | Préfixe `address_` supprimé |
| `address_town` | `towns` | Préfixe `address_` supprimé |
| `address_district` | `districts` | Préfixe `address_` supprimé |
| `address` | `addresses` | Pluralisé |
| `property_category` | `categories` | Préfixe `property_` supprimé |
| `property` | `properties` | Pluralisé |
| `property_photo` | `photos` | Préfixe `property_` supprimé |
| `property_listing` | `listings` | Préfixe `property_` supprimé |
| `reservation` | `reservations` | Pluralisé |
| `visit` | `visits` | Pluralisé |
| `review` | `reviews` | Pluralisé |
| `lease` | `leases` | Pluralisé |
| `rent_invoice` | `rent_invoices` | Pluralisé |
| `rent_payment` | `rent_payments` | Pluralisé |
| `payment_schedule` | `payment_schedules` | Pluralisé |
| `rent_reminder` | `rent_reminders` | Pluralisé |
| `transaction` | `transactions` | Pluralisé |
| `currency` | `currencies` | Pluralisé |
| `payment_method` | `payment_methods` | Pluralisé |
| `conversation` | `conversations` | Pluralisé |
| `conversation_user` | `conversation_participants` | Renommé |
| `message` | `messages` | Pluralisé |
| `notification` | `notifications` | Pluralisé |
| `contact` | `contacts` | Pluralisé |

---

## Types de colonnes

### Timestamps

| Symfony | Spring Boot | Script de migration |
|---------|-------------|---------------------|
| `timestamp(0) without time zone` | `TIMESTAMP WITH TIME ZONE` | `source.created_at AT TIME ZONE 'UTC'` |

### Types monétaires

| Symfony | Spring Boot | Notes |
|---------|-------------|-------|
| `NUMERIC(10,2)` | `DECIMAL(12,2)` | Précision augmentée |
| `double precision` | `DECIMAL(12,2)` | Plus de double pour l'argent |
| `NUMERIC(15,2)` | `DECIMAL(15,2)` | Identique |

### Enums

| Symfony | Spring Boot | Notes |
|---------|-------------|-------|
| `VARCHAR` (valeurs libres) | `VARCHAR` + enum Java | Validation côté Java |
| Valeurs en minuscules (`pending`) | Valeurs en MAJUSCULES (`PENDING`) | Convention Java |

### JSON

| Symfony | Spring Boot | Notes |
|---------|-------------|-------|
| `json` | `jsonb` | PostgreSQL JSONB pour performance |
| `@JdbcTypeCode(SqlTypes.JSON)` | idem | Même annotation JPA |

---

## Colonnes renommées par table

### users
| Symfony | Spring Boot | Type changement |
|---------|-------------|-----------------|
| `password` | `password_hash` | Renommé |
| `roles` (JSON) | `role` (VARCHAR) | Simplifié |
| — | `status` | Ajouté (défaut: ACTIVE) |

### reservations
| Symfony | Spring Boot |
|---------|-------------|
| `guest_id` | `user_id` |
| `number_of_guests` | `guest_count` |
| `special_requests` | `notes` |
| `cancellation_reason` | `cancel_reason` |

### visits
| Symfony | Spring Boot |
|---------|-------------|
| `visitor_id` | `user_id` |

### reviews
| Symfony | Spring Boot |
|---------|-------------|
| `status` (string) | `is_approved` (boolean) |

### leases
| Symfony | Spring Boot |
|---------|-------------|
| `deposit` | `deposit_amount` |

### rent_invoices
| Symfony | Spring Boot |
|---------|-------------|
| `month` + `year` (integers) | `period_start` + `period_end` (DATE) |
| — | `paid_amount` (ajouté, défaut 0) |

### transactions
| Symfony | Spring Boot |
|---------|-------------|
| `external_id` | `external_ref` |
| `failed_reason` | `failure_reason` |
| `gateway` | `provider` |
| — | `reference` (ajouté, UNIQUE) |
| — | `phone_number` (ajouté) |

### notifications
| Symfony | Spring Boot |
|---------|-------------|
| `content` | `body` |

### contacts
| Symfony | Spring Boot |
|---------|-------------|
| `name` | `first_name` + `last_name` |
| `assigned_to_id` | `responded_by` |

---

## Contraintes et Index

### Convention de nommage

| Type | Symfony (Doctrine auto) | Spring Boot (Flyway) |
|------|------------------------|---------------------|
| Primary Key | `table_pkey` | `table_pkey` |
| Unique | `uniq_HASH_column` | `uk_table_column` |
| Foreign Key | `fk_HASH` | `fk_table_column` |
| Index | `idx_HASH_column` | `idx_table_column` |

### Index ajoutés dans Spring Boot

- `idx_properties_status` — filtrage par statut
- `idx_properties_listing_type` — filtrage par type
- `idx_properties_slug` — recherche par slug
- `idx_properties_price` — filtrage par prix
- `uk_review_user_property` — unicité 1 review/user/property
- `uk_listing_user_property` — unicité favoris

---

## Données de seed

### Incluses dans les migrations

| Migration | Données |
|-----------|---------|
| V001 | Company par défaut "Futela" |
| V002 | RDC: 1 pays, 26 provinces, villes, 24 communes Kinshasa, quartiers |
| V006 | Devises (USD, CDF), méthodes paiement (MPESA, AIRTEL, ORANGE, CASH) |
| V009 | Fixtures dev: users, properties, leases, reservations, reviews, etc. |

### Commande de seed Symfony → Flyway

| Symfony Command | Flyway Migration |
|----------------|-----------------|
| `app:seed-rdc-geographic-data` | V002 (inclus dans la migration) |
| DataFixtures (AppFixtures) | V009 (seed_fixtures.sql) |
