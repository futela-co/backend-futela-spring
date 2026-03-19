-- ============================================
-- V003: Property Schema (Single Table Inheritance)
-- ============================================

-- Properties (STI - all types in one table)
CREATE TABLE properties (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Discriminator column
    type VARCHAR(31) NOT NULL,

    -- Common fields
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    listing_type VARCHAR(50) NOT NULL DEFAULT 'RENT',
    price_per_day DECIMAL(10, 2) NOT NULL,
    price_per_month DECIMAL(10, 2),
    sale_price DECIMAL(15, 2),
    slug VARCHAR(255) NOT NULL UNIQUE,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    view_count INTEGER NOT NULL DEFAULT 0,
    rating DECIMAL(3, 2),
    review_count INTEGER NOT NULL DEFAULT 0,
    deletion_reason VARCHAR(500),

    -- Shared columns (Apartment/House)
    bedrooms INTEGER,
    bathrooms INTEGER,
    square_meters INTEGER,

    -- Car-specific columns
    brand VARCHAR(255),
    model VARCHAR(255),
    year INTEGER,
    mileage INTEGER,
    fuel_type VARCHAR(50),
    transmission VARCHAR(50),

    -- EventHall-specific column
    capacity INTEGER,

    -- Land-specific columns
    land_type VARCHAR(50),
    surface_area INTEGER,

    -- JSON attributes (type-specific non-filtered fields)
    attributes JSONB DEFAULT '{}',

    -- Foreign keys
    owner_id UUID NOT NULL REFERENCES users(id),
    category_id UUID REFERENCES categories(id),
    address_id UUID NOT NULL REFERENCES addresses(id),
    company_id UUID NOT NULL REFERENCES companies(id),

    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_properties_owner_id ON properties(owner_id);
CREATE INDEX idx_properties_category_id ON properties(category_id);
CREATE INDEX idx_properties_address_id ON properties(address_id);
CREATE INDEX idx_properties_company_id ON properties(company_id);
CREATE INDEX idx_properties_type ON properties(type);
CREATE INDEX idx_properties_status ON properties(status);
CREATE INDEX idx_properties_listing_type ON properties(listing_type);
CREATE INDEX idx_properties_slug ON properties(slug);
CREATE INDEX idx_properties_price ON properties(price_per_day);
CREATE INDEX idx_properties_published ON properties(is_published) WHERE is_published = TRUE;
CREATE INDEX idx_properties_bedrooms ON properties(bedrooms) WHERE bedrooms IS NOT NULL;

-- Photos
CREATE TABLE photos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    url VARCHAR(1000) NOT NULL,
    caption VARCHAR(500),
    display_order INTEGER NOT NULL DEFAULT 0,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_photos_property_id ON photos(property_id);

-- Listings (Favorites)
CREATE TABLE listings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    property_id UUID NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    company_id UUID NOT NULL REFERENCES companies(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_listing_user_property UNIQUE (user_id, property_id)
);

CREATE INDEX idx_listings_user_id ON listings(user_id);
CREATE INDEX idx_listings_property_id ON listings(property_id);
