-- Increase review density for low-review demo products.
-- Goal: keep product pages more realistic for presentation by targeting at least 4 reviews per product.

WITH target_products AS (
    SELECT
        p.id AS product_id,
        p.slug,
        COALESCE(COUNT(r.id), 0) AS review_count
    FROM products p
    LEFT JOIN reviews r ON r.product_id = p.id
    GROUP BY p.id, p.slug
    HAVING COALESCE(COUNT(r.id), 0) < 4
),
demo_users AS (
    SELECT
        u.id AS user_id,
        ROW_NUMBER() OVER (ORDER BY u.id) AS rn
    FROM users u
    WHERE u.role = 'USER'
),
demo_user_count AS (
    SELECT COUNT(*) AS cnt FROM demo_users
),
review_slots AS (
    SELECT
        tp.product_id,
        tp.slug,
        gs.slot_no,
        ((ROW_NUMBER() OVER (ORDER BY tp.product_id, gs.slot_no) - 1)
            % (SELECT cnt FROM demo_user_count)) + 1 AS user_rn
    FROM target_products tp
    JOIN LATERAL generate_series(1, 4 - tp.review_count) AS gs(slot_no) ON TRUE
)
INSERT INTO reviews (user_id, product_id, rating, comment, created_at, updated_at)
SELECT
    du.user_id,
    rs.product_id,
    CASE rs.slot_no
        WHEN 1 THEN 5
        WHEN 2 THEN 4
        WHEN 3 THEN 3
        ELSE 4
    END AS rating,
    CASE rs.slot_no
        WHEN 1 THEN 'Fiyat/performans dengesi iyi, günlük kullanımda beklentiyi karşılıyor.'
        WHEN 2 THEN 'Genel kalite tatmin edici, kargo ve paketleme sorunsuz ulaştı.'
        WHEN 3 THEN 'Ürün iş görüyor, açıklama kısmına birkaç ek detay daha faydalı olurdu.'
        ELSE 'Demo akışında sorunsuz görünüyor, genel deneyim olumlu.'
    END AS comment,
    NOW() - ((15 + rs.slot_no)::text || ' days')::interval,
    NOW() - ((15 + rs.slot_no)::text || ' days')::interval
FROM review_slots rs
JOIN demo_users du ON du.rn = rs.user_rn
WHERE NOT EXISTS (
    SELECT 1
    FROM reviews r
    WHERE r.user_id = du.user_id
      AND r.product_id = rs.product_id
);
