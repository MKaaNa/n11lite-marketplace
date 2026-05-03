INSERT INTO categories (id, name, slug, active, created_at)
VALUES (5, 'Dijital Kodlar', 'digital-codes', TRUE, NOW());

UPDATE products SET badge = NULL WHERE slug IN (
    'usb-c-hub-8-in-1',
    'portable-ssd-1tb',
    'sql-performance-playbook',
    'double-layer-storage-box',
    'bath-towel-set',
    'high-waist-leggings'
);

INSERT INTO products (
    id, name, slug, description, price, stock, sold_count, view_count, badge,
    category_id, store_id, active, created_at
) VALUES
    (53, 'Steam Cüzdan Kodu 10 USD', 'steam-wallet-code-10-usd', 'Steam hesabına bakiye yüklemek için dijital cüzdan kodu.', 389.90, 250, 410, 1200, 'BESTSELLER', 5, 1, TRUE, NOW()),
    (54, 'Steam Cüzdan Kodu 20 USD', 'steam-wallet-code-20-usd', 'Dijital teslimata uygun 20 USD tutarında cüzdan kodu.', 779.90, 180, 300, 950, NULL, 5, 1, TRUE, NOW()),
    (55, 'Google Play Hediye Kartı 250 TL', 'google-play-gift-card-250-tl', 'Uygulama, oyun ve dijital içerikler için hediye kartı.', 250.00, 220, 360, 1040, 'FEATURED', 5, 2, TRUE, NOW()),
    (56, 'Xbox Game Pass Ultimate 1 Aylık', 'xbox-game-pass-ultimate-1-month', 'Konsol ve PC oyun deneyimi için 1 aylık dijital üyelik kodu.', 299.90, 140, 240, 870, 'NEW', 5, 2, TRUE, NOW()),
    (57, 'PlayStation Store Hediye Kartı 250 TL', 'playstation-store-gift-card-250-tl', 'PlayStation Store alışverişlerinde kullanılabilen dijital hediye kartı.', 250.00, 165, 280, 910, NULL, 5, 3, TRUE, NOW()),
    (58, 'Razer Gold E-Pin 500 TL', 'razer-gold-e-pin-500-tl', 'Oyun içi satın alımlar için kullanılabilen dijital e-pin.', 500.00, 130, 210, 790, 'FREE_SHIPPING', 5, 3, TRUE, NOW()),
    (59, 'Riot Points Dijital Kod', 'riot-points-digital-code', 'Popüler oyun içi içerikler için dijital puan kodu.', 349.90, 190, 330, 990, 'BESTSELLER', 5, 4, TRUE, NOW()),
    (60, 'Nintendo eShop Hediye Kartı 20 USD', 'nintendo-eshop-gift-card-20-usd', 'Nintendo eShop hesabında kullanılabilen dijital hediye kartı.', 799.90, 95, 170, 640, NULL, 5, 4, TRUE, NOW());

INSERT INTO product_images (id, product_id, image_url, display_order) VALUES
    (65, 53, 'https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?auto=format&fit=crop&w=1200&q=80', 1),
    (66, 54, 'https://images.unsplash.com/photo-1556742502-ec7c0e9f34b1?auto=format&fit=crop&w=1200&q=80', 1),
    (67, 55, 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?auto=format&fit=crop&w=1200&q=80', 1),
    (68, 56, 'https://images.unsplash.com/photo-1542751371-adc38448a05e?auto=format&fit=crop&w=1200&q=80', 1),
    (69, 57, 'https://images.unsplash.com/photo-1556742111-a301076d9d18?auto=format&fit=crop&w=1200&q=80', 1),
    (70, 58, 'https://images.unsplash.com/photo-1605901309584-818e25960a8f?auto=format&fit=crop&w=1200&q=80', 1),
    (71, 59, 'https://images.unsplash.com/photo-1598550476439-6847785fcea6?auto=format&fit=crop&w=1200&q=80', 1),
    (72, 60, 'https://images.unsplash.com/photo-1511512578047-dfb367046420?auto=format&fit=crop&w=1200&q=80', 1);

SELECT setval('categories_id_seq', 5, TRUE);
SELECT setval('products_id_seq', 60, TRUE);
SELECT setval('product_images_id_seq', 72, TRUE);
