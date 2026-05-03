-- Replace generic template comments in books category with product-specific review texts.

UPDATE reviews r
SET comment = CASE p.slug
    WHEN 'clean-architecture-in-practice' THEN 'Katmanlar arasi bagimlilik anlatimi temiz; ekipte refactor planina dogrudan katkisi oldu.'
    WHEN 'clean-code-basics' THEN 'Naming ve method boyutu kisimlari yeni baslayanlar icin cok net, tekrar okunasi bir kaynak.'
    WHEN 'design-patterns-quick-guide' THEN 'Factory ve Strategy ozetleri karar verirken hiz kazandirdi; pratikte karsiligi var.'
    WHEN 'domain-driven-design-notes' THEN 'Bounded context ornekleri is alanini parcalarken bakis acisi kazandirdi.'
    WHEN 'microservices-patterns-handbook' THEN 'Saga ve circuit breaker bolumleri daginik servis akisini toparlamada faydali oldu.'
    WHEN 'modern-java-guide' THEN 'Stream API ve Optional kisimlari guncel Java kodu yazarken cok is goruyor.'
    WHEN 'pragmatic-devops-guide' THEN 'Pipeline ve deployment checklist bolumleri sunum oncesi kontrol listesine donustu.'
    WHEN 'refactoring-essentials' THEN 'Code smell -> refactor adimlarini net bagliyor; eski modulleri sadeleştirmede yardimci oldu.'
    WHEN 'spring-boot-starter-handbook' THEN 'Starter yapisi ve konfigurasyon sirasi net; yeni proje acilisinda hata payini azaltiyor.'
    WHEN 'spring-security-fundamentals' THEN 'Authentication flow siralamasi ve token yonetimi bolumu uygulamada dogrudan kullanildi.'
    WHEN 'sql-performance-playbook' THEN 'Execution plan okuma mantigi net; yavas sorgularin nedenini bulmak kolaylasti.'
    WHEN 'unit-testing-for-teams' THEN 'Test piramidi ve naming ornekleri ekipte ortak test dili olusturmaya yardim etti.'
    WHEN 'api-design-best-practices' THEN 'HTTP durum kodlari ve hata kontrati bolumu API tutarliligini ciddi sekilde iyilestirdi.'
    ELSE r.comment
END,
updated_at = NOW()
FROM products p
JOIN categories c ON c.id = p.category_id
WHERE r.product_id = p.id
  AND c.slug = 'books'
  AND r.comment = 'Fiyat/performans dengesi iyi, günlük kullanımda beklentiyi karşılıyor.';

UPDATE reviews r
SET comment = CASE p.slug
    WHEN 'clean-architecture-in-practice' THEN 'Bolumler akici, fakat daha fazla buyuk olcekli ornek senaryo eklenirse degeri artar.'
    WHEN 'clean-code-basics' THEN 'Temel anlatim iyi; legacy kod donusumu icin daha kapsamli once/sonra ornekleri eklenebilir.'
    WHEN 'design-patterns-quick-guide' THEN 'Kisa ozet iyi, UML benzeri gorsel diyagramlar biraz daha fazla olsa super olurdu.'
    WHEN 'domain-driven-design-notes' THEN 'Kavramsal kisim guclu; aggregate sinirlari icin daha fazla gercek vaka iyi olur.'
    WHEN 'microservices-patterns-handbook' THEN 'Icerik dengeli; event-driven entegrasyon ornekleri biraz daha detaylanabilir.'
    WHEN 'modern-java-guide' THEN 'Anlatim temiz, performans karsilastirmali benchmark bolumu eklenirse daha da guclenir.'
    WHEN 'pragmatic-devops-guide' THEN 'Pratik yonu guclu; gozlemleme metrikleri icin daha fazla dashboard ornegi beklenirdi.'
    WHEN 'refactoring-essentials' THEN 'Temel cerceve iyi; buyuk sinif bolme stratejileri daha detayli verilebilir.'
    WHEN 'spring-boot-starter-handbook' THEN 'Baslangic icin ideal; production profile ve tuning tarafi biraz daha derin olabilir.'
    WHEN 'spring-security-fundamentals' THEN 'Kimlik dogrulama net; yetki modeli kisimlari icin daha cok domain ornegi faydali olur.'
    WHEN 'sql-performance-playbook' THEN 'Guclu bir giris; farkli veritabani motorlarinda karsilastirma tablolari eklenebilir.'
    WHEN 'unit-testing-for-teams' THEN 'Takim standardi anlatimi iyi; flaky test tespiti icin daha fazla ornek beklenir.'
    WHEN 'api-design-best-practices' THEN 'Genel kalite yuksek; backward compatibility stratejileri biraz daha uzun islenebilir.'
    ELSE r.comment
END,
updated_at = NOW()
FROM products p
JOIN categories c ON c.id = p.category_id
WHERE r.product_id = p.id
  AND c.slug = 'books'
  AND r.comment = 'Genel kalite tatmin edici, kargo ve paketleme sorunsuz ulaştı.';

UPDATE reviews r
SET comment = CASE p.slug
    WHEN 'clean-architecture-in-practice' THEN 'Temel kurgu iyi, ancak daha fazla enterprise olcekli vaka ile desteklenirse daha etkili olur.'
    WHEN 'clean-code-basics' THEN 'Anlatim sade; ileri seviye kod kokulari icin ek bolum olmasi daha faydali olurdu.'
    WHEN 'design-patterns-quick-guide' THEN 'Konuya giris icin yeterli, ancak pattern seciminde trade-off karsilastirmasi artabilir.'
    WHEN 'domain-driven-design-notes' THEN 'DDD temeli icin iyi bir baslangic; event storming ornekleri arttirilabilir.'
    WHEN 'microservices-patterns-handbook' THEN 'Icerik faydali; dagitik transaction bolumlerinde daha fazla adim adim senaryo beklenirdi.'
    WHEN 'modern-java-guide' THEN 'Genel olarak iyi; JVM tuning ve memory analizi tarafina kisa bir bolum eklenebilir.'
    WHEN 'pragmatic-devops-guide' THEN 'Cok pratik bir rehber; incident response akisina dair daha fazla ornek iyi olurdu.'
    WHEN 'refactoring-essentials' THEN 'Ana fikirler net, ancak kod donusumlerinde performans etkisi daha detayli islenebilir.'
    WHEN 'spring-boot-starter-handbook' THEN 'Boot tarafi iyi; guvenlik ve gozlemlenebilirlik entegrasyonu daha ayrintili anlatilabilir.'
    WHEN 'spring-security-fundamentals' THEN 'Konu toplu ve faydali; OAuth akislarinda daha fazla uctan uca ornek beklenirdi.'
    WHEN 'sql-performance-playbook' THEN 'Fayda sagliyor; sorgu optimizasyonunda gercek dunya vaka sayisi artabilir.'
    WHEN 'unit-testing-for-teams' THEN 'Takim ici rehberlik iyi; mock stratejileri ve test veri yonetimi kismina ek yapilabilir.'
    WHEN 'api-design-best-practices' THEN 'Icerik yararli; idempotency ve retry senaryolari daha fazla ornekle gelirse daha guclu olur.'
    ELSE r.comment
END,
updated_at = NOW()
FROM products p
JOIN categories c ON c.id = p.category_id
WHERE r.product_id = p.id
  AND c.slug = 'books'
  AND r.comment = 'Ürün iş görüyor, açıklama kısmına birkaç ek detay daha faydalı olurdu.';
