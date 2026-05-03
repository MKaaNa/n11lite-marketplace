-- Add one more product-related review for remaining high-sales, low-review products

INSERT INTO reviews (user_id, product_id, rating, comment, created_at, updated_at)
SELECT u.id,
       p.id,
       v.rating,
       v.comment,
       NOW() - ((v.days_ago || ' days')::INTERVAL),
       NOW() - ((v.days_ago || ' days')::INTERVAL)
FROM (VALUES
    ('nilay.demo2@n11lite.local', 'riot-points-digital-code', 4, 'Teslim edilen kod sorunsuz calisti, bakiye hizli yansidi.', 9),
    ('emre.demo2@n11lite.local',  'playstation-store-gift-card-250-tl', 4, 'Kod aktivasyonu kolaydi, urun aciklamasi yeterliydi.', 8),
    ('mert.demo2@n11lite.local',  'razer-gold-e-pin-500-tl', 4, 'Kod kisa surede geldi, odeme sonrasi bekleme yasamadim.', 7),
    ('deniz.demo2@n11lite.local', 'foldable-laundry-basket', 4, 'Katlanir yapisi depolamada avantaj sagliyor, malzeme yeterli.', 14),
    ('selin.demo2@n11lite.local', 'soft-cotton-bed-sheet-set', 4, 'Kumas yumusak, olcu yatakla uyumlu cikti.', 13),
    ('ece.demo2@n11lite.local',   'active-sport-shorts', 4, 'Hareket rahatligi iyi, spor sirasinda konforlu hissettirdi.', 12),
    ('kerem.demo2@n11lite.local', 'wireless-bluetooth-headphones', 5, 'Baglanti stabil, ses seviyesi ve bass dengesi basarili.', 11),
    ('derya.demo2@n11lite.local', 'ergonomic-wireless-mouse', 4, 'Uzun kullanimda ele oturusu rahat, tiklama hissi net.', 10),
    ('baran.demo2@n11lite.local', 'decorative-cushion-cover-set', 4, 'Kumas dokusu guzel, renkler urun fotolariyla uyumlu.', 9),
    ('ceren.demo2@n11lite.local', 'canvas-shoulder-bag', 4, 'Dikis kalitesi iyi, gunluk esya tasimak icin ideal hacimde.', 8),
    ('nilay.demo2@n11lite.local', 'glass-spice-jar-set', 4, 'Kapaklar saglam, mutfakta duzen acisindan faydali oldu.', 7),
    ('emre.demo2@n11lite.local',  'design-patterns-quick-guide', 4, 'Konu ozetleri pratik, tekrar icin hizli okunuyor.', 10)
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
