ALTER TABLE users ADD COLUMN iyzico_card_user_key VARCHAR(255);

CREATE TABLE user_addresses (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    label           VARCHAR(80) NOT NULL,
    full_address    TEXT NOT NULL,
    is_default      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_addresses_user_id ON user_addresses (user_id);
