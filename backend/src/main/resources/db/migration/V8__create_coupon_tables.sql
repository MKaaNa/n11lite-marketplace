CREATE TABLE coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_type VARCHAR(20) NOT NULL,
    discount_value NUMERIC(12, 2) NOT NULL,
    min_order_amount NUMERIC(12, 2),
    max_discount_amount NUMERIC(12, 2),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    usage_limit INTEGER,
    used_count INTEGER NOT NULL DEFAULT 0,
    starts_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT coupons_discount_value_positive CHECK (discount_value > 0),
    CONSTRAINT coupons_used_count_positive CHECK (used_count >= 0)
);

ALTER TABLE orders
    ADD COLUMN coupon_code VARCHAR(50),
    ADD COLUMN discount_amount NUMERIC(12, 2) NOT NULL DEFAULT 0;
