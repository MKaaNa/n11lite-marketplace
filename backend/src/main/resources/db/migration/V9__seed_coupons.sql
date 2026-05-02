INSERT INTO coupons (
    code,
    discount_type,
    discount_value,
    min_order_amount,
    max_discount_amount,
    active,
    used_count,
    created_at,
    updated_at
) VALUES
('N11WELCOME', 'PERCENT', 10.00, 100.00, 100.00, TRUE, 0, NOW(), NOW()),
('TECH50', 'FIXED', 50.00, 300.00, NULL, TRUE, 0, NOW(), NOW());
