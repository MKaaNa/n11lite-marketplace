-- Add mixed (including dissatisfied) reviews to improve demo realism.
-- Intentionally includes 1-2-3 star ratings with product-relevant feedback.

INSERT INTO reviews (user_id, product_id, rating, comment, created_at, updated_at)
SELECT u.id,
       p.id,
       v.rating,
       v.comment,
       NOW() - ((v.days_ago || ' days')::INTERVAL),
       NOW() - ((v.days_ago || ' days')::INTERVAL)
FROM (VALUES
    ('mert.demo2@n11lite.local',  'wireless-bluetooth-headphones', 2, 'Uzun kullanimda kulakta rahatsizlik yapti, ses fena degil ama konfor beklentimi karsilamadi.', 5),
    ('deniz.demo2@n11lite.local', 'portable-power-bank-20000-mah', 3, 'Kapasite idare eder ama bekledigimden agir, gunluk tasimada pratik gelmedi.', 6),
    ('selin.demo2@n11lite.local', 'daily-socks-5-pack', 2, 'Kumas baslarda iyi ama birkac yikamadan sonra elastikiyet azaldi.', 4),
    ('ece.demo2@n11lite.local',   'high-waist-leggings', 3, 'Kumas kaliteli ancak beden kalibi biraz dar, iade degisime gitmek zorunda kaldim.', 3),
    ('kerem.demo2@n11lite.local', 'daily-cap', 2, 'Fotograftaki tonla gelen urun rengi farkliydi, beklentimi dusurdu.', 5),
    ('derya.demo2@n11lite.local', 'smart-home-plug', 3, 'Kurulum tamamlandi ama uygulama eslestirmesi ilk denemede stabil degildi.', 7),
    ('baran.demo2@n11lite.local', 'steam-wallet-code-20-usd', 2, 'Kod calisti ama teslim suresi bir miktar uzundu, dijital urunde daha hizli bekliyordum.', 4),
    ('ceren.demo2@n11lite.local', 'nintendo-eshop-gift-card-20-usd', 2, 'Bolge uyumlulugu notu daha belirgin olmali, ilk denemede sorun yasadim.', 6),
    ('nilay.demo2@n11lite.local', 'wooden-serving-tray', 3, 'Yuzey guzel ama kenar finisaji daha iyi olabilirdi.', 8),
    ('emre.demo2@n11lite.local',  'bamboo-cutting-board-set', 2, 'En kucuk boy bekledigimden ince geldi, uzun omur konusunda emin olamadim.', 9),
    ('mert.demo2@n11lite.local',  'refactoring-essentials', 1, 'Icerik beklentime gore cok yuzeysel kaldi, pratik ornek sayisi az.', 5),
    ('deniz.demo2@n11lite.local', 'unit-testing-for-teams', 2, 'Bazi bolumler tekrarli, daha sistematik bir ilerleyis bekliyordum.', 4)
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
