-- Final realism balance: prevent all-5-star clusters on popular items.

INSERT INTO reviews (user_id, product_id, rating, comment, created_at, updated_at)
SELECT u.id,
       p.id,
       v.rating,
       v.comment,
       NOW() - ((v.days_ago || ' days')::INTERVAL),
       NOW() - ((v.days_ago || ' days')::INTERVAL)
FROM (VALUES
    ('nilay.demo2@n11lite.local', 'wireless-bluetooth-headphones', 3, 'Ses performansi iyi ama uzun toplantilarda kulak yastiklari biraz terletiyor.', 4),
    ('emre.demo2@n11lite.local',  'clean-code-basics', 3, 'Temel seviye icin iyi fakat ileri seviye okuyucuya kisa gelebilir.', 6),
    ('baran.demo2@n11lite.local', 'minimal-desk-lamp', 3, 'Isik tonu guzel ama govde aci ayari daha sert olabilir.', 5),
    ('ceren.demo2@n11lite.local', 'sql-performance-playbook', 2, 'Bazi bolumlerde aciklama yogun, ornek sorgu adedi daha fazla olmaliydi.', 7)
) AS v(email, slug, rating, comment, days_ago)
JOIN users u ON u.email = v.email
JOIN products p ON p.slug = v.slug
WHERE NOT EXISTS (
    SELECT 1
    FROM reviews r
    WHERE r.user_id = u.id
      AND r.product_id = p.id
);

SELECT setval('reviews_id_seq', (SELECT COALESCE(MAX(id), 1) FROM reviews), TRUE);
