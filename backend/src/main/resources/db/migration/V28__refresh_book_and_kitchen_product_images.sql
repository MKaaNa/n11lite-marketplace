-- User-requested image refresh for specific products

UPDATE product_images
SET image_url = 'https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcTCrMzK0YYmqxxXUtjoZfxNIMVJrZmgqXrKWcR_hVNE3Fezvpj5XtaRRFDOdy1f2LTqBjOHnMhLYXbX8DokO9aAZXHJdTUcF6A7yXFmyLrYutE4atqU6QpuRw'
WHERE product_id = (SELECT id FROM products WHERE slug = 'unit-testing-for-teams');

UPDATE product_images
SET image_url = 'https://static.ticimax.cloud/cdn-cgi/image/width=0,quality=0/4916/uploads/urunresimleri/buyuk/arcoform-servis-tepsisi-kaymaz-ceviz-a-3aad-d.jpeg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'wooden-serving-tray');

UPDATE product_images
SET image_url = 'https://static.ticimax.cloud/cdn-cgi/image/width=-,quality=99/48333/uploads/urunresimleri/buyuk/perotti-bambu-3-lu-kesme-tahtasi-seti-9d-41f.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'bamboo-cutting-board-set');
