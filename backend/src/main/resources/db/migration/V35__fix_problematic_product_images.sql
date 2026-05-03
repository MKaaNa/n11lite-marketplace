-- Replace problematic demo images with approved references

UPDATE product_images
SET image_url = 'https://www.tesbihcibaba.com.tr/mandarin-full-grain-fermuarli-erkek-deri-kartlik-12618-455181-12-B.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'leather-card-holder')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://cdn.dsmcdn.com/ty1321/product/media/images/prod/QC/20240516/10/4603277f-6313-3cc6-bb38-c689f76261ae/1_org_zoom.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'usb-c-hub-8-in-1')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.manning.com/book/b/dc43dfc-e43d-419d-b577-3809c6967442/Richardson-MP-HI.png'
WHERE product_id = (SELECT id FROM products WHERE slug = 'microservices-patterns-handbook')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://images.manning.com/book/0/b4c8c91-f360-4fb3-bbec-13ab3dd26d68/Lauret-DWAPIs-HI.png'
WHERE product_id = (SELECT id FROM products WHERE slug = 'api-design-best-practices')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://m.media-amazon.com/images/I/81mZJiObMSL._AC_UF1000,1000_QL80_.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'sql-performance-playbook')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://m.media-amazon.com/images/I/61IPaca1ELL._AC_UF1000,1000_QL80_.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'spring-security-fundamentals')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://miro.medium.com/v2/resize:fit:1400/1*peTjtB1Zx6pTnQOGtUU3gg.jpeg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'domain-driven-design-notes')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://cdn.waterstones.com/bookjackets/large/9780/3211/9780321125217.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'design-patterns-quick-guide')
  AND display_order = 1;

UPDATE product_images
SET image_url = 'https://m.media-amazon.com/images/I/61gGR4uuT-L._AC_UF1000,1000_QL80_.jpg'
WHERE product_id = (SELECT id FROM products WHERE slug = 'pragmatic-devops-guide')
  AND display_order = 1;
