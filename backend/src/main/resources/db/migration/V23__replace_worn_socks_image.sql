-- Replace worn-looking socks image with product-only flat lay.
UPDATE product_images
SET image_url = 'https://source.unsplash.com/YHmZBuNCLvQ/1200x1200'
WHERE product_id = 51;
