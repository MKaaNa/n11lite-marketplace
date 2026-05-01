INSERT INTO categories (id, name, slug, active, created_at) VALUES
    (1, 'Electronics', 'electronics', TRUE, NOW()),
    (2, 'Fashion', 'fashion', TRUE, NOW()),
    (3, 'Home & Living', 'home-living', TRUE, NOW()),
    (4, 'Books', 'books', TRUE, NOW());

INSERT INTO stores (id, name, description, logo_url, rating, official, active, created_at) VALUES
    (1, 'TechStore', 'Simple electronics store for demo products.', 'https://placehold.co/200x200?text=TechStore', 4.70, TRUE, TRUE, NOW()),
    (2, 'ModaPlus', 'Daily fashion products and accessories.', 'https://placehold.co/200x200?text=ModaPlus', 4.40, FALSE, TRUE, NOW()),
    (3, 'HomeLife', 'Useful home and living products.', 'https://placehold.co/200x200?text=HomeLife', 4.60, FALSE, TRUE, NOW()),
    (4, 'BookNest', 'Popular books for everyday reading.', 'https://placehold.co/200x200?text=BookNest', 4.80, TRUE, TRUE, NOW());

INSERT INTO products (
    id, name, slug, description, price, stock, sold_count, view_count, badge,
    category_id, store_id, active, created_at
) VALUES
    (1, 'Wireless Bluetooth Headphones', 'wireless-bluetooth-headphones', 'Comfortable wireless headphones with long battery life.', 1299.90, 45, 180, 950, 'BESTSELLER', 1, 1, TRUE, NOW()),
    (2, 'Smart Watch Series 5', 'smart-watch-series-5', 'Simple smart watch with health tracking and notifications.', 2199.00, 32, 96, 740, 'FEATURED', 1, 1, TRUE, NOW()),
    (3, 'Portable Power Bank 20000 mAh', 'portable-power-bank-20000-mah', 'High capacity power bank for phones and tablets.', 799.50, 80, 230, 1100, 'DISCOUNTED', 1, 1, TRUE, NOW()),
    (4, 'Basic Cotton T-Shirt', 'basic-cotton-t-shirt', 'Soft cotton t-shirt for daily use.', 249.90, 120, 340, 880, 'BESTSELLER', 2, 2, TRUE, NOW()),
    (5, 'Slim Fit Denim Jeans', 'slim-fit-denim-jeans', 'Comfortable slim fit jeans with classic style.', 899.00, 55, 140, 620, NULL, 2, 2, TRUE, NOW()),
    (6, 'Casual Sneaker Shoes', 'casual-sneaker-shoes', 'Lightweight sneakers for everyday outfits.', 1499.90, 38, 115, 700, 'NEW', 2, 2, TRUE, NOW()),
    (7, 'Ceramic Dinner Set', 'ceramic-dinner-set', 'Modern ceramic dinner set for family tables.', 1199.90, 24, 75, 430, 'FREE_SHIPPING', 3, 3, TRUE, NOW()),
    (8, 'Soft Cotton Bed Sheet Set', 'soft-cotton-bed-sheet-set', 'Comfortable bed sheet set for double beds.', 699.90, 60, 190, 520, 'DISCOUNTED', 3, 3, TRUE, NOW()),
    (9, 'Desk Organizer Wooden', 'desk-organizer-wooden', 'Wooden desk organizer for a clean workspace.', 349.50, 70, 88, 360, NULL, 3, 3, TRUE, NOW()),
    (10, 'Clean Code Basics', 'clean-code-basics', 'Beginner friendly software development book.', 399.90, 40, 210, 980, 'FEATURED', 4, 4, TRUE, NOW()),
    (11, 'Modern Java Guide', 'modern-java-guide', 'Practical Java guide for backend developers.', 459.90, 35, 160, 820, 'NEW', 4, 4, TRUE, NOW()),
    (12, 'Spring Boot Starter Handbook', 'spring-boot-starter-handbook', 'Simple Spring Boot handbook with examples.', 499.90, 28, 130, 760, 'BESTSELLER', 4, 4, TRUE, NOW());

INSERT INTO product_images (id, product_id, image_url, display_order) VALUES
    (1, 1, 'https://placehold.co/600x600?text=Headphones+1', 1),
    (2, 1, 'https://placehold.co/600x600?text=Headphones+2', 2),
    (3, 2, 'https://placehold.co/600x600?text=Smart+Watch+1', 1),
    (4, 2, 'https://placehold.co/600x600?text=Smart+Watch+2', 2),
    (5, 3, 'https://placehold.co/600x600?text=Power+Bank+1', 1),
    (6, 3, 'https://placehold.co/600x600?text=Power+Bank+2', 2),
    (7, 4, 'https://placehold.co/600x600?text=T-Shirt+1', 1),
    (8, 4, 'https://placehold.co/600x600?text=T-Shirt+2', 2),
    (9, 5, 'https://placehold.co/600x600?text=Jeans+1', 1),
    (10, 5, 'https://placehold.co/600x600?text=Jeans+2', 2),
    (11, 6, 'https://placehold.co/600x600?text=Sneaker+1', 1),
    (12, 6, 'https://placehold.co/600x600?text=Sneaker+2', 2),
    (13, 7, 'https://placehold.co/600x600?text=Dinner+Set+1', 1),
    (14, 7, 'https://placehold.co/600x600?text=Dinner+Set+2', 2),
    (15, 8, 'https://placehold.co/600x600?text=Bed+Sheet+1', 1),
    (16, 8, 'https://placehold.co/600x600?text=Bed+Sheet+2', 2),
    (17, 9, 'https://placehold.co/600x600?text=Organizer+1', 1),
    (18, 9, 'https://placehold.co/600x600?text=Organizer+2', 2),
    (19, 10, 'https://placehold.co/600x600?text=Clean+Code+1', 1),
    (20, 10, 'https://placehold.co/600x600?text=Clean+Code+2', 2),
    (21, 11, 'https://placehold.co/600x600?text=Java+Guide+1', 1),
    (22, 11, 'https://placehold.co/600x600?text=Java+Guide+2', 2),
    (23, 12, 'https://placehold.co/600x600?text=Spring+Boot+1', 1),
    (24, 12, 'https://placehold.co/600x600?text=Spring+Boot+2', 2);

SELECT setval('categories_id_seq', 4, TRUE);
SELECT setval('stores_id_seq', 4, TRUE);
SELECT setval('products_id_seq', 12, TRUE);
SELECT setval('product_images_id_seq', 24, TRUE);
