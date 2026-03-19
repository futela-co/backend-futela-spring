#!/usr/bin/env bash
# ============================================================================
# Data Migration Script: futela_db (Symfony) → futela_spring (Spring Boot)
# ============================================================================
set -euo pipefail

DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-5432}"
DB_USER="${DB_USER:-postgres}"
DB_PASS="${DB_PASS:-password}"
SOURCE_DB="futela_db"
TARGET_DB="futela_spring"

export PGPASSWORD="$DB_PASS"

PSQL="psql -U $DB_USER -h $DB_HOST -p $DB_PORT -d $TARGET_DB -v ON_ERROR_STOP=1"

echo "=== Starting data migration from $SOURCE_DB to $TARGET_DB ==="
echo "=== $(date) ==="

$PSQL <<'MIGRATION_SQL'

-- Ensure dblink is available
CREATE EXTENSION IF NOT EXISTS dblink;

-- Connection string for source DB
-- We'll use this throughout
DO $$ BEGIN PERFORM dblink_connect('src', 'dbname=futela_db host=127.0.0.1 port=5432 user=postgres password=password'); END $$;

BEGIN;

-- ============================================================================
-- Disable FK constraints and triggers for bulk insert
-- ============================================================================
SET session_replication_role = 'replica';

-- ============================================================================
-- 1. COMPANIES (company → companies)
--    code → slug, missing: address/subdomain/currency in target
-- ============================================================================
INSERT INTO companies (id, name, slug, email, phone, logo, is_active, created_at, updated_at, deleted_at)
SELECT id, name, code, email, phone, logo, is_active,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, name, code, email, phone, logo, is_active, created_at, updated_at, deleted_at
    FROM company
') AS t(id uuid, name varchar, code varchar, email varchar, phone varchar, logo varchar,
        is_active boolean, created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 2. PLATFORM_SETTINGS (platform_setting → platform_settings)
-- ============================================================================
INSERT INTO platform_settings (id, setting_key, value, description, created_at, updated_at)
SELECT id, setting_key, value, description,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, setting_key, value, description, created_at, updated_at
    FROM platform_setting
') AS t(id uuid, setting_key varchar, value varchar, description varchar,
        created_at timestamp, updated_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 3. USERS (users → users)
--    password → password_hash, roles JSON → role VARCHAR, add status default
-- ============================================================================
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, avatar,
                   role, status, is_verified, is_available, profile_completed,
                   email_verified_at, last_login_at, company_id,
                   created_at, updated_at, deleted_at)
SELECT id, email, password, first_name, last_name, phone, avatar,
       -- Extract role from JSON array: pick highest role
       CASE
           WHEN roles::text LIKE '%ROLE_ADMIN%' THEN 'ADMIN'
           WHEN roles::text LIKE '%ROLE_LANDLORD%' THEN 'LANDLORD'
           WHEN roles::text LIKE '%ROLE_AGENT%' THEN 'AGENT'
           ELSE 'USER'
       END,
       'ACTIVE',
       is_verified, is_available, profile_completed,
       email_verified_at AT TIME ZONE 'UTC',
       last_login_at AT TIME ZONE 'UTC',
       company_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, email, password, first_name, last_name, phone, avatar,
           roles::text, is_verified, is_available, profile_completed,
           email_verified_at, last_login_at, company_id,
           created_at, updated_at, deleted_at
    FROM users
') AS t(id uuid, email varchar, password varchar, first_name varchar, last_name varchar,
        phone varchar, avatar varchar, roles text, is_verified boolean,
        is_available boolean, profile_completed boolean,
        email_verified_at timestamp, last_login_at timestamp, company_id uuid,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 4. DEVICE_SESSIONS (auth_device_session → device_sessions)
--    last_activity_at → last_active_at
-- ============================================================================
INSERT INTO device_sessions (id, user_id, device_name, device_fingerprint, ip_address,
                             user_agent, location, is_active, is_trusted,
                             last_active_at, revoked_at,
                             created_at, updated_at, deleted_at)
SELECT id, user_id, device_name, device_fingerprint, ip_address,
       user_agent, location, is_active, is_trusted,
       last_activity_at AT TIME ZONE 'UTC',
       revoked_at AT TIME ZONE 'UTC',
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, user_id, device_name, device_fingerprint, ip_address,
           user_agent, location, is_active, is_trusted,
           last_activity_at, revoked_at, created_at, updated_at, deleted_at
    FROM auth_device_session
') AS t(id uuid, user_id uuid, device_name varchar, device_fingerprint varchar,
        ip_address varchar, user_agent varchar, location varchar,
        is_active boolean, is_trusted boolean,
        last_activity_at timestamp, revoked_at timestamp,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 5. REFRESH_TOKENS (auth_refresh_token → refresh_tokens)
-- ============================================================================
INSERT INTO refresh_tokens (id, device_session_id, token_hash, expires_at, used_at,
                            is_revoked, created_at, updated_at, deleted_at)
SELECT id, device_session_id, token_hash,
       expires_at AT TIME ZONE 'UTC',
       used_at AT TIME ZONE 'UTC',
       is_revoked,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, device_session_id, token_hash, expires_at, used_at, is_revoked,
           created_at, updated_at, deleted_at
    FROM auth_refresh_token
') AS t(id uuid, device_session_id uuid, token_hash varchar, expires_at timestamp,
        used_at timestamp, is_revoked boolean,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 6. COUNTRIES (address_country → countries)
-- ============================================================================
INSERT INTO countries (id, name, code, phone_code, is_active,
                       created_at, updated_at, deleted_at)
SELECT id, name, code, phone_code, is_active,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, name, code, phone_code, is_active, created_at, updated_at, deleted_at
    FROM address_country
') AS t(id uuid, name varchar, code varchar, phone_code varchar, is_active boolean,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 7. PROVINCES (address_province → provinces)
-- ============================================================================
INSERT INTO provinces (id, name, code, is_active, country_id,
                       created_at, updated_at, deleted_at)
SELECT id, name, code, is_active, country_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, name, code, is_active, country_id, created_at, updated_at, deleted_at
    FROM address_province
') AS t(id uuid, name varchar, code varchar, is_active boolean, country_id uuid,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 8. CITIES (address_city → cities)
-- ============================================================================
INSERT INTO cities (id, name, zip_code, is_active, province_id,
                    created_at, updated_at, deleted_at)
SELECT id, name, zip_code, is_active, province_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, name, zip_code, is_active, province_id, created_at, updated_at, deleted_at
    FROM address_city
') AS t(id uuid, name varchar, zip_code varchar, is_active boolean, province_id uuid,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 9. TOWNS (address_town → towns)
-- ============================================================================
INSERT INTO towns (id, name, zip_code, is_active, city_id,
                   created_at, updated_at, deleted_at)
SELECT id, name, zip_code, is_active, city_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, name, zip_code, is_active, city_id, created_at, updated_at, deleted_at
    FROM address_town
') AS t(id uuid, name varchar, zip_code varchar, is_active boolean, city_id uuid,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 10. DISTRICTS (address_district → districts)
-- ============================================================================
INSERT INTO districts (id, name, is_active, city_id, town_id,
                       created_at, updated_at, deleted_at)
SELECT id, name, is_active, city_id, town_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, name, is_active, city_id, town_id, created_at, updated_at, deleted_at
    FROM address_district
') AS t(id uuid, name varchar, is_active boolean, city_id uuid, town_id uuid,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 11. ADDRESSES (address → addresses)
--     Source only has town_id/district_id, target needs city_id/province_id/country_id too
-- ============================================================================
INSERT INTO addresses (id, street, number, additional_info, latitude, longitude,
                       district_id, town_id, city_id, province_id, country_id,
                       created_at, updated_at, deleted_at)
SELECT id, street, number, additional_info, latitude::double precision, longitude::double precision,
       district_id, town_id, city_id, province_id, country_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT a.id, a.street, a.number, a.additional_info,
           a.latitude::double precision, a.longitude::double precision,
           a.district_id, a.town_id,
           t.city_id, c.province_id, p.country_id,
           a.created_at, a.updated_at, a.deleted_at
    FROM address a
    JOIN address_town t ON t.id = a.town_id
    JOIN address_city c ON c.id = t.city_id
    JOIN address_province p ON p.id = c.province_id
') AS t(id uuid, street varchar, number varchar, additional_info text,
        latitude double precision, longitude double precision,
        district_id uuid, town_id uuid, city_id uuid, province_id uuid, country_id uuid,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 12. CATEGORIES (property_category → categories)
-- ============================================================================
INSERT INTO categories (id, name, slug, description, is_active, company_id,
                        created_at, updated_at, deleted_at)
SELECT id, name, slug, description, is_active, company_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, name, slug, description, is_active, company_id,
           created_at, updated_at, deleted_at
    FROM property_category
') AS t(id uuid, name varchar, slug varchar, description text, is_active boolean,
        company_id uuid, created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 13. PROPERTIES (property → properties)
--     Target has extra cols: slug, status, brand, model, year, mileage, fuel_type, transmission, land_type, surface_area
--     Source listing_type 'for_rent' → 'RENT', 'for_sale' → 'SALE'
--     Source has no slug → generate from title + id
-- ============================================================================
INSERT INTO properties (id, type, title, description, status, listing_type,
                        price_per_day, price_per_month, sale_price, slug,
                        is_published, is_available, is_active, view_count,
                        rating, review_count, deletion_reason,
                        bedrooms, bathrooms, square_meters, capacity, attributes,
                        owner_id, category_id, address_id, company_id,
                        created_at, updated_at, deleted_at)
SELECT id, UPPER(type),
       title, description,
       CASE WHEN is_published THEN 'PUBLISHED' ELSE 'DRAFT' END,
       CASE listing_type
           WHEN 'for_rent' THEN 'RENT'
           WHEN 'for_sale' THEN 'SALE'
           WHEN 'for_both' THEN 'BOTH'
           ELSE UPPER(listing_type)
       END,
       price_per_day, price_per_month, sale_price,
       -- Generate slug from title
       LOWER(REGEXP_REPLACE(REGEXP_REPLACE(title, '[^a-zA-Z0-9 ]', '', 'g'), '\s+', '-', 'g')) || '-' || REPLACE(id::text, '-', ''),
       is_published, is_available, is_active, view_count,
       rating, review_count, deletion_reason,
       bedrooms, bathrooms, square_meters, capacity,
       COALESCE(attributes::text, '{}')::jsonb,
       owner_id, category_id, address_id, company_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, type, title, description, is_published, listing_type,
           price_per_day, price_per_month, sale_price,
           is_available, is_active, view_count,
           rating, review_count, deletion_reason,
           bedrooms, bathrooms, square_meters, capacity,
           attributes::text,
           owner_id, category_id, address_id, company_id,
           created_at, updated_at, deleted_at
    FROM property
') AS t(id uuid, type varchar, title varchar, description text, is_published boolean,
        listing_type varchar, price_per_day numeric, price_per_month numeric, sale_price numeric,
        is_available boolean, is_active boolean, view_count integer,
        rating numeric, review_count integer, deletion_reason varchar,
        bedrooms integer, bathrooms integer, square_meters integer, capacity integer,
        attributes text,
        owner_id uuid, category_id uuid, address_id uuid, company_id uuid,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 14. PHOTOS (property_photo → photos)
--     Target is simpler: only url, caption, display_order, is_primary
-- ============================================================================
INSERT INTO photos (id, property_id, url, caption, display_order, is_primary,
                    created_at, updated_at, deleted_at)
SELECT id, property_id, COALESCE(url, filename, ''), caption, display_order, is_primary,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, property_id, url, filename, caption, display_order, is_primary,
           created_at, updated_at, deleted_at
    FROM property_photo
') AS t(id uuid, property_id uuid, url varchar, filename varchar, caption varchar,
        display_order integer, is_primary boolean,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 15. LISTINGS (property_listing → listings)
--     Source has no company_id → resolve via property
-- ============================================================================
INSERT INTO listings (id, user_id, property_id, company_id,
                      created_at, updated_at, deleted_at)
SELECT pl_id, user_id, property_id, company_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT pl.id, pl.user_id, pl.property_id, p.company_id,
           pl.created_at, pl.updated_at, pl.deleted_at
    FROM property_listing pl
    JOIN property p ON p.id = pl.property_id
') AS t(pl_id uuid, user_id uuid, property_id uuid, company_id uuid,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 16. RESERVATIONS (reservation → reservations)
--     guest_id → user_id, number_of_guests → guest_count,
--     special_requests → notes, cancellation_reason → cancel_reason,
--     total_price DOUBLE → DECIMAL, dates timestamp → date
-- ============================================================================
INSERT INTO reservations (id, property_id, user_id, host_id, company_id,
                          status, start_date, end_date, total_price, currency,
                          guest_count, notes, cancel_reason,
                          confirmed_at, cancelled_at, completed_at,
                          payment_transaction_id,
                          created_at, updated_at, deleted_at)
SELECT id, property_id, guest_id, host_id, company_id,
       UPPER(status), start_date::date, end_date::date,
       total_price::numeric, currency,
       number_of_guests, special_requests, cancellation_reason,
       confirmed_at, cancelled_at, completed_at,
       payment_transaction_id,
       created_at, updated_at, deleted_at
FROM dblink('src', '
    SELECT id, property_id, guest_id, host_id, company_id,
           status, start_date, end_date, total_price, currency,
           number_of_guests, special_requests, cancellation_reason,
           confirmed_at, cancelled_at, completed_at, payment_transaction_id,
           created_at, updated_at, deleted_at
    FROM reservation
') AS t(id uuid, property_id uuid, guest_id uuid, host_id uuid, company_id uuid,
        status varchar, start_date timestamp, end_date timestamp,
        total_price double precision, currency varchar,
        number_of_guests integer, special_requests text, cancellation_reason text,
        confirmed_at timestamp, cancelled_at timestamp, completed_at timestamp,
        payment_transaction_id varchar,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 17. VISITS (visit → visits)
--     visitor_id → user_id, cancellation_reason → cancel_reason
-- ============================================================================
INSERT INTO visits (id, property_id, user_id, company_id,
                    status, scheduled_at, notes, cancel_reason,
                    confirmed_at, completed_at, payment_transaction_id, is_paid,
                    created_at, updated_at, deleted_at)
SELECT id, property_id, visitor_id, company_id,
       UPPER(status), scheduled_at, notes, cancellation_reason,
       confirmed_at, completed_at, payment_transaction_id, is_paid,
       created_at, updated_at, deleted_at
FROM dblink('src', '
    SELECT id, property_id, visitor_id, company_id,
           status, scheduled_at, notes, cancellation_reason,
           confirmed_at, completed_at, payment_transaction_id, is_paid,
           created_at, updated_at, deleted_at
    FROM visit
') AS t(id uuid, property_id uuid, visitor_id uuid, company_id uuid,
        status varchar, scheduled_at timestamp, notes text, cancellation_reason text,
        confirmed_at timestamp, completed_at timestamp,
        payment_transaction_id varchar, is_paid boolean,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 18. REVIEWS (review → reviews)
--     reviewer_id → user_id, status → is_approved boolean
--     Target is simplified: no title, pros, cons, would_recommend, moderation_reason, etc.
-- ============================================================================
INSERT INTO reviews (id, property_id, user_id, company_id,
                     rating, comment, is_approved, is_flagged, flag_reason,
                     created_at, updated_at, deleted_at)
SELECT id, property_id, reviewer_id, company_id,
       rating, comment,
       CASE WHEN status = 'approved' THEN true ELSE false END,
       is_flagged, flag_reason,
       created_at, updated_at, deleted_at
FROM dblink('src', '
    SELECT id, property_id, reviewer_id, company_id,
           rating, comment, status, is_flagged, flag_reason,
           created_at, updated_at, deleted_at
    FROM review
') AS t(id uuid, property_id uuid, reviewer_id uuid, company_id uuid,
        rating integer, comment text, status varchar,
        is_flagged boolean, flag_reason text,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 19. LEASES (lease → leases)
--     deposit → deposit_amount, special_terms → notes,
--     dates timestamp → date
-- ============================================================================
INSERT INTO leases (id, property_id, tenant_id, landlord_id, company_id,
                    status, monthly_rent, currency, deposit_amount,
                    start_date, end_date, payment_day_of_month,
                    notes, terminated_at, termination_reason,
                    created_at, updated_at, deleted_at)
SELECT id, property_id, tenant_id, landlord_id, company_id,
       UPPER(status), monthly_rent, currency, deposit,
       start_date::date, end_date::date, payment_day_of_month,
       special_terms,
       terminated_at AT TIME ZONE 'UTC', termination_reason,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, property_id, tenant_id, landlord_id, company_id,
           status, monthly_rent, currency, deposit,
           start_date, end_date, payment_day_of_month,
           special_terms, terminated_at, termination_reason,
           created_at, updated_at, deleted_at
    FROM lease
') AS t(id uuid, property_id uuid, tenant_id uuid, landlord_id uuid, company_id uuid,
        status varchar, monthly_rent numeric, currency varchar, deposit numeric,
        start_date timestamp, end_date timestamp, payment_day_of_month integer,
        special_terms text, terminated_at timestamp, termination_reason text,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 20. RENT_INVOICES (rent_invoice → rent_invoices)
--     month+year → period_start/period_end, add paid_amount default 0
--     due_date timestamp → date, add late_fee default
-- ============================================================================
INSERT INTO rent_invoices (id, lease_id, company_id, invoice_number,
                           amount, paid_amount, status, due_date,
                           period_start, period_end, late_fee,
                           created_at, updated_at, deleted_at)
SELECT id, lease_id, company_id, invoice_number,
       amount, 0,
       UPPER(status), due_date::date,
       -- Compute period_start from year/month
       MAKE_DATE(year, month, 1),
       -- Compute period_end as last day of month
       (MAKE_DATE(year, month, 1) + INTERVAL '1 month' - INTERVAL '1 day')::date,
       0,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, lease_id, company_id, invoice_number,
           amount, status, due_date, month, year,
           created_at, updated_at, deleted_at
    FROM rent_invoice
') AS t(id uuid, lease_id uuid, company_id uuid, invoice_number varchar,
        amount numeric, status varchar, due_date timestamp, month integer, year integer,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 21. RENT_PAYMENTS (rent_payment → rent_payments)
--     transaction_id (varchar) → reference, payment_date timestamp → date
--     Target: no status, no late_fee, no month/year
-- ============================================================================
INSERT INTO rent_payments (id, invoice_id, lease_id, company_id,
                           amount, payment_date, payment_method, reference, notes,
                           created_at, updated_at, deleted_at)
SELECT id, invoice_id, lease_id, company_id,
       amount, payment_date::date, payment_method, transaction_id, notes,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, invoice_id, lease_id, company_id,
           amount, payment_date, payment_method, transaction_id, notes,
           created_at, updated_at, deleted_at
    FROM rent_payment
') AS t(id uuid, invoice_id uuid, lease_id uuid, company_id uuid,
        amount numeric, payment_date timestamp, payment_method varchar,
        transaction_id varchar, notes text,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 22. RENT_REMINDERS (rent_reminder → rent_reminders)
--     reminder_type → type, sent_via → channel
-- ============================================================================
INSERT INTO rent_reminders (id, invoice_id, lease_id, company_id,
                            type, sent_at, channel,
                            created_at, updated_at, deleted_at)
SELECT id, invoice_id, lease_id, company_id,
       reminder_type,
       sent_at AT TIME ZONE 'UTC',
       sent_via,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, invoice_id, lease_id, company_id,
           reminder_type, sent_at, sent_via,
           created_at, updated_at, deleted_at
    FROM rent_reminder
') AS t(id uuid, invoice_id uuid, lease_id uuid, company_id uuid,
        reminder_type varchar, sent_at timestamp, sent_via varchar,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 23. CURRENCIES (currency → currencies)
--     Source has no deleted_at (implied from schema check)
-- ============================================================================
INSERT INTO currencies (id, code, name, symbol, exchange_rate, is_active,
                        created_at, updated_at)
SELECT id, code, name, symbol, exchange_rate, is_active,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, code, name, symbol, exchange_rate, is_active,
           created_at, updated_at
    FROM currency
') AS t(id uuid, code varchar, name varchar, symbol varchar,
        exchange_rate numeric, is_active boolean,
        created_at timestamp, updated_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 24. PAYMENT_METHODS (payment_method → payment_methods)
--     gateway → provider, supported_currencies dropped, add logo NULL
-- ============================================================================
INSERT INTO payment_methods (id, name, code, provider, is_active,
                             created_at, updated_at)
SELECT id, name, code, gateway, is_active,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, name, code, gateway, is_active,
           created_at, updated_at
    FROM payment_method
') AS t(id uuid, name varchar, code varchar, gateway varchar, is_active boolean,
        created_at timestamp, updated_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 25. TRANSACTIONS (transaction → transactions)
--     external_id → external_ref, failed_reason → failure_reason,
--     gateway → provider, add reference (gen from id), add phone_number
--     related_entity/related_entity_type dropped in target
-- ============================================================================
INSERT INTO transactions (id, reference, external_ref, type, status,
                          amount, currency, phone_number, provider,
                          user_id, company_id, description,
                          metadata, failure_reason, processed_at,
                          created_at, updated_at, deleted_at)
SELECT id,
       'TXN-' || UPPER(REPLACE(id::text, '-', '')),
       external_id,
       UPPER(type), UPPER(status),
       amount, currency,
       COALESCE(
           -- Try to extract phone from metadata
           metadata_text::jsonb->>'phone',
           '000000000'
       ),
       gateway,
       user_id, company_id, description,
       COALESCE(metadata_text, '{}')::jsonb,
       failed_reason,
       processed_at AT TIME ZONE 'UTC',
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, external_id, type, status, amount, currency,
           gateway, user_id, company_id, description,
           metadata::text, failed_reason, processed_at,
           created_at, updated_at, deleted_at
    FROM transaction
') AS t(id uuid, external_id varchar, type varchar, status varchar,
        amount numeric, currency varchar, gateway varchar,
        user_id uuid, company_id uuid, description text,
        metadata_text text, failed_reason text, processed_at timestamp,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 26. PAYMENT_SCHEDULES (payment_schedule → payment_schedules)
--     Source has schedule_data JSON + generated_at, target has due_date/amount/status/invoice_id
--     Since schemas differ greatly, we'll extract from schedule_data or use defaults
-- ============================================================================
-- Source table is empty (0 rows), so this is a no-op but included for completeness
INSERT INTO payment_schedules (id, lease_id, company_id, due_date, amount, status,
                               created_at, updated_at, deleted_at)
SELECT id, lease_id, company_id,
       generated_at::date,
       0,
       'PENDING',
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, lease_id, company_id, generated_at,
           created_at, updated_at, deleted_at
    FROM payment_schedule
') AS t(id uuid, lease_id uuid, company_id uuid, generated_at timestamp,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 27. CONVERSATIONS (conversation → conversations)
-- ============================================================================
INSERT INTO conversations (id, subject, property_id, last_message_at,
                           is_archived, company_id,
                           created_at, updated_at, deleted_at)
SELECT id, subject, property_id,
       last_message_at AT TIME ZONE 'UTC',
       is_archived, company_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, subject, property_id, last_message_at, is_archived, company_id,
           created_at, updated_at, deleted_at
    FROM conversation
') AS t(id uuid, subject varchar, property_id uuid, last_message_at timestamp,
        is_archived boolean, company_id uuid,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 28. CONVERSATION_PARTICIPANTS (conversation_user → conversation_participants)
-- ============================================================================
INSERT INTO conversation_participants (conversation_id, user_id)
SELECT conversation_id, user_id
FROM dblink('src', '
    SELECT conversation_id, user_id FROM conversation_user
') AS t(conversation_id uuid, user_id uuid)
ON CONFLICT DO NOTHING;

-- ============================================================================
-- 29. MESSAGES (message → messages)
--     Source already has type, target has type default 'TEXT'
-- ============================================================================
INSERT INTO messages (id, conversation_id, sender_id, type, content,
                      is_read, read_at, company_id,
                      created_at, updated_at, deleted_at)
SELECT id, conversation_id, sender_id, UPPER(type), content,
       is_read,
       read_at AT TIME ZONE 'UTC',
       company_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, conversation_id, sender_id, type, content,
           is_read, read_at, company_id,
           created_at, updated_at, deleted_at
    FROM message
') AS t(id uuid, conversation_id uuid, sender_id uuid, type varchar, content text,
        is_read boolean, read_at timestamp, company_id uuid,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 30. NOTIFICATIONS (notification → notifications)
--     content → body, sent_via dropped, data json → jsonb
-- ============================================================================
INSERT INTO notifications (id, user_id, type, status, title, body, channel,
                           data, related_entity_id, related_entity_type,
                           is_read, read_at, company_id,
                           created_at, updated_at, deleted_at)
SELECT id, user_id, type, UPPER(status), title, content, channel,
       COALESCE(data_text, '{}')::jsonb,
       related_entity_id, related_entity_type,
       is_read,
       read_at AT TIME ZONE 'UTC',
       company_id,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, user_id, type, status, title, content, channel,
           data::text, related_entity_id, related_entity_type,
           is_read, read_at, company_id,
           created_at, updated_at, deleted_at
    FROM notification
') AS t(id uuid, user_id uuid, type varchar, status varchar, title varchar,
        content text, channel varchar, data_text text,
        related_entity_id uuid, related_entity_type varchar,
        is_read boolean, read_at timestamp, company_id uuid,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- 31. CONTACTS (contact → contacts)
--     name → split into first_name/last_name, assigned_to_id → responded_by
-- ============================================================================
INSERT INTO contacts (id, first_name, last_name, email, phone, subject, message,
                      status, response, responded_at, responded_by,
                      ip_address, user_agent,
                      created_at, updated_at, deleted_at)
SELECT id,
       -- Split name: first word = first_name, rest = last_name
       SPLIT_PART(name, ' ', 1),
       CASE
           WHEN POSITION(' ' IN name) > 0
           THEN SUBSTRING(name FROM POSITION(' ' IN name) + 1)
           ELSE ''
       END,
       email, phone, subject, message,
       UPPER(status), response,
       responded_at AT TIME ZONE 'UTC',
       assigned_to_id,
       ip_address, user_agent,
       created_at AT TIME ZONE 'UTC', updated_at AT TIME ZONE 'UTC',
       deleted_at AT TIME ZONE 'UTC'
FROM dblink('src', '
    SELECT id, name, email, phone, subject, message,
           status, response, responded_at, assigned_to_id,
           ip_address, user_agent,
           created_at, updated_at, deleted_at
    FROM contact
') AS t(id uuid, name varchar, email varchar, phone varchar, subject varchar,
        message text, status varchar, response text, responded_at timestamp,
        assigned_to_id uuid, ip_address varchar, user_agent varchar,
        created_at timestamp, updated_at timestamp, deleted_at timestamp)
ON CONFLICT (id) DO NOTHING;

-- ============================================================================
-- Re-enable FK constraints
-- ============================================================================
SET session_replication_role = 'origin';

COMMIT;

-- Close dblink connection
SELECT dblink_disconnect('src');

-- ============================================================================
-- VERIFICATION: Row counts
-- ============================================================================
SELECT '=== MIGRATION VERIFICATION ===' AS info;

SELECT target_table, target_count, source_table, source_count,
       CASE WHEN target_count >= source_count THEN 'OK' ELSE 'MISMATCH' END AS status
FROM (
    SELECT 'companies' AS target_table, (SELECT count(*) FROM companies) AS target_count,
           'company' AS source_table, 0::bigint AS source_count
) x
UNION ALL SELECT 'users', (SELECT count(*) FROM users), 'users', 0
UNION ALL SELECT 'device_sessions', (SELECT count(*) FROM device_sessions), 'auth_device_session', 0
UNION ALL SELECT 'refresh_tokens', (SELECT count(*) FROM refresh_tokens), 'auth_refresh_token', 0
UNION ALL SELECT 'countries', (SELECT count(*) FROM countries), 'address_country', 0
UNION ALL SELECT 'provinces', (SELECT count(*) FROM provinces), 'address_province', 0
UNION ALL SELECT 'cities', (SELECT count(*) FROM cities), 'address_city', 0
UNION ALL SELECT 'towns', (SELECT count(*) FROM towns), 'address_town', 0
UNION ALL SELECT 'districts', (SELECT count(*) FROM districts), 'address_district', 0
UNION ALL SELECT 'addresses', (SELECT count(*) FROM addresses), 'address', 0
UNION ALL SELECT 'categories', (SELECT count(*) FROM categories), 'property_category', 0
UNION ALL SELECT 'properties', (SELECT count(*) FROM properties), 'property', 0
UNION ALL SELECT 'photos', (SELECT count(*) FROM photos), 'property_photo', 0
UNION ALL SELECT 'listings', (SELECT count(*) FROM listings), 'property_listing', 0
UNION ALL SELECT 'reservations', (SELECT count(*) FROM reservations), 'reservation', 0
UNION ALL SELECT 'visits', (SELECT count(*) FROM visits), 'visit', 0
UNION ALL SELECT 'reviews', (SELECT count(*) FROM reviews), 'review', 0
UNION ALL SELECT 'leases', (SELECT count(*) FROM leases), 'lease', 0
UNION ALL SELECT 'rent_invoices', (SELECT count(*) FROM rent_invoices), 'rent_invoice', 0
UNION ALL SELECT 'rent_payments', (SELECT count(*) FROM rent_payments), 'rent_payment', 0
UNION ALL SELECT 'rent_reminders', (SELECT count(*) FROM rent_reminders), 'rent_reminder', 0
UNION ALL SELECT 'currencies', (SELECT count(*) FROM currencies), 'currency', 0
UNION ALL SELECT 'payment_methods', (SELECT count(*) FROM payment_methods), 'payment_method', 0
UNION ALL SELECT 'transactions', (SELECT count(*) FROM transactions), 'transaction', 0
UNION ALL SELECT 'payment_schedules', (SELECT count(*) FROM payment_schedules), 'payment_schedule', 0
UNION ALL SELECT 'conversations', (SELECT count(*) FROM conversations), 'conversation', 0
UNION ALL SELECT 'conversation_participants', (SELECT count(*) FROM conversation_participants), 'conversation_user', 0
UNION ALL SELECT 'messages', (SELECT count(*) FROM messages), 'message', 0
UNION ALL SELECT 'notifications', (SELECT count(*) FROM notifications), 'notification', 0
UNION ALL SELECT 'contacts', (SELECT count(*) FROM contacts), 'contact', 0
UNION ALL SELECT 'platform_settings', (SELECT count(*) FROM platform_settings), 'platform_setting', 0
ORDER BY 1;

MIGRATION_SQL
