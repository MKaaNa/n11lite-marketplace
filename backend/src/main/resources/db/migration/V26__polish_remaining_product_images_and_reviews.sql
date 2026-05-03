-- Demo seed-data polish:
-- 1) Remaining product image mismatches
-- 2) Review diversity and density improvements

-- -----------------------------------------------------
-- Product image fixes (remaining mismatches)
-- -----------------------------------------------------

-- Ev ve Yasam
UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1622560481156-01fc7e1693e6?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'desk-organizer-wooden')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1586953208448-b95a79798f07?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'desk-organizer-wooden')
  AND display_order = 2;

-- Elektronik
UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1591488320449-011701bb6704?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'usb-c-hub-8-in-1')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1558002038-1055e2e28ed1?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'smart-home-plug')
  AND display_order = 1;

-- Kitap (teknik/calisma masasi odakli, daha cesitli)
UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'clean-architecture-in-practice')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'refactoring-essentials')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1484417894907-623942c8ee29?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'domain-driven-design-notes')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'microservices-patterns-handbook')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'design-patterns-quick-guide')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1516116216624-53e697fedbea?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'spring-security-fundamentals')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'sql-performance-playbook')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'api-design-best-practices')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1515876305429-78d3e950f5fa?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'unit-testing-for-teams')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1484480974693-6ca0a78fb36b?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'pragmatic-devops-guide')
  AND display_order = 1;

-- Moda (corap: logosuz urun odakli)
UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1543163521-1bf539c55dd2?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'daily-socks-5-pack')
  AND display_order = 1;

-- Dijital Kodlar (klasordeki lokal varliklara geri don)
UPDATE product_images
SET image_url = '/assets/products/digital-codes/steam-10-usd.jpeg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'steam-wallet-code-10-usd');

UPDATE product_images
SET image_url = '/assets/products/digital-codes/steam-20-usd.png'
WHERE product_id = (SELECT id FROM products WHERE slug = 'steam-wallet-code-20-usd');

UPDATE product_images
SET image_url = '/assets/products/digital-codes/google-play-250-tl.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'google-play-gift-card-250-tl');

UPDATE product_images
SET image_url = '/assets/products/digital-codes/xbox-game-pass-1-month.png'
WHERE product_id = (SELECT id FROM products WHERE slug = 'xbox-game-pass-ultimate-1-month');

UPDATE product_images
SET image_url = '/assets/products/digital-codes/playstation-250-tl.png'
WHERE product_id = (SELECT id FROM products WHERE slug = 'playstation-store-gift-card-250-tl');

UPDATE product_images
SET image_url = '/assets/products/digital-codes/razer-gold-500-tl.png'
WHERE product_id = (SELECT id FROM products WHERE slug = 'razer-gold-e-pin-500-tl');

UPDATE product_images
SET image_url = '/assets/products/digital-codes/riot-points.png'
WHERE product_id = (SELECT id FROM products WHERE slug = 'riot-points-digital-code');

UPDATE product_images
SET image_url = '/assets/products/digital-codes/nintendo-eshop-20-usd.png'
WHERE product_id = (SELECT id FROM products WHERE slug = 'nintendo-eshop-gift-card-20-usd');

-- -----------------------------------------------------
-- Review diversity improvements
-- -----------------------------------------------------

-- Additional demo users (idempotent)
INSERT INTO users (email, password_hash, full_name, phone, role, created_at)
SELECT v.email, v.password_hash, v.full_name, v.phone, 'USER', NOW()
FROM (VALUES
    ('mert.demo2@n11lite.local',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Mert Kaya', '+905551111201'),
    ('deniz.demo2@n11lite.local',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Deniz Arslan', '+905551111202'),
    ('selin.demo2@n11lite.local',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Selin Demir', '+905551111203'),
    ('ece.demo2@n11lite.local',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Ece Sahin', '+905551111204'),
    ('kerem.demo2@n11lite.local',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Kerem Aydin', '+905551111205'),
    ('derya.demo2@n11lite.local',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Derya Aksoy', '+905551111206'),
    ('baran.demo2@n11lite.local',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Baran Yalcin', '+905551111207'),
    ('ceren.demo2@n11lite.local',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Ceren Polat', '+905551111208'),
    ('nilay.demo2@n11lite.local',  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Nilay Ozkan', '+905551111209'),
    ('emre.demo2@n11lite.local',   '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Emre Yildiz', '+905551111210')
) AS v(email, password_hash, full_name, phone)
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.email = v.email);

-- Admin User yorumlarini normal demo kullanicilara dagit
UPDATE reviews r
SET user_id = u.id,
    updated_at = NOW()
FROM users u
WHERE u.email = 'mert.demo2@n11lite.local'
  AND r.user_id = (SELECT id FROM users WHERE email = 'admin@n11lite.com')
  AND r.product_id = (SELECT id FROM products WHERE slug = 'wireless-bluetooth-headphones')
  AND NOT EXISTS (SELECT 1 FROM reviews x WHERE x.user_id = u.id AND x.product_id = r.product_id);

UPDATE reviews r
SET user_id = u.id,
    updated_at = NOW()
FROM users u
WHERE u.email = 'deniz.demo2@n11lite.local'
  AND r.user_id = (SELECT id FROM users WHERE email = 'admin@n11lite.com')
  AND r.product_id = (SELECT id FROM products WHERE slug = '4k-action-camera')
  AND NOT EXISTS (SELECT 1 FROM reviews x WHERE x.user_id = u.id AND x.product_id = r.product_id);

UPDATE reviews r
SET user_id = u.id,
    updated_at = NOW()
FROM users u
WHERE u.email = 'selin.demo2@n11lite.local'
  AND r.user_id = (SELECT id FROM users WHERE email = 'admin@n11lite.com')
  AND r.product_id = (SELECT id FROM products WHERE slug = 'ergonomic-wireless-mouse')
  AND NOT EXISTS (SELECT 1 FROM reviews x WHERE x.user_id = u.id AND x.product_id = r.product_id);

UPDATE reviews r
SET user_id = u.id,
    updated_at = NOW()
FROM users u
WHERE u.email = 'ece.demo2@n11lite.local'
  AND r.user_id = (SELECT id FROM users WHERE email = 'admin@n11lite.com')
  AND r.product_id = (SELECT id FROM products WHERE slug = 'clean-architecture-in-practice')
  AND NOT EXISTS (SELECT 1 FROM reviews x WHERE x.user_id = u.id AND x.product_id = r.product_id);

UPDATE reviews r
SET user_id = u.id,
    updated_at = NOW()
FROM users u
WHERE u.email = 'kerem.demo2@n11lite.local'
  AND r.user_id = (SELECT id FROM users WHERE email = 'admin@n11lite.com')
  AND r.product_id = (SELECT id FROM products WHERE slug = 'sql-performance-playbook')
  AND NOT EXISTS (SELECT 1 FROM reviews x WHERE x.user_id = u.id AND x.product_id = r.product_id);

UPDATE reviews r
SET user_id = u.id,
    updated_at = NOW()
FROM users u
WHERE u.email = 'derya.demo2@n11lite.local'
  AND r.user_id = (SELECT id FROM users WHERE email = 'admin@n11lite.com')
  AND r.product_id = (SELECT id FROM products WHERE slug = 'kitchen-organizer-shelf')
  AND NOT EXISTS (SELECT 1 FROM reviews x WHERE x.user_id = u.id AND x.product_id = r.product_id);

UPDATE reviews r
SET user_id = u.id,
    updated_at = NOW()
FROM users u
WHERE u.email = 'baran.demo2@n11lite.local'
  AND r.user_id = (SELECT id FROM users WHERE email = 'admin@n11lite.com')
  AND r.product_id = (SELECT id FROM products WHERE slug = 'daily-cap')
  AND NOT EXISTS (SELECT 1 FROM reviews x WHERE x.user_id = u.id AND x.product_id = r.product_id);

UPDATE reviews r
SET user_id = u.id,
    updated_at = NOW()
FROM users u
WHERE u.email = 'ceren.demo2@n11lite.local'
  AND r.user_id = (SELECT id FROM users WHERE email = 'admin@n11lite.com')
  AND r.product_id = (SELECT id FROM products WHERE slug = 'daily-socks-5-pack')
  AND NOT EXISTS (SELECT 1 FROM reviews x WHERE x.user_id = u.id AND x.product_id = r.product_id);

-- Ek yorumlar: populer ve gosterim urunlerinde 2-4 araligina yaklas
INSERT INTO reviews (user_id, product_id, rating, comment, created_at, updated_at)
SELECT u.id,
       p.id,
       v.rating,
       v.comment,
       NOW() - ((v.days_ago || ' days')::INTERVAL),
       NOW() - ((v.days_ago || ' days')::INTERVAL)
FROM (VALUES
    ('mert.demo2@n11lite.local',  'wireless-bluetooth-headphones', 4, 'Ses kalitesi dengeli, toplantilarda mikrofon performansi da yeterli.', 26),
    ('deniz.demo2@n11lite.local', 'usb-c-hub-8-in-1', 4, 'Port cesitliligi guzel, isinir mi diye baktim ama normal seviyede kaldi.', 24),
    ('selin.demo2@n11lite.local', 'smart-home-plug', 5, 'Kurulumu kolaydi, zamanlayici ozelligi gunluk rutinde cok ise yariyor.', 23),
    ('ece.demo2@n11lite.local',   'desk-organizer-wooden', 4, 'Masaustu kablo ve kalem daginikligini toparladi, olcusu ideal.', 22),
    ('kerem.demo2@n11lite.local', 'minimal-desk-lamp', 5, 'Isik tonu calisma masasinda goz yormuyor, govde dengeli.', 21),
    ('derya.demo2@n11lite.local', 'bath-towel-set', 4, 'Kumas yumusak ve emicilik iyi, paketleme duzgun geldi.', 20),
    ('baran.demo2@n11lite.local', 'basic-cotton-t-shirt', 4, 'Kalibi rahat, gunluk kullanima uygun bir tisort.', 19),
    ('ceren.demo2@n11lite.local', 'high-waist-leggings', 5, 'Kumas toparlayici, spor sirasinda hareketi kisitlamiyor.', 18),
    ('nilay.demo2@n11lite.local', 'daily-socks-5-pack', 4, 'Paket icerigi yeterli, gunluk kullanimda konforlu hissettiriyor.', 17),
    ('emre.demo2@n11lite.local',  'clean-architecture-in-practice', 4, 'Konu anlatimi sade, ornekler pratikte dogrudan karsilik buluyor.', 16),
    ('mert.demo2@n11lite.local',  'sql-performance-playbook', 5, 'Index ve query plani bolumu dogrudan isime yaradi.', 15),
    ('deniz.demo2@n11lite.local', 'spring-security-fundamentals', 4, 'Baslangic icin gayet acik, bazi bolumler biraz daha detayli olabilirdi.', 14),
    ('selin.demo2@n11lite.local', 'api-design-best-practices', 5, 'API sozlesmesi ve hata yaniti yaklasimi cok net anlatilmis.', 13),
    ('ece.demo2@n11lite.local',   'modern-java-guide', 4, 'Java tarafinda temel konulari toparlamak icin iyi bir kaynak.', 12),
    ('kerem.demo2@n11lite.local', 'steam-wallet-code-10-usd', 5, 'Kod teslimi hizliydi, demo akista aktivasyon sorunsuz ilerledi.', 11),
    ('derya.demo2@n11lite.local', 'steam-wallet-code-20-usd', 4, 'Dijital teslimat akisinda kod kismi net, deneyim akiciydi.', 10),
    ('baran.demo2@n11lite.local', 'google-play-gift-card-250-tl', 4, 'Teslimat bekledigim hizda geldi, kullanim adimlari anlasilir.', 9),
    ('ceren.demo2@n11lite.local', 'xbox-game-pass-ultimate-1-month', 5, 'Kod aktivasyonu sorunsuz, urun aciklamasiyla uyumlu.', 8),
    ('nilay.demo2@n11lite.local', 'nintendo-eshop-gift-card-20-usd', 3, 'Teslimat tamamdi, bolge uyumlulugu bilgisinin daha belirgin olmasi iyi olur.', 7),
    ('emre.demo2@n11lite.local',  'kitchen-organizer-shelf', 4, 'Mutfakta alan kazandirdi, kurulum adimi kolaydi.', 6)
) AS v(email, slug, rating, comment, days_ago)
JOIN users u ON u.email = v.email
JOIN products p ON p.slug = v.slug
WHERE NOT EXISTS (
    SELECT 1
    FROM reviews r
    WHERE r.user_id = u.id
      AND r.product_id = p.id
);

SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 1) FROM users), TRUE);
SELECT setval('reviews_id_seq', (SELECT COALESCE(MAX(id), 1) FROM reviews), TRUE);
