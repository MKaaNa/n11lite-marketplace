-- Demo yorumlar: tüm ürünlere (dolayısıyla tüm mağazalara) ürünle alakalı Türkçe yorumlar.
-- Kullanıcı dağılımı: user_id = ((product_id - 1) % 7) + 1 → admin + 6 demo kullanıcı döngüsel.
-- Şifre hash'i (demo kullanıcılar): bcrypt "password" — sadece seed.
INSERT INTO users (email, password_hash, full_name, phone, role, created_at)
SELECT * FROM (VALUES
    ('ayse.demo@n11lite.local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Ayşe Yılmaz', '+905551111101', 'USER', NOW()),
    ('mehmet.demo@n11lite.local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Mehmet Kaya', '+905551111102', 'USER', NOW()),
    ('zeynep.demo@n11lite.local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Zeynep Demir', '+905551111103', 'USER', NOW()),
    ('can.demo@n11lite.local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Can Öztürk', '+905551111104', 'USER', NOW()),
    ('elif.demo@n11lite.local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Elif Şahin', '+905551111105', 'USER', NOW()),
    ('burak.demo@n11lite.local', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Burak Aydın', '+905551111106', 'USER', NOW())
) AS v(email, password_hash, full_name, phone, role, created_at)
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.email = v.email);

-- TechStore (1): elektronik odaklı yorumlar
INSERT INTO reviews (user_id, product_id, rating, comment, created_at, updated_at) VALUES
    (1, 1, 5, 'Kablosuz bağlantı stabil, müzik dinlerken gecikme hissetmedim. Batarya günlük kullanımda yeterli geldi.', NOW() - INTERVAL '40 days', NOW() - INTERVAL '40 days'),
    (2, 2, 4, 'Sağlık ve bildirim özellikleri iş görüyor; telefon eşleşmesi ara sıra yeniden istiyor.', NOW() - INTERVAL '39 days', NOW() - INTERVAL '39 days'),
    (3, 3, 5, 'Kapasite iddia edilenle uyumlu, hızlı şarj portu gerçekten zaman kazandırıyor.', NOW() - INTERVAL '38 days', NOW() - INTERVAL '38 days'),
    (4, 13, 5, 'Gürültü engelleme ofiste fark edilir şekilde işe yarıyor, uzun süre takınca da kulak yormuyor.', NOW() - INTERVAL '37 days', NOW() - INTERVAL '37 days'),
    (5, 17, 5, 'Küçük gövdesine göre sesi temiz; masaüstünde film izlemek için ideal.', NOW() - INTERVAL '36 days', NOW() - INTERVAL '36 days'),
    (6, 21, 5, 'QHD çözünürlük metin ve tasarım işlerinde gözü yormuyor, renkler dengeli.', NOW() - INTERVAL '35 days', NOW() - INTERVAL '35 days'),
    (7, 25, 4, 'DDD kavramlarını özetleyen bölümler iş hayatındaki örneklerle daha da güçlense süper olurdu; yine de faydalı.', NOW() - INTERVAL '34 days', NOW() - INTERVAL '34 days'),
    (1, 29, 5, 'Sorgu planı ve indeks ipuçları doğrudan projeme uyguladım, fark hemen hissedildi.', NOW() - INTERVAL '33 days', NOW() - INTERVAL '33 days'),
    (2, 33, 5, 'Bambu yüzey düzgün, keserken kaymıyor; set fiyatına göre kaliteli.', NOW() - INTERVAL '32 days', NOW() - INTERVAL '32 days'),
    (3, 37, 4, 'Kapaklar sıkı kapanıyor, tezgah üstünde düzenli duruyor; cam parçalar dikkat ister.', NOW() - INTERVAL '31 days', NOW() - INTERVAL '31 days'),
    (4, 41, 5, 'Katlanınca dolaba giriyor, hafif ve taşıması kolay; çamaşır odası için pratik.', NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days'),
    (5, 45, 5, 'Yüksek bel gerçekten toparlıyor, spor sırasında kaymıyor; kumaş nefes alıyor.', NOW() - INTERVAL '29 days', NOW() - INTERVAL '29 days'),
    (6, 49, 4, 'Triko yumuşak, kaşınma yapmadı; mevsim geçişi için tam istediğim kalınlıkta.', NOW() - INTERVAL '28 days', NOW() - INTERVAL '28 days');

-- ModaPlus (2): giyim ve seçili elektronik / ev eşyası (ModaPlus ürün gamı)
INSERT INTO reviews (user_id, product_id, rating, comment, created_at, updated_at) VALUES
    (5, 4, 5, 'Pamuk dokusu günlük kullanımda çok rahat, yıkamadan sonra da formunu korudu.', NOW() - INTERVAL '27 days', NOW() - INTERVAL '27 days'),
    (6, 5, 4, 'Kot kalıbı slim ama bacakta fazla sıkmıyor; renk ilk yıkamada hafif açıldı.', NOW() - INTERVAL '26 days', NOW() - INTERVAL '26 days'),
    (7, 6, 5, 'Hafif taban, günlük yürüyüşte konforlu; numara tarifine uygun geldi.', NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days'),
    (1, 14, 5, '4K çekim netliği tatilde çok işime yaradı, montaj aksesuarları kutuda eksiksizdi.', NOW() - INTERVAL '24 days', NOW() - INTERVAL '24 days'),
    (2, 18, 4, 'Uygulama kurulumu kolay, zamanlayıcı işlevini sık kullanıyorum; sinyal zayıf odada ara ara kopuyor.', NOW() - INTERVAL '23 days', NOW() - INTERVAL '23 days'),
    (3, 22, 5, 'Dosya aktarım hızı beklentimin üstünde, kasa sağlam görünüyor.', NOW() - INTERVAL '22 days', NOW() - INTERVAL '22 days'),
    (4, 26, 5, 'Mikroservis hataları ve devre kesici anlatımı özellikle işe yarar.', NOW() - INTERVAL '21 days', NOW() - INTERVAL '21 days'),
    (5, 30, 5, 'Versiyonlama ve hata gövdeleri bölümü API ekibimizle hemen paylaştık.', NOW() - INTERVAL '20 days', NOW() - INTERVAL '20 days'),
    (6, 34, 5, 'İki katman dolap içinde iş çıkarıyor, kapaklar düzgün kapanıyor.', NOW() - INTERVAL '19 days', NOW() - INTERVAL '19 days'),
    (7, 38, 5, 'Kırlent kılıfları kumaşı modern görünüyor, fermuarlar sorunsuz.', NOW() - INTERVAL '18 days', NOW() - INTERVAL '18 days'),
    (1, 42, 4, 'Tezgah üstü düzeni toparladı; montaj için kısa bir video olsa iyi olurdu.', NOW() - INTERVAL '17 days', NOW() - INTERVAL '17 days'),
    (2, 46, 5, 'Omuz askısı rahat, laptop ve defter için yeterli hacim; kanvas dayanıklı.', NOW() - INTERVAL '16 days', NOW() - INTERVAL '16 days'),
    (3, 50, 5, 'Hafif yağmurda suyu üzerinde tuttu, katlanınca çantaya sığdı.', NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days');

-- HomeLife (3): ev, yaşam, mutfak; teknik kitaplar (HomeLife kataloğundaki yazılım kitapları)
INSERT INTO reviews (user_id, product_id, rating, comment, created_at, updated_at) VALUES
    (3, 7, 5, 'Seramik parlaklığı şık, tabaklar bulaşık makinesinde çizilmedi.', NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days'),
    (4, 8, 4, 'Çarşaf pamuklu hissi güzel; ilk yıkamada çok hafif çekme oldu, ütüyle düzeldi.', NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days'),
    (5, 9, 5, 'Masa üstü düzeni için ideal, ahşap yüzey düzgün zımparalanmış.', NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'),
    (6, 15, 5, 'Telefonu koyar koymaz şarj başlıyor, gece başucunda kullanıyorum.', NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days'),
    (7, 19, 4, 'Tuş sesi net, TKL düzeni masada yer kazandırdı; tuş kapları biraz parlak.', NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
    (1, 23, 5, 'Clean Architecture örnekleri gerçek senaryoya yakın, takım içi okuma yaptık.', NOW() - INTERVAL '9 days', NOW() - INTERVAL '9 days'),
    (2, 27, 5, 'Tasarım desenlerini hızlı tekrar için tam aradığım sade anlatım.', NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days'),
    (3, 31, 5, 'Takım içi test standardı bölümü doğrudan checklist olarak kullanıldı.', NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'),
    (4, 35, 5, 'Işık sıcaklığı göz yormuyor, gövde minimal ve stabil.', NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days'),
    (5, 39, 5, 'Vazolar salon rafına tam uydu, seramik işçiliği düzgün.', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
    (6, 43, 4, 'Hoodie günlük kullanım için ideal; tam kışlık değil ama katmanlamaya uygun.', NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
    (7, 47, 5, 'Antrenmanda teri iyi yönetiyor, bel lastiği sıkmıyor.', NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
    (1, 51, 5, 'Beşli paket fiyatına göre mantıklı, pamuk oranı yüksek hissediliyor.', NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days');

-- BookNest (4): kitaplar + mağazanın sattığı aksesuar ürünleri
INSERT INTO reviews (user_id, product_id, rating, comment, created_at, updated_at) VALUES
    (4, 10, 5, 'Yazılım prensiplerini sade dille özetliyor; kariyer başı için temel kaynak.', NOW() - INTERVAL '41 days', NOW() - INTERVAL '41 days'),
    (5, 11, 5, 'Java sözdizimi ve koleksiyonlar bölümü ders notlarımda referans oldu.', NOW() - INTERVAL '42 days', NOW() - INTERVAL '42 days'),
    (6, 12, 5, 'Örnek proje adım adım ilerliyor, Spring Boot’a ilk giriş için çok uygun.', NOW() - INTERVAL '43 days', NOW() - INTERVAL '43 days'),
    (7, 16, 5, 'Mac ile monitör ve hub tek kabloda toplandı; ısınma kontrol altında.', NOW() - INTERVAL '44 days', NOW() - INTERVAL '44 days'),
    (1, 20, 5, 'El ergonomisi iyi, uzun süre kod yazarken bile rahatsız etmedi.', NOW() - INTERVAL '45 days', NOW() - INTERVAL '45 days'),
    (2, 24, 5, 'Kokular ve tekrarlanan yapılar bölümü kod okumayı kolaylaştırdı.', NOW() - INTERVAL '46 days', NOW() - INTERVAL '46 days'),
    (3, 28, 5, 'JWT ve form login örnekleri güncel Spring sürümüyle uyumlu anlatılmış.', NOW() - INTERVAL '47 days', NOW() - INTERVAL '47 days'),
    (4, 32, 4, 'CI/CD bölümü özet ama yön gösterici; derinlemesine için ek kaynak gerekir.', NOW() - INTERVAL '48 days', NOW() - INTERVAL '48 days'),
    (5, 36, 5, 'Kahvaltı sunumları için kaymaz yüzey gerçekten işe yarıyor, temizliği kolay.', NOW() - INTERVAL '49 days', NOW() - INTERVAL '49 days'),
    (6, 40, 5, 'Havlular emici ve yumuşak; banyo seti olarak komple yeterli.', NOW() - INTERVAL '50 days', NOW() - INTERVAL '50 days'),
    (7, 44, 4, 'Oversize kesim fotoğraftaki gibi; kumaş hafif, ütü istiyor.', NOW() - INTERVAL '51 days', NOW() - INTERVAL '51 days'),
    (1, 48, 5, 'Ayar tokası sağlam, günlük kombinle sade duruyor.', NOW() - INTERVAL '52 days', NOW() - INTERVAL '52 days'),
    (2, 52, 5, 'İnce cüzdana sığdı, kartlar çıkarken takılmıyor; deri kokusu hoş.', NOW() - INTERVAL '53 days', NOW() - INTERVAL '53 days');

SELECT setval(
    'users_id_seq',
    (SELECT COALESCE(MAX(id), 1) FROM users),
    TRUE);
SELECT setval(
    'reviews_id_seq',
    (SELECT COALESCE(MAX(id), 1) FROM reviews),
    TRUE);
