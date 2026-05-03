-- source.unsplash.com yerine doğrudan images.unsplash.com URL kullan.
UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1586350977771-b3b0abd50c82?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = 51;
