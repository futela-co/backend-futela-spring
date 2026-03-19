-- ============================================================
-- V009: Add missing relationship columns and FK constraints
-- ============================================================

-- 1. REVIEWS: Add reviewee_id column (nullable for existing data)
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS reviewee_id UUID;

-- Set reviewee_id = user_id for existing reviews (self-review as default)
UPDATE reviews SET reviewee_id = user_id WHERE reviewee_id IS NULL;

-- Add FK constraints
ALTER TABLE reviews
    ADD CONSTRAINT fk_review_reviewee FOREIGN KEY (reviewee_id) REFERENCES users(id);
ALTER TABLE reviews
    ADD CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(id);

-- Add index
CREATE INDEX IF NOT EXISTS idx_review_reviewee ON reviews (reviewee_id);

-- 2. CONVERSATIONS: Add FK constraint on property_id
ALTER TABLE conversations
    ADD CONSTRAINT fk_conversation_property FOREIGN KEY (property_id) REFERENCES properties(id);

-- 3. CONTACTS: Add FK constraint on assigned_to
ALTER TABLE contacts
    ADD CONSTRAINT fk_contact_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id);
