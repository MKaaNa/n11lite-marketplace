-- Increase review density for high-selling products with low review counts
-- Keeps seed data realistic by adding product-relevant demo comments.

INSERT INTO reviews (user_id, product_id, rating, comment, created_at, updated_at)
SELECT u.id,
       p.id,
       v.rating,
       v.comment,
       NOW() - ((v.days_ago || ' days')::INTERVAL),
       NOW() - ((v.days_ago || ' days')::INTERVAL)
FROM (VALUES
    ('mert.demo2@n11lite.local',  'steam-wallet-code-10-usd', 5, 'Kod teslimati cok hizliydi, aktivasyon adimlari netti.', 14),
    ('deniz.demo2@n11lite.local', 'steam-wallet-code-10-usd', 4, 'Bakiyeye sorunsuz tanimlandi, aciklama ile uyumlu.', 11),
    ('selin.demo2@n11lite.local', 'google-play-gift-card-250-tl', 5, 'Kod aninda geldi, mobilde kullanmasi pratiktı.', 13),
    ('ece.demo2@n11lite.local',   'google-play-gift-card-250-tl', 4, 'Teslimat hizi iyi, kod formati anlasilir.', 10),
    ('kerem.demo2@n11lite.local', 'riot-points-digital-code', 4, 'RP kodu hizli geldi, hesapta dogru bakiyeye donustu.', 12),
    ('derya.demo2@n11lite.local', 'playstation-store-gift-card-250-tl', 4, 'PS Store tarafinda sorunsuz aktif oldu.', 9),
    ('baran.demo2@n11lite.local', 'razer-gold-e-pin-500-tl', 4, 'Kod teslimati bekledigimden hizliydi, akista sorun yasamadim.', 8),
    ('ceren.demo2@n11lite.local', 'steam-wallet-code-20-usd', 5, 'Kod hemen tanimlandi, urun aciklamasi yeterliydi.', 16),
    ('nilay.demo2@n11lite.local', 'xbox-game-pass-ultimate-1-month', 4, 'Uyelik aktivasyonu sorunsuz, sure bilgisi net.', 7),
    ('emre.demo2@n11lite.local',  'nintendo-eshop-gift-card-20-usd', 4, 'Kod sorunsuz calisti, bolge notu faydali olur.', 6),
    ('mert.demo2@n11lite.local',  'portable-power-bank-20000-mah', 4, 'Sarj suresi iyi, gunluk tasimada agirlik dengeli.', 18),
    ('deniz.demo2@n11lite.local', 'daily-cap', 4, 'Kalibi rahat, kumasi gunluk kullanim icin uygun.', 15),
    ('selin.demo2@n11lite.local', 'bluetooth-speaker-mini', 4, 'Ses seviyesi boyutuna gore yeterli, baglanti hizli.', 17),
    ('ece.demo2@n11lite.local',   'clean-code-basics', 5, 'Ornekler sade ve anlasilir, ekip icinde referans olur.', 19),
    ('kerem.demo2@n11lite.local', 'double-layer-storage-box', 4, 'Malzeme hissi iyi, evde duzeni toplamada ise yariyor.', 20),
    ('derya.demo2@n11lite.local', 'bamboo-cutting-board-set', 4, 'Set olmasi pratik, farkli boylar mutfakta ise yariyor.', 12),
    ('baran.demo2@n11lite.local', 'wireless-charging-stand', 4, 'Masada duzen sagliyor, sarj hizi beklentimi karsiladi.', 14),
    ('ceren.demo2@n11lite.local', 'unisex-hoodie-basic', 4, 'Kumas yumusak, beden olcusu urun aciklamasiyla uyumlu.', 11)
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
