-- User-provided image replacements for problematic demo products

UPDATE product_images
SET image_url = 'https://m.media-amazon.com/images/I/61lXAVv-kgL.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'double-layer-storage-box');

UPDATE product_images
SET image_url = 'https://static.ticimax.cloud/14356/uploads/urunresimleri/buyuk/adidas-kadin-gunluk-sapka-cap-it7365-2f-f41.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'daily-cap');

UPDATE product_images
SET image_url = 'https://static.ticimax.cloud/52007/uploads/urunresimleri/buyuk/5li-paket-erkek-soket-corap-cok-renkli-d99-ff.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'daily-socks-5-pack');

UPDATE product_images
SET image_url = 'https://static.ticimax.cloud/59684/uploads/urunresimleri/buyuk/dikisli-yuksek-bel-tayt-siyah-tayt-mod-40441-.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'high-waist-leggings');

UPDATE product_images
SET image_url = 'https://www.arifinhediyelik.com.tr/masaustu-agac-kitaplik-ahsap-masaustu-duzenleyici-serhendi-logolu-nida-m8-kitaplik-serhendi-logolu-urunler-2255-55-B.webp'
WHERE product_id = (SELECT id FROM products WHERE slug = 'desk-organizer-wooden');

UPDATE product_images
SET image_url = 'https://image-ikea.mncdn.com/urunler/500_500/PE921270.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'smart-home-plug');

UPDATE product_images
SET image_url = 'https://m.media-amazon.com/images/I/71DmkwHR-VL._AC_UF894,1000_QL80_.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'clean-architecture-in-practice');

UPDATE product_images
SET image_url = 'https://i.ebayimg.com/images/g/ef0AAOSw0eBjDPxy/s-l400.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'refactoring-essentials');
