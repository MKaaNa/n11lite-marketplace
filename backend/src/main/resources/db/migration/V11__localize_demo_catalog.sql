UPDATE categories SET name = 'Elektronik' WHERE slug = 'electronics';
UPDATE categories SET name = 'Moda' WHERE slug = 'fashion';
UPDATE categories SET name = 'Ev ve Yaşam' WHERE slug = 'home-living';
UPDATE categories SET name = 'Kitap' WHERE slug = 'books';

UPDATE stores SET description = 'Demo elektronik ürünleri için yerel mağaza.' WHERE id = 1;
UPDATE stores SET description = 'Günlük giyim ürünleri ve aksesuarlar.' WHERE id = 2;
UPDATE stores SET description = 'Ev ve yaşam için kullanışlı ürünler.' WHERE id = 3;
UPDATE stores SET description = 'Günlük okuma için popüler kitaplar.' WHERE id = 4;

UPDATE products SET
    name = 'Kablosuz Bluetooth Kulaklık',
    description = 'Uzun pil ömrüne sahip rahat kablosuz kulaklık.'
WHERE slug = 'wireless-bluetooth-headphones';

UPDATE products SET
    name = 'Akıllı Saat Series 5',
    description = 'Sağlık takibi ve bildirim desteği sunan sade akıllı saat.'
WHERE slug = 'smart-watch-series-5';

UPDATE products SET
    name = 'Taşınabilir Powerbank 20000 mAh',
    description = 'Telefon ve tabletler için yüksek kapasiteli powerbank.'
WHERE slug = 'portable-power-bank-20000-mah';

UPDATE products SET
    name = 'Basic Pamuklu Tişört',
    description = 'Günlük kullanım için yumuşak pamuklu tişört.'
WHERE slug = 'basic-cotton-t-shirt';

UPDATE products SET
    name = 'Slim Fit Kot Pantolon',
    description = 'Klasik stile sahip rahat slim fit kot pantolon.'
WHERE slug = 'slim-fit-denim-jeans';

UPDATE products SET
    name = 'Günlük Sneaker Ayakkabı',
    description = 'Günlük kombinler için hafif sneaker ayakkabı.'
WHERE slug = 'casual-sneaker-shoes';

UPDATE products SET
    name = 'Seramik Yemek Takımı',
    description = 'Aile sofraları için modern seramik yemek takımı.'
WHERE slug = 'ceramic-dinner-set';

UPDATE products SET
    name = 'Pamuklu Nevresim Takımı',
    description = 'Çift kişilik yataklar için rahat nevresim takımı.'
WHERE slug = 'soft-cotton-bed-sheet-set';

UPDATE products SET
    name = 'Ahşap Masa Düzenleyici',
    description = 'Daha düzenli bir çalışma alanı için ahşap masa düzenleyici.'
WHERE slug = 'desk-organizer-wooden';

UPDATE products SET
    name = 'Clean Code Temelleri',
    description = 'Yazılım geliştirmeye yeni başlayanlar için sade bir kitap.'
WHERE slug = 'clean-code-basics';

UPDATE products SET
    name = 'Modern Java Rehberi',
    description = 'Backend geliştiriciler için pratik Java rehberi.'
WHERE slug = 'modern-java-guide';

UPDATE products SET
    name = 'Spring Boot Başlangıç Kitabı',
    description = 'Örneklerle anlatılan sade Spring Boot başlangıç kitabı.'
WHERE slug = 'spring-boot-starter-handbook';

UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/headphones?lock=101' WHERE id = 1;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/headphones?lock=102' WHERE id = 2;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/smartwatch?lock=201' WHERE id = 3;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/watch?lock=202' WHERE id = 4;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/powerbank?lock=301' WHERE id = 5;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/charger?lock=302' WHERE id = 6;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/tshirt?lock=401' WHERE id = 7;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/tshirt?lock=402' WHERE id = 8;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/jeans?lock=501' WHERE id = 9;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/denim?lock=502' WHERE id = 10;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/sneakers?lock=601' WHERE id = 11;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/shoes?lock=602' WHERE id = 12;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/dinnerware?lock=701' WHERE id = 13;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/tableware?lock=702' WHERE id = 14;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/bedding?lock=801' WHERE id = 15;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/bedroom?lock=802' WHERE id = 16;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/desk-organizer?lock=901' WHERE id = 17;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/desk?lock=902' WHERE id = 18;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/book?lock=1001' WHERE id = 19;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/books?lock=1002' WHERE id = 20;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/java,book?lock=1101' WHERE id = 21;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/programming,book?lock=1102' WHERE id = 22;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/spring,book?lock=1201' WHERE id = 23;
UPDATE product_images SET image_url = 'https://loremflickr.com/600/600/software,book?lock=1202' WHERE id = 24;
