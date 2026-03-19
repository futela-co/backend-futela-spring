-- ============================================================
-- V009 - Seed Fixtures (test data for development)
-- ============================================================
-- NOTE: V001 already created company 'Futela' (id: 00000000-...0001)
-- NOTE: V002 already seeded RDC geographic data (country, provinces, cities, communes, districts)
-- NOTE: V006 already seeded USD/CDF currencies and 4 payment methods (MPESA, AIRTEL, ORANGE, CASH)
-- ============================================================

-- ============================================================
-- 1. Additional Company
-- ============================================================
INSERT INTO companies (id, name, slug, email, phone, is_active) VALUES
    ('00000000-0000-0000-0000-000000000002', 'Kinshasa Properties', 'kinshasa-properties', 'info@kinproperties.cd', '+243 815 000 001', TRUE)
ON CONFLICT (slug) DO NOTHING;

-- ============================================================
-- 2. EUR Currency (USD and CDF already in V006)
-- ============================================================
INSERT INTO currencies (code, name, symbol, exchange_rate) VALUES
    ('EUR', 'Euro', '€', 0.9200)
ON CONFLICT (code) DO NOTHING;

-- ============================================================
-- 3. Platform Settings
-- ============================================================
INSERT INTO platform_settings (setting_key, value, category, description) VALUES
    ('visit_fee', '5.00', 'payment', 'Frais de visite par défaut (USD)'),
    ('default_currency', 'USD', 'general', 'Devise par défaut de la plateforme'),
    ('reservation_commission', '10', 'payment', 'Commission sur les réservations (%)')
ON CONFLICT (setting_key) DO NOTHING;

-- ============================================================
-- 4. Additional Districts (complement V002 which only has Gombe and Ngaliema districts)
-- ============================================================
INSERT INTO districts (id, name, town_id, city_id) VALUES
    -- Limete
    ('44444444-4444-4444-4444-444444444010', 'Industriel', '33333333-3333-3333-3333-333333333012', '22222222-2222-2222-2222-222222222001'),
    ('44444444-4444-4444-4444-444444444011', 'Résidentiel', '33333333-3333-3333-3333-333333333012', '22222222-2222-2222-2222-222222222001'),
    ('44444444-4444-4444-4444-444444444012', 'Mombele', '33333333-3333-3333-3333-333333333012', '22222222-2222-2222-2222-222222222001'),
    -- Kalamu
    ('44444444-4444-4444-4444-444444444013', 'Matonge', '33333333-3333-3333-3333-333333333005', '22222222-2222-2222-2222-222222222001'),
    ('44444444-4444-4444-4444-444444444014', 'Yolo', '33333333-3333-3333-3333-333333333005', '22222222-2222-2222-2222-222222222001'),
    -- Kintambo
    ('44444444-4444-4444-4444-444444444015', 'Kintambo Magasin', '33333333-3333-3333-3333-333333333009', '22222222-2222-2222-2222-222222222001'),
    ('44444444-4444-4444-4444-444444444016', 'Jamaïque', '33333333-3333-3333-3333-333333333009', '22222222-2222-2222-2222-222222222001'),
    -- Lemba
    ('44444444-4444-4444-4444-444444444017', 'Lemba Salongo', '33333333-3333-3333-3333-333333333011', '22222222-2222-2222-2222-222222222001'),
    ('44444444-4444-4444-4444-444444444018', 'Righini', '33333333-3333-3333-3333-333333333011', '22222222-2222-2222-2222-222222222001'),
    -- Bandalungwa
    ('44444444-4444-4444-4444-444444444019', 'Camp Luka', '33333333-3333-3333-3333-333333333001', '22222222-2222-2222-2222-222222222001'),
    ('44444444-4444-4444-4444-444444444020', 'Bisengo', '33333333-3333-3333-3333-333333333001', '22222222-2222-2222-2222-222222222001'),
    -- Masina
    ('44444444-4444-4444-4444-444444444021', 'Sans Fil', '33333333-3333-3333-3333-333333333016', '22222222-2222-2222-2222-222222222001'),
    ('44444444-4444-4444-4444-444444444022', 'Petro Congo', '33333333-3333-3333-3333-333333333016', '22222222-2222-2222-2222-222222222001'),
    -- Mont-Ngafula
    ('44444444-4444-4444-4444-444444444023', 'Université', '33333333-3333-3333-3333-333333333018', '22222222-2222-2222-2222-222222222001'),
    ('44444444-4444-4444-4444-444444444024', 'Mama Mobutu', '33333333-3333-3333-3333-333333333018', '22222222-2222-2222-2222-222222222001'),
    -- Barumbu
    ('44444444-4444-4444-4444-444444444025', 'Beach Ngobila', '33333333-3333-3333-3333-333333333002', '22222222-2222-2222-2222-222222222001'),
    -- Ndjili
    ('44444444-4444-4444-4444-444444444026', 'Aéroport', '33333333-3333-3333-3333-333333333019', '22222222-2222-2222-2222-222222222001'),
    -- Ngiri-Ngiri
    ('44444444-4444-4444-4444-444444444027', 'Elengesa', '33333333-3333-3333-3333-333333333022', '22222222-2222-2222-2222-222222222001')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 5. Users
-- Password hash = bcrypt of "password123"
-- ============================================================
-- $2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa

-- SUPER_ADMIN
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, is_verified, email_verified_at, company_id) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa001', 'superadmin@futela.com', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Super', 'Admin', '+243 999 100 001', 'SUPER_ADMIN', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000001')
ON CONFLICT (email) DO NOTHING;

-- ADMIN - Futela Immo
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, is_verified, email_verified_at, company_id) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa002', 'admin@futela.com', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Admin', 'Futela', '+243 999 100 002', 'ADMIN', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000001')
ON CONFLICT (email) DO NOTHING;

-- OWNERS - Futela Immo
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, is_verified, email_verified_at, company_id) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003', 'pierre.mbuyi@futela.com', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Pierre', 'Mbuyi', '+243 812 345 001', 'OWNER', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000001'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa004', 'grace.kabongo@futela.com', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Grace', 'Kabongo', '+243 815 678 002', 'OWNER', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000001')
ON CONFLICT (email) DO NOTHING;

-- TENANTS - Futela Immo
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, is_verified, email_verified_at, company_id) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa005', 'jean.tshimanga@futela.com', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Jean', 'Tshimanga', '+243 811 222 003', 'TENANT', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000001'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa006', 'marie.nseka@futela.com', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Marie', 'Nseka', '+243 997 333 004', 'TENANT', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000001')
ON CONFLICT (email) DO NOTHING;

-- USERS - Futela Immo
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, is_verified, email_verified_at, company_id) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa007', 'patrick.kalala@futela.com', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Patrick', 'Kalala', '+243 814 444 005', 'USER', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000001'),
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa008', 'esther.lunda@futela.com', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Esther', 'Lunda', '+243 818 555 006', 'USER', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000001')
ON CONFLICT (email) DO NOTHING;

-- ADMIN - Kinshasa Properties
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, is_verified, email_verified_at, company_id) VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb001', 'admin@kinproperties.cd', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Joseph', 'Mukendi', '+243 813 600 001', 'ADMIN', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000002')
ON CONFLICT (email) DO NOTHING;

-- OWNERS - Kinshasa Properties
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, is_verified, email_verified_at, company_id) VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb002', 'david.kasongo@kinproperties.cd', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'David', 'Kasongo', '+243 816 700 002', 'OWNER', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000002'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb003', 'rachel.mutombo@kinproperties.cd', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Rachel', 'Mutombo', '+243 819 800 003', 'OWNER', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000002')
ON CONFLICT (email) DO NOTHING;

-- TENANTS - Kinshasa Properties
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, is_verified, email_verified_at, company_id) VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb004', 'alice.kasa@kinproperties.cd', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Alice', 'Kasa', '+243 811 900 004', 'TENANT', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000002'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb005', 'samuel.lubala@kinproperties.cd', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Samuel', 'Lubala', '+243 997 100 005', 'TENANT', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000002')
ON CONFLICT (email) DO NOTHING;

-- USERS - Kinshasa Properties
INSERT INTO users (id, email, password_hash, first_name, last_name, phone, role, status, is_verified, email_verified_at, company_id) VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb006', 'papy.ilunga@kinproperties.cd', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Papy', 'Ilunga', '+243 815 200 006', 'USER', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000002'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb007', 'merveille.ngoy@kinproperties.cd', '$2a$12$LJ3m4ys3ZS8v8E8.D6zNru2EvSP3fGJiB.zZ5FXMQU0X3kY8fNAZa',
     'Merveille', 'Ngoy', '+243 818 300 007', 'USER', 'ACTIVE', TRUE, NOW(), '00000000-0000-0000-0000-000000000002')
ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- 6. Categories (5 per company)
-- ============================================================
-- Futela Immo
INSERT INTO categories (id, name, slug, description, is_active, company_id) VALUES
    ('cccccccc-cccc-cccc-cccc-ccccccccc001', 'Résidentiel', 'residentiel-futela', 'Propriétés résidentielles (appartements, maisons)', TRUE, '00000000-0000-0000-0000-000000000001'),
    ('cccccccc-cccc-cccc-cccc-ccccccccc002', 'Commercial', 'commercial-futela', 'Espaces commerciaux et bureaux', TRUE, '00000000-0000-0000-0000-000000000001'),
    ('cccccccc-cccc-cccc-cccc-ccccccccc003', 'Luxe', 'luxe-futela', 'Propriétés haut de gamme', TRUE, '00000000-0000-0000-0000-000000000001'),
    ('cccccccc-cccc-cccc-cccc-ccccccccc004', 'Étudiant', 'etudiant-futela', 'Logements pour étudiants', TRUE, '00000000-0000-0000-0000-000000000001'),
    ('cccccccc-cccc-cccc-cccc-ccccccccc005', 'Vacances', 'vacances-futela', 'Locations de vacances et courts séjours', TRUE, '00000000-0000-0000-0000-000000000001')
ON CONFLICT (slug) DO NOTHING;

-- Kinshasa Properties
INSERT INTO categories (id, name, slug, description, is_active, company_id) VALUES
    ('cccccccc-cccc-cccc-cccc-ccccccccc006', 'Résidentiel', 'residentiel-kinproperties', 'Propriétés résidentielles', TRUE, '00000000-0000-0000-0000-000000000002'),
    ('cccccccc-cccc-cccc-cccc-ccccccccc007', 'Commercial', 'commercial-kinproperties', 'Espaces commerciaux', TRUE, '00000000-0000-0000-0000-000000000002'),
    ('cccccccc-cccc-cccc-cccc-ccccccccc008', 'Luxe', 'luxe-kinproperties', 'Propriétés de luxe', TRUE, '00000000-0000-0000-0000-000000000002'),
    ('cccccccc-cccc-cccc-cccc-ccccccccc009', 'Étudiant', 'etudiant-kinproperties', 'Logements étudiants', TRUE, '00000000-0000-0000-0000-000000000002'),
    ('cccccccc-cccc-cccc-cccc-ccccccccc010', 'Vacances', 'vacances-kinproperties', 'Locations vacances', TRUE, '00000000-0000-0000-0000-000000000002')
ON CONFLICT (slug) DO NOTHING;

-- ============================================================
-- 7. Addresses (for properties)
-- ============================================================
INSERT INTO addresses (id, street, number, latitude, longitude, district_id, town_id, city_id, province_id, country_id) VALUES
    -- Gombe addresses
    ('55555555-5555-5555-5555-555555555001', 'Avenue du Commerce', '45', -4.3100, 15.3100,
     '44444444-4444-4444-4444-444444444003', '33333333-3333-3333-3333-333333333004',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    ('55555555-5555-5555-5555-555555555002', 'Boulevard du 30 Juin', '120', -4.3050, 15.3050,
     '44444444-4444-4444-4444-444444444001', '33333333-3333-3333-3333-333333333004',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    -- Ngaliema addresses
    ('55555555-5555-5555-5555-555555555003', 'Avenue de la Libération', '88', -4.3300, 15.2800,
     '44444444-4444-4444-4444-444444444004', '33333333-3333-3333-3333-333333333021',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    ('55555555-5555-5555-5555-555555555004', 'Avenue Ma Campagne', '15', -4.3350, 15.2750,
     '44444444-4444-4444-4444-444444444006', '33333333-3333-3333-3333-333333333021',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    -- Limete addresses
    ('55555555-5555-5555-5555-555555555005', 'Avenue de l''Université', '200', -4.3400, 15.3200,
     '44444444-4444-4444-4444-444444444011', '33333333-3333-3333-3333-333333333012',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    -- Kalamu addresses
    ('55555555-5555-5555-5555-555555555006', 'Avenue Kabinda', '32', -4.3250, 15.3150,
     '44444444-4444-4444-4444-444444444013', '33333333-3333-3333-3333-333333333005',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    -- Lemba addresses
    ('55555555-5555-5555-5555-555555555007', 'Avenue de la Science', '5', -4.3500, 15.3100,
     '44444444-4444-4444-4444-444444444017', '33333333-3333-3333-3333-333333333011',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    -- Kintambo
    ('55555555-5555-5555-5555-555555555008', 'Avenue Kintambo', '77', -4.3150, 15.2900,
     '44444444-4444-4444-4444-444444444015', '33333333-3333-3333-3333-333333333009',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    -- Bandalungwa
    ('55555555-5555-5555-5555-555555555009', 'Avenue Camp Luka', '12', -4.3200, 15.3000,
     '44444444-4444-4444-4444-444444444019', '33333333-3333-3333-3333-333333333001',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    -- Mont-Ngafula
    ('55555555-5555-5555-5555-555555555010', 'Avenue de l''Université', '300', -4.3800, 15.2600,
     '44444444-4444-4444-4444-444444444023', '33333333-3333-3333-3333-333333333018',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    -- Masina
    ('55555555-5555-5555-5555-555555555011', 'Avenue Petro Congo', '55', -4.3600, 15.3500,
     '44444444-4444-4444-4444-444444444022', '33333333-3333-3333-3333-333333333016',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),
    -- Ndjili
    ('55555555-5555-5555-5555-555555555012', 'Avenue Aéroport', '1', -4.3900, 15.4300,
     '44444444-4444-4444-4444-444444444026', '33333333-3333-3333-3333-333333333019',
     '22222222-2222-2222-2222-222222222001', '11111111-1111-1111-1111-111111111001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 8. Properties (10+ with mix of types and statuses)
-- ============================================================

-- === APARTMENTS (3) ===

-- Apartment 1 - Published, Gombe
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, price_per_month, slug, is_published, bedrooms, bathrooms, square_meters,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd001', 'APARTMENT',
     'Appartement Moderne Gombe', 'Magnifique appartement de standing dans le quartier des affaires de la Gombe. Vue panoramique sur le fleuve Congo. Entièrement meublé avec finitions haut de gamme.',
     'PUBLISHED', 'RENT', 150.00, 3500.00, 'appartement-moderne-gombe', TRUE, 3, 2, 120,
     '{"floor": 8, "hasElevator": true, "hasParking": true, "hasBalcony": true, "hasFurnished": true}',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003', 'cccccccc-cccc-cccc-cccc-ccccccccc003',
     '55555555-5555-5555-5555-555555555001', '00000000-0000-0000-0000-000000000001')
ON CONFLICT (slug) DO NOTHING;

-- Apartment 2 - Published, Ngaliema
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, price_per_month, slug, is_published, bedrooms, bathrooms, square_meters,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd002', 'APARTMENT',
     'Studio Binza-Pigeon', 'Joli studio meublé à Binza-Pigeon avec vue dégagée. Idéal pour jeune professionnel ou étudiant. Eau et électricité 24h/24.',
     'PUBLISHED', 'RENT', 50.00, 800.00, 'studio-binza-pigeon', TRUE, 1, 1, 35,
     '{"floor": 3, "hasElevator": false, "hasParking": false, "hasBalcony": true, "hasFurnished": true}',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003', 'cccccccc-cccc-cccc-cccc-ccccccccc004',
     '55555555-5555-5555-5555-555555555003', '00000000-0000-0000-0000-000000000001')
ON CONFLICT (slug) DO NOTHING;

-- Apartment 3 - Draft, Limete
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, price_per_month, slug, is_published, bedrooms, bathrooms, square_meters,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd003', 'APARTMENT',
     'Appartement Familial Limete', 'Grand appartement familial dans le quartier résidentiel de Limete. 4 chambres spacieuses, salon double, cuisine équipée.',
     'DRAFT', 'RENT', 100.00, 2200.00, 'appartement-familial-limete', FALSE, 4, 2, 150,
     '{"floor": 2, "hasElevator": false, "hasParking": true, "hasBalcony": false, "hasFurnished": false}',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa004', 'cccccccc-cccc-cccc-cccc-ccccccccc001',
     '55555555-5555-5555-5555-555555555005', '00000000-0000-0000-0000-000000000001')
ON CONFLICT (slug) DO NOTHING;

-- === HOUSES (3) ===

-- House 1 - Published, Ma Campagne
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, price_per_month, slug, is_published, bedrooms, bathrooms, square_meters,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd004', 'HOUSE',
     'Villa Luxueuse Ma Campagne', 'Splendide villa de 5 chambres avec piscine, jardin tropical et dépendance. Quartier calme et sécurisé de Ma Campagne.',
     'PUBLISHED', 'RENT', 350.00, 8000.00, 'villa-luxueuse-ma-campagne', TRUE, 5, 4, 350,
     '{"landSquareMeters": 800, "floors": 2, "hasGarden": true, "hasPool": true, "hasGarage": true, "hasGuardHouse": true}',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003', 'cccccccc-cccc-cccc-cccc-ccccccccc003',
     '55555555-5555-5555-5555-555555555004', '00000000-0000-0000-0000-000000000001')
ON CONFLICT (slug) DO NOTHING;

-- House 2 - Published, Lemba
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, price_per_month, slug, is_published, bedrooms, bathrooms, square_meters,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd005', 'HOUSE',
     'Maison Familiale Lemba', 'Belle maison familiale de 3 chambres dans un quartier résidentiel calme. Proche des universités et des commerces.',
     'PUBLISHED', 'RENT', 120.00, 1800.00, 'maison-familiale-lemba', TRUE, 3, 2, 180,
     '{"landSquareMeters": 400, "floors": 1, "hasGarden": true, "hasPool": false, "hasGarage": true}',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa004', 'cccccccc-cccc-cccc-cccc-ccccccccc001',
     '55555555-5555-5555-5555-555555555007', '00000000-0000-0000-0000-000000000001')
ON CONFLICT (slug) DO NOTHING;

-- House 3 - Draft, Bandalungwa
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, price_per_month, slug, is_published, bedrooms, bathrooms, square_meters,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd006', 'HOUSE',
     'Maison Camp Luka', 'Maison à rénover avec un grand terrain. Idéal pour investissement locatif. Quartier populaire et bien desservi.',
     'DRAFT', 'SALE', 80.00, NULL, 'maison-camp-luka', FALSE, 4, 2, 200,
     '{"landSquareMeters": 600, "floors": 1, "hasGarden": true, "hasPool": false, "hasGarage": false}',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb002', 'cccccccc-cccc-cccc-cccc-ccccccccc006',
     '55555555-5555-5555-5555-555555555009', '00000000-0000-0000-0000-000000000002')
ON CONFLICT (slug) DO NOTHING;

-- === LANDS (2) ===

-- Land 1 - Published, Mont-Ngafula
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, slug, is_published, surface_area, sale_price,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd007', 'LAND',
     'Terrain Mont-Ngafula 1500m²', 'Grand terrain plat avec titre foncier. Accès eau et électricité. Zone résidentielle calme près de l''Université de Kinshasa.',
     'PUBLISHED', 'SALE', 30.00, 'terrain-mont-ngafula-1500m2', TRUE, 1500, 75000.00,
     '{"hasBuildingPermit": true, "hasElectricityAccess": true, "hasWaterAccess": true, "isFenced": true}',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003', 'cccccccc-cccc-cccc-cccc-ccccccccc001',
     '55555555-5555-5555-5555-555555555010', '00000000-0000-0000-0000-000000000001')
ON CONFLICT (slug) DO NOTHING;

-- Land 2 - Published, Masina
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, slug, is_published, surface_area, sale_price,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd008', 'LAND',
     'Parcelle Masina 500m²', 'Parcelle résidentielle à Masina, quartier Petro Congo. Idéal pour construction de maison familiale.',
     'PUBLISHED', 'SALE', 15.00, 'parcelle-masina-500m2', TRUE, 500, 25000.00,
     '{"hasBuildingPermit": false, "hasElectricityAccess": true, "hasWaterAccess": false, "isFenced": false}',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb002', 'cccccccc-cccc-cccc-cccc-ccccccccc006',
     '55555555-5555-5555-5555-555555555011', '00000000-0000-0000-0000-000000000002')
ON CONFLICT (slug) DO NOTHING;

-- === CAR (1) ===

-- Car - Published, Kintambo
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, slug, is_published,
    brand, model, year, mileage, fuel_type, transmission,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd009', 'CAR',
     'Toyota Land Cruiser V8 2022', 'Toyota Land Cruiser V8 en excellent état. Climatisation, cuir, GPS. Parfait pour les déplacements en ville et hors route.',
     'PUBLISHED', 'RENT', 200.00, 'toyota-land-cruiser-v8-2022', TRUE,
     'Toyota', 'Land Cruiser V8', 2022, 35000, 'DIESEL', 'AUTOMATIC',
     '{"color": "Blanc", "seats": 7, "hasAC": true, "hasGPS": true}',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa004', 'cccccccc-cccc-cccc-cccc-ccccccccc005',
     '55555555-5555-5555-5555-555555555008', '00000000-0000-0000-0000-000000000001')
ON CONFLICT (slug) DO NOTHING;

-- === EVENT HALL (1) ===

-- Event Hall - Published, Socimat/Gombe
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, slug, is_published, capacity,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd010', 'EVENT_HALL',
     'Salle Royal Palace Gombe', 'Salle de réception premium au coeur de la Gombe. Capacité 300 personnes. Cuisine équipée, parking, sonorisation professionnelle.',
     'PUBLISHED', 'RENT', 1500.00, 'salle-royal-palace-gombe', TRUE, 300,
     '{"hasKitchen": true, "hasParking": true, "hasSoundSystem": true, "hasVideoProjector": true, "hasAC": true}',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003', 'cccccccc-cccc-cccc-cccc-ccccccccc002',
     '55555555-5555-5555-5555-555555555002', '00000000-0000-0000-0000-000000000001')
ON CONFLICT (slug) DO NOTHING;

-- === Additional properties for Kinshasa Properties company ===

-- Apartment - KP, Kalamu
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, price_per_month, slug, is_published, bedrooms, bathrooms, square_meters,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd011', 'APARTMENT',
     'Appartement Matonge 2 Chambres', 'Appartement lumineux à Matonge, quartier vivant de Kalamu. Proche des marchés et transports en commun.',
     'PUBLISHED', 'RENT', 60.00, 1000.00, 'appartement-matonge-2ch', TRUE, 2, 1, 70,
     '{"floor": 1, "hasElevator": false, "hasParking": false, "hasBalcony": true, "hasFurnished": false}',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb003', 'cccccccc-cccc-cccc-cccc-ccccccccc006',
     '55555555-5555-5555-5555-555555555006', '00000000-0000-0000-0000-000000000002')
ON CONFLICT (slug) DO NOTHING;

-- House - KP, Ndjili
INSERT INTO properties (id, type, title, description, status, listing_type, price_per_day, price_per_month, slug, is_published, bedrooms, bathrooms, square_meters,
    attributes, owner_id, category_id, address_id, company_id) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddd012', 'HOUSE',
     'Maison Ndjili Aéroport', 'Maison spacieuse proche de l''aéroport de Ndjili. 3 chambres, terrain clôturé. Idéal pour voyageurs fréquents.',
     'PUBLISHED', 'RENT', 90.00, 1500.00, 'maison-ndjili-aeroport', TRUE, 3, 2, 160,
     '{"landSquareMeters": 500, "floors": 1, "hasGarden": true, "hasPool": false, "hasGarage": true}',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb002', 'cccccccc-cccc-cccc-cccc-ccccccccc006',
     '55555555-5555-5555-5555-555555555012', '00000000-0000-0000-0000-000000000002')
ON CONFLICT (slug) DO NOTHING;

-- ============================================================
-- 9. Photos (2-3 per property)
-- ============================================================
INSERT INTO photos (id, property_id, url, caption, display_order, is_primary) VALUES
    -- Appartement Moderne Gombe (3 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee001', 'dddddddd-dddd-dddd-dddd-ddddddddd001', 'https://picsum.photos/seed/apt-gombe-1/800/600', 'Salon moderne', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee002', 'dddddddd-dddd-dddd-dddd-ddddddddd001', 'https://picsum.photos/seed/apt-gombe-2/800/600', 'Chambre principale', 1, FALSE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee003', 'dddddddd-dddd-dddd-dddd-ddddddddd001', 'https://picsum.photos/seed/apt-gombe-3/800/600', 'Vue sur le fleuve', 2, FALSE),

    -- Studio Binza-Pigeon (2 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee004', 'dddddddd-dddd-dddd-dddd-ddddddddd002', 'https://picsum.photos/seed/studio-binza-1/800/600', 'Intérieur du studio', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee005', 'dddddddd-dddd-dddd-dddd-ddddddddd002', 'https://picsum.photos/seed/studio-binza-2/800/600', 'Cuisine équipée', 1, FALSE),

    -- Appartement Familial Limete (2 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee006', 'dddddddd-dddd-dddd-dddd-ddddddddd003', 'https://picsum.photos/seed/apt-limete-1/800/600', 'Façade extérieure', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee007', 'dddddddd-dddd-dddd-dddd-ddddddddd003', 'https://picsum.photos/seed/apt-limete-2/800/600', 'Salon spacieux', 1, FALSE),

    -- Villa Ma Campagne (3 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee008', 'dddddddd-dddd-dddd-dddd-ddddddddd004', 'https://picsum.photos/seed/villa-camp-1/800/600', 'Façade principale', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee009', 'dddddddd-dddd-dddd-dddd-ddddddddd004', 'https://picsum.photos/seed/villa-camp-2/800/600', 'Piscine', 1, FALSE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee010', 'dddddddd-dddd-dddd-dddd-ddddddddd004', 'https://picsum.photos/seed/villa-camp-3/800/600', 'Jardin tropical', 2, FALSE),

    -- Maison Lemba (2 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee011', 'dddddddd-dddd-dddd-dddd-ddddddddd005', 'https://picsum.photos/seed/maison-lemba-1/800/600', 'Entrée principale', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee012', 'dddddddd-dddd-dddd-dddd-ddddddddd005', 'https://picsum.photos/seed/maison-lemba-2/800/600', 'Jardin arrière', 1, FALSE),

    -- Maison Camp Luka (2 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee013', 'dddddddd-dddd-dddd-dddd-ddddddddd006', 'https://picsum.photos/seed/maison-camp-1/800/600', 'Vue d''ensemble', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee014', 'dddddddd-dddd-dddd-dddd-ddddddddd006', 'https://picsum.photos/seed/maison-camp-2/800/600', 'Terrain', 1, FALSE),

    -- Terrain Mont-Ngafula (2 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee015', 'dddddddd-dddd-dddd-dddd-ddddddddd007', 'https://picsum.photos/seed/terrain-ngaf-1/800/600', 'Vue aérienne', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee016', 'dddddddd-dddd-dddd-dddd-ddddddddd007', 'https://picsum.photos/seed/terrain-ngaf-2/800/600', 'Accès route', 1, FALSE),

    -- Parcelle Masina (2 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee017', 'dddddddd-dddd-dddd-dddd-ddddddddd008', 'https://picsum.photos/seed/parcelle-mas-1/800/600', 'Vue terrain', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee018', 'dddddddd-dddd-dddd-dddd-ddddddddd008', 'https://picsum.photos/seed/parcelle-mas-2/800/600', 'Environnement', 1, FALSE),

    -- Toyota Land Cruiser (3 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee019', 'dddddddd-dddd-dddd-dddd-ddddddddd009', 'https://picsum.photos/seed/toyota-lc-1/800/600', 'Vue extérieure', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee020', 'dddddddd-dddd-dddd-dddd-ddddddddd009', 'https://picsum.photos/seed/toyota-lc-2/800/600', 'Intérieur cuir', 1, FALSE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee021', 'dddddddd-dddd-dddd-dddd-ddddddddd009', 'https://picsum.photos/seed/toyota-lc-3/800/600', 'Tableau de bord', 2, FALSE),

    -- Salle Royal Palace (3 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee022', 'dddddddd-dddd-dddd-dddd-ddddddddd010', 'https://picsum.photos/seed/salle-royal-1/800/600', 'Salle principale', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee023', 'dddddddd-dddd-dddd-dddd-ddddddddd010', 'https://picsum.photos/seed/salle-royal-2/800/600', 'Configuration banquet', 1, FALSE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee024', 'dddddddd-dddd-dddd-dddd-ddddddddd010', 'https://picsum.photos/seed/salle-royal-3/800/600', 'Cuisine', 2, FALSE),

    -- Appartement Matonge (2 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee025', 'dddddddd-dddd-dddd-dddd-ddddddddd011', 'https://picsum.photos/seed/apt-matonge-1/800/600', 'Salon', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee026', 'dddddddd-dddd-dddd-dddd-ddddddddd011', 'https://picsum.photos/seed/apt-matonge-2/800/600', 'Chambre', 1, FALSE),

    -- Maison Ndjili (2 photos)
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee027', 'dddddddd-dddd-dddd-dddd-ddddddddd012', 'https://picsum.photos/seed/maison-ndjili-1/800/600', 'Façade', 0, TRUE),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeee028', 'dddddddd-dddd-dddd-dddd-ddddddddd012', 'https://picsum.photos/seed/maison-ndjili-2/800/600', 'Cour intérieure', 1, FALSE)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 10. Leases (3 active leases)
-- ============================================================
INSERT INTO leases (id, property_id, tenant_id, landlord_id, company_id, status, monthly_rent, currency, deposit_amount, start_date, end_date, payment_day_of_month, notes) VALUES
    -- Lease 1: Jean Tshimanga rents Appartement Gombe from Pierre Mbuyi
    ('ffffffff-ffff-ffff-ffff-fffffffffff1', 'dddddddd-dddd-dddd-dddd-ddddddddd001',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa005', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003',
     '00000000-0000-0000-0000-000000000001', 'ACTIVE', 3500.00, 'USD', 7000.00,
     '2025-01-01', '2025-12-31', 5, 'Bail annuel, paiement le 5 de chaque mois'),

    -- Lease 2: Marie Nseka rents Maison Lemba from Grace Kabongo
    ('ffffffff-ffff-ffff-ffff-fffffffffff2', 'dddddddd-dddd-dddd-dddd-ddddddddd005',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa006', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa004',
     '00000000-0000-0000-0000-000000000001', 'ACTIVE', 1800.00, 'USD', 3600.00,
     '2025-03-01', '2026-02-28', 1, NULL),

    -- Lease 3: Alice Kasa rents Appartement Matonge from Rachel Mutombo
    ('ffffffff-ffff-ffff-ffff-fffffffffff3', 'dddddddd-dddd-dddd-dddd-ddddddddd011',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb004', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb003',
     '00000000-0000-0000-0000-000000000002', 'ACTIVE', 1000.00, 'USD', 2000.00,
     '2025-06-01', '2026-05-31', 10, 'Paiement le 10 de chaque mois')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 11. Rent Invoices (mix of PAID and PENDING)
-- ============================================================
INSERT INTO rent_invoices (id, lease_id, company_id, invoice_number, amount, paid_amount, status, due_date, period_start, period_end) VALUES
    -- Lease 1 invoices
    ('11111111-aaaa-bbbb-cccc-111111111001', 'ffffffff-ffff-ffff-ffff-fffffffffff1', '00000000-0000-0000-0000-000000000001',
     'INV-2025-001-01', 3500.00, 3500.00, 'PAID', '2025-01-05', '2025-01-01', '2025-01-31'),
    ('11111111-aaaa-bbbb-cccc-111111111002', 'ffffffff-ffff-ffff-ffff-fffffffffff1', '00000000-0000-0000-0000-000000000001',
     'INV-2025-001-02', 3500.00, 3500.00, 'PAID', '2025-02-05', '2025-02-01', '2025-02-28'),
    ('11111111-aaaa-bbbb-cccc-111111111003', 'ffffffff-ffff-ffff-ffff-fffffffffff1', '00000000-0000-0000-0000-000000000001',
     'INV-2025-001-03', 3500.00, 0.00, 'PENDING', '2025-03-05', '2025-03-01', '2025-03-31'),

    -- Lease 2 invoices
    ('11111111-aaaa-bbbb-cccc-111111111004', 'ffffffff-ffff-ffff-ffff-fffffffffff2', '00000000-0000-0000-0000-000000000001',
     'INV-2025-002-03', 1800.00, 1800.00, 'PAID', '2025-03-01', '2025-03-01', '2025-03-31'),
    ('11111111-aaaa-bbbb-cccc-111111111005', 'ffffffff-ffff-ffff-ffff-fffffffffff2', '00000000-0000-0000-0000-000000000001',
     'INV-2025-002-04', 1800.00, 0.00, 'PENDING', '2025-04-01', '2025-04-01', '2025-04-30'),

    -- Lease 3 invoices
    ('11111111-aaaa-bbbb-cccc-111111111006', 'ffffffff-ffff-ffff-ffff-fffffffffff3', '00000000-0000-0000-0000-000000000002',
     'INV-2025-003-06', 1000.00, 0.00, 'PENDING', '2025-06-10', '2025-06-01', '2025-06-30')
ON CONFLICT (invoice_number) DO NOTHING;

-- ============================================================
-- 12. Rent Payments (for paid invoices)
-- ============================================================
INSERT INTO rent_payments (id, invoice_id, lease_id, company_id, amount, payment_date, payment_method, reference) VALUES
    ('22222222-aaaa-bbbb-cccc-222222222001', '11111111-aaaa-bbbb-cccc-111111111001', 'ffffffff-ffff-ffff-ffff-fffffffffff1',
     '00000000-0000-0000-0000-000000000001', 3500.00, '2025-01-03', 'MPESA', 'TXN-FTL-20250103-001'),
    ('22222222-aaaa-bbbb-cccc-222222222002', '11111111-aaaa-bbbb-cccc-111111111002', 'ffffffff-ffff-ffff-ffff-fffffffffff1',
     '00000000-0000-0000-0000-000000000001', 3500.00, '2025-02-04', 'MPESA', 'TXN-FTL-20250204-001'),
    ('22222222-aaaa-bbbb-cccc-222222222003', '11111111-aaaa-bbbb-cccc-111111111004', 'ffffffff-ffff-ffff-ffff-fffffffffff2',
     '00000000-0000-0000-0000-000000000001', 1800.00, '2025-02-28', 'AIRTEL', 'TXN-FTL-20250228-001')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 13. Reservations
-- ============================================================
INSERT INTO reservations (id, property_id, user_id, host_id, company_id, status, start_date, end_date, total_price, currency, guest_count, notes) VALUES
    -- Patrick reserves Villa Ma Campagne for 3 days
    ('33333333-aaaa-bbbb-cccc-333333333001', 'dddddddd-dddd-dddd-dddd-ddddddddd004',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa007', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003',
     '00000000-0000-0000-0000-000000000001', 'CONFIRMED', '2026-04-10', '2026-04-13', 1050.00, 'USD', 4,
     'Réunion familiale'),

    -- Esther reserves Salle Royal Palace for 1 day
    ('33333333-aaaa-bbbb-cccc-333333333002', 'dddddddd-dddd-dddd-dddd-ddddddddd010',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa008', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003',
     '00000000-0000-0000-0000-000000000001', 'PENDING', '2026-05-20', '2026-05-21', 1500.00, 'USD', 150,
     'Mariage'),

    -- Papy reserves Toyota Land Cruiser for 5 days
    ('33333333-aaaa-bbbb-cccc-333333333003', 'dddddddd-dddd-dddd-dddd-ddddddddd009',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb006', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa004',
     '00000000-0000-0000-0000-000000000001', 'CONFIRMED', '2026-04-01', '2026-04-06', 1000.00, 'USD', 1,
     'Voyage Lubumbashi')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 14. Reviews
-- ============================================================
INSERT INTO reviews (id, property_id, user_id, company_id, rating, comment, is_approved) VALUES
    ('77777777-aaaa-bbbb-cccc-777777777001', 'dddddddd-dddd-dddd-dddd-ddddddddd001',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa007', '00000000-0000-0000-0000-000000000001',
     5, 'Appartement exceptionnel avec une vue magnifique. Service impeccable du propriétaire.', TRUE),

    ('77777777-aaaa-bbbb-cccc-777777777002', 'dddddddd-dddd-dddd-dddd-ddddddddd002',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa008', '00000000-0000-0000-0000-000000000001',
     4, 'Bon rapport qualité-prix pour un studio. Quartier un peu bruyant mais bien situé.', TRUE),

    ('77777777-aaaa-bbbb-cccc-777777777003', 'dddddddd-dddd-dddd-dddd-ddddddddd004',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa007', '00000000-0000-0000-0000-000000000001',
     5, 'Villa magnifique ! La piscine et le jardin sont incroyables. On y reviendra sans hésiter.', TRUE),

    ('77777777-aaaa-bbbb-cccc-777777777004', 'dddddddd-dddd-dddd-dddd-ddddddddd005',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa008', '00000000-0000-0000-0000-000000000001',
     3, 'Maison correcte mais quelques travaux de maintenance seraient nécessaires.', TRUE),

    ('77777777-aaaa-bbbb-cccc-777777777005', 'dddddddd-dddd-dddd-dddd-ddddddddd011',
     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb006', '00000000-0000-0000-0000-000000000002',
     4, 'Appartement bien situé à Matonge. Ambiance vivante du quartier. Recommandé.', TRUE)
ON CONFLICT ON CONSTRAINT uk_review_user_property DO NOTHING;

-- ============================================================
-- 15. Conversations & Messages
-- ============================================================
INSERT INTO conversations (id, subject, property_id, last_message_at, company_id) VALUES
    ('88888888-aaaa-bbbb-cccc-888888888001', 'Question sur l''Appartement Gombe',
     'dddddddd-dddd-dddd-dddd-ddddddddd001', '2026-03-18 14:30:00+02', '00000000-0000-0000-0000-000000000001'),
    ('88888888-aaaa-bbbb-cccc-888888888002', 'Visite Villa Ma Campagne',
     'dddddddd-dddd-dddd-dddd-ddddddddd004', '2026-03-17 10:00:00+02', '00000000-0000-0000-0000-000000000001')
ON CONFLICT (id) DO NOTHING;

INSERT INTO conversation_participants (conversation_id, user_id) VALUES
    ('88888888-aaaa-bbbb-cccc-888888888001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa007'),
    ('88888888-aaaa-bbbb-cccc-888888888001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003'),
    ('88888888-aaaa-bbbb-cccc-888888888002', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa007'),
    ('88888888-aaaa-bbbb-cccc-888888888002', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003')
ON CONFLICT (conversation_id, user_id) DO NOTHING;

INSERT INTO messages (id, conversation_id, sender_id, type, content, is_read, company_id) VALUES
    ('99999999-aaaa-bbbb-cccc-999999999001', '88888888-aaaa-bbbb-cccc-888888888001',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa007', 'TEXT',
     'Bonjour, est-ce que l''appartement à la Gombe est toujours disponible pour le mois prochain ?', TRUE, '00000000-0000-0000-0000-000000000001'),
    ('99999999-aaaa-bbbb-cccc-999999999002', '88888888-aaaa-bbbb-cccc-888888888001',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003', 'TEXT',
     'Bonjour Patrick, oui l''appartement est disponible. Souhaitez-vous organiser une visite ?', TRUE, '00000000-0000-0000-0000-000000000001'),
    ('99999999-aaaa-bbbb-cccc-999999999003', '88888888-aaaa-bbbb-cccc-888888888001',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa007', 'TEXT',
     'Oui, je serais disponible samedi matin. Est-ce possible ?', FALSE, '00000000-0000-0000-0000-000000000001'),

    ('99999999-aaaa-bbbb-cccc-999999999004', '88888888-aaaa-bbbb-cccc-888888888002',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa007', 'TEXT',
     'Bonjour, je souhaiterais visiter la villa à Ma Campagne. Quand est-ce possible ?', TRUE, '00000000-0000-0000-0000-000000000001'),
    ('99999999-aaaa-bbbb-cccc-999999999005', '88888888-aaaa-bbbb-cccc-888888888002',
     'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003', 'TEXT',
     'Bonjour, je peux vous recevoir demain à 10h. La villa est vraiment magnifique, vous allez adorer !', FALSE, '00000000-0000-0000-0000-000000000001')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 16. Notifications
-- ============================================================
INSERT INTO notifications (id, user_id, type, status, title, body, channel, is_read, company_id) VALUES
    ('aabbccdd-aaaa-bbbb-cccc-aabbccdd0001', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa005', 'PAYMENT', 'UNREAD',
     'Loyer dû', 'Votre loyer de mars 2025 (3 500 $) est dû le 5 mars.', 'PUSH', FALSE,
     '00000000-0000-0000-0000-000000000001'),
    ('aabbccdd-aaaa-bbbb-cccc-aabbccdd0002', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003', 'RESERVATION', 'READ',
     'Nouvelle réservation', 'Patrick Kalala a réservé votre Villa Ma Campagne du 10 au 13 avril.', 'PUSH', TRUE,
     '00000000-0000-0000-0000-000000000001'),
    ('aabbccdd-aaaa-bbbb-cccc-aabbccdd0003', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa003', 'REVIEW', 'UNREAD',
     'Nouvel avis', 'Patrick Kalala a laissé un avis 5 étoiles sur votre Appartement Gombe.', 'EMAIL', FALSE,
     '00000000-0000-0000-0000-000000000001'),
    ('aabbccdd-aaaa-bbbb-cccc-aabbccdd0004', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb004', 'PAYMENT', 'UNREAD',
     'Loyer dû', 'Votre loyer de juin 2025 (1 000 $) est dû le 10 juin.', 'PUSH', FALSE,
     '00000000-0000-0000-0000-000000000002')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 17. Transactions
-- ============================================================
INSERT INTO transactions (id, reference, type, status, amount, currency, phone_number, provider, user_id, company_id, description) VALUES
    ('ddddeeee-aaaa-bbbb-cccc-ddddeeee0001', 'TXN-FTL-20250103-001', 'RENT_PAYMENT', 'COMPLETED', 3500.00, 'USD',
     '+243811222003', 'MPESA', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa005', '00000000-0000-0000-0000-000000000001',
     'Paiement loyer janvier 2025 - Appartement Gombe'),
    ('ddddeeee-aaaa-bbbb-cccc-ddddeeee0002', 'TXN-FTL-20250204-001', 'RENT_PAYMENT', 'COMPLETED', 3500.00, 'USD',
     '+243811222003', 'MPESA', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa005', '00000000-0000-0000-0000-000000000001',
     'Paiement loyer février 2025 - Appartement Gombe'),
    ('ddddeeee-aaaa-bbbb-cccc-ddddeeee0003', 'TXN-FTL-20250228-001', 'RENT_PAYMENT', 'COMPLETED', 1800.00, 'USD',
     '+243997333004', 'AIRTEL', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa006', '00000000-0000-0000-0000-000000000001',
     'Paiement loyer mars 2025 - Maison Lemba'),
    ('ddddeeee-aaaa-bbbb-cccc-ddddeeee0004', 'TXN-FTL-20260410-001', 'RESERVATION', 'COMPLETED', 1050.00, 'USD',
     '+243814444005', 'MPESA', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaa007', '00000000-0000-0000-0000-000000000001',
     'Réservation Villa Ma Campagne - 10 au 13 avril 2026'),
    ('ddddeeee-aaaa-bbbb-cccc-ddddeeee0005', 'TXN-FTL-20260401-001', 'RESERVATION', 'PENDING', 1000.00, 'USD',
     '+243815200006', 'ORANGE', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbb006', '00000000-0000-0000-0000-000000000001',
     'Réservation Toyota Land Cruiser - 1 au 6 avril 2026')
ON CONFLICT (reference) DO NOTHING;
