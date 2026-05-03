ALTER TABLE coupons
    ADD COLUMN IF NOT EXISTS product_id BIGINT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_coupons_product'
          AND table_name = 'coupons'
    ) THEN
        ALTER TABLE coupons
            ADD CONSTRAINT fk_coupons_product
            FOREIGN KEY (product_id) REFERENCES products(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_coupons_product_id ON coupons(product_id);

INSERT INTO coupons (
    code,
    product_id,
    discount_type,
    discount_value,
    min_order_amount,
    max_discount_amount,
    active,
    usage_limit,
    used_count,
    created_at,
    updated_at
) VALUES
('MONITOR500', (SELECT id FROM products WHERE slug = '27-inch-qhd-monitor' LIMIT 1), 'FIXED', 500.00, 3000.00, NULL, TRUE, NULL, 0, NOW(), NOW()),
('SSD10', (SELECT id FROM products WHERE slug = 'portable-ssd-1tb' LIMIT 1), 'PERCENT', 10.00, 1500.00, 350.00, TRUE, NULL, 0, NOW(), NOW()),
('ACTION300', (SELECT id FROM products WHERE slug = '4k-action-camera' LIMIT 1), 'FIXED', 300.00, 1200.00, NULL, TRUE, NULL, 0, NOW(), NOW())
ON CONFLICT (code) DO NOTHING;
