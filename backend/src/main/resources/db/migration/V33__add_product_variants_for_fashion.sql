CREATE TABLE product_variants (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    variant_type VARCHAR(40) NOT NULL,
    variant_value VARCHAR(40) NOT NULL,
    stock INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_product_variants_product FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT uk_product_variants_product_type_value UNIQUE (product_id, variant_type, variant_value)
);

CREATE INDEX idx_product_variants_product_id ON product_variants (product_id);

ALTER TABLE cart_items
    ADD COLUMN product_variant_id BIGINT NULL,
    ADD CONSTRAINT fk_cart_items_variant FOREIGN KEY (product_variant_id) REFERENCES product_variants (id);

ALTER TABLE cart_items
    DROP CONSTRAINT IF EXISTS uk_cart_items_cart_product;

CREATE UNIQUE INDEX uk_cart_items_cart_product_variant
    ON cart_items (cart_id, product_id, COALESCE(product_variant_id, -1));

INSERT INTO product_variants (product_id, variant_type, variant_value, stock, active)
SELECT p.id, v.variant_type, v.variant_value, v.stock, TRUE
FROM (
    VALUES
        ('basic-cotton-t-shirt', 'BEDEN', 'S', 20),
        ('basic-cotton-t-shirt', 'BEDEN', 'M', 28),
        ('basic-cotton-t-shirt', 'BEDEN', 'L', 24),
        ('basic-cotton-t-shirt', 'BEDEN', 'XL', 18),

        ('premium-denim-jeans', 'BEDEN', '30', 14),
        ('premium-denim-jeans', 'BEDEN', '32', 20),
        ('premium-denim-jeans', 'BEDEN', '34', 19),
        ('premium-denim-jeans', 'BEDEN', '36', 12),

        ('high-waist-leggings', 'BEDEN', 'S', 18),
        ('high-waist-leggings', 'BEDEN', 'M', 23),
        ('high-waist-leggings', 'BEDEN', 'L', 21),

        ('daily-cap', 'BEDEN', 'S/M', 16),
        ('daily-cap', 'BEDEN', 'L/XL', 14),

        ('daily-socks-5-pack', 'NUMARA', '39-42', 26),
        ('daily-socks-5-pack', 'NUMARA', '43-46', 22),

        ('canvas-shoulder-bag', 'BOYUT', 'Standart', 20),
        ('unisex-hoodie-basic', 'BEDEN', 'S', 15),
        ('unisex-hoodie-basic', 'BEDEN', 'M', 20),
        ('unisex-hoodie-basic', 'BEDEN', 'L', 19),
        ('unisex-hoodie-basic', 'BEDEN', 'XL', 14),
        ('active-sport-shorts', 'BEDEN', 'S', 14),
        ('active-sport-shorts', 'BEDEN', 'M', 18),
        ('active-sport-shorts', 'BEDEN', 'L', 16)
) AS v(slug, variant_type, variant_value, stock)
JOIN products p ON p.slug = v.slug
WHERE NOT EXISTS (
    SELECT 1
    FROM product_variants pv
    WHERE pv.product_id = p.id
      AND pv.variant_type = v.variant_type
      AND pv.variant_value = v.variant_value
);

SELECT setval('product_variants_id_seq', (SELECT COALESCE(MAX(id), 1) FROM product_variants), TRUE);
