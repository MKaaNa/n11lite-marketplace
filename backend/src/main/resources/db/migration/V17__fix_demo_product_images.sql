-- V17: Demo catalog – fix primary product images (display_order = 1) per image audit.
-- Unsplash URLs, generic product/stock style; no Java/frontend changes.

-- Moda
UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1599901860904-17e6ed7083a0?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'high-waist-leggings')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1595341888016-a392ef81b7de?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'casual-sneaker-shoes')
  AND display_order = 1;

-- Elektronik
UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1513193643083-07325d25a4b0?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = '4k-action-camera')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1603674554159-b62f6febbce5?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'wireless-charging-stand')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1547052178-7f2c5a20c332?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'bluetooth-speaker-mini')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1520092352425-9699926a9b0b?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'mechanical-keyboard-tkl')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1581725645226-92ad3b4c16d8?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'portable-ssd-1tb')
  AND display_order = 1;

-- Kitap
UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1604866830893-c13cafa515d5?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'domain-driven-design-notes')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1535905557558-afc4877a26fc?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'sql-performance-playbook')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'spring-boot-starter-handbook')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1513185041617-8ab03f83d6c5?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'pragmatic-devops-guide')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'api-design-best-practices')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1610116306796-6fea9f4fae38?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'modern-java-guide')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1517770413964-df8ca61194a6?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'design-patterns-quick-guide')
  AND display_order = 1;

-- Ev ve Yaşam
UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1585314293845-4db3b9d0c6e9?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'foldable-laundry-basket')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1677761640321-b80251be00ca?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'ceramic-vase-duo-set')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1697294872375-9942fd94d2a7?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'glass-spice-jar-set')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1590251024078-8a6d9f90b02d?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'decorative-cushion-cover-set')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.unsplash.com/photo-1583315388850-347d1e737643?auto=format&fit=crop&w=1200&q=80'
WHERE product_id = (SELECT id FROM products WHERE slug = 'kitchen-organizer-shelf')
  AND display_order = 1;
