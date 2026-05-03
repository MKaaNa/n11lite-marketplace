import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { addToCart } from '../api/cartApi';
import { getProductBySlug } from '../api/catalogApi';
import { getOrCreateSessionId, getRecommendations, trackProductView } from '../api/recommendationApi';
import { createProductReview, getProductReviews } from '../api/reviewApi';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';

const FALLBACK_IMAGE = 'https://placehold.co/600x600?text=N11Lite';

function formatPrice(price) {
  return new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
  }).format(price);
}

const BADGE_LABELS = {
  NEW: 'Yeni',
  BESTSELLER: 'Çok Satan',
  FEATURED: 'Öne Çıkan',
  DISCOUNTED: 'İndirimli',
  FREE_SHIPPING: 'Ücretsiz Kargo',
};

function formatBadge(badge) {
  return BADGE_LABELS[badge] || badge;
}

function getPriceModel(product) {
  const isDiscounted = product.badge === 'DISCOUNTED';
  if (!isDiscounted) {
    return { isDiscounted: false, currentPrice: product.price, originalPrice: null, discountRate: null };
  }

  const discountRate = 20;
  const originalPrice = Number((product.price / (1 - discountRate / 100)).toFixed(2));
  return { isDiscounted: true, currentPrice: product.price, originalPrice, discountRate };
}

export default function ProductDetailPage() {
  const { slug } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const { showToast } = useToast();
  const [product, setProduct] = useState(null);
  const [selectedImage, setSelectedImage] = useState(FALLBACK_IMAGE);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [cartLoading, setCartLoading] = useState(false);
  const [error, setError] = useState('');

  const [reviewSummary, setReviewSummary] = useState(null);
  const [reviewRating, setReviewRating] = useState(5);
  const [reviewComment, setReviewComment] = useState('');
  const [reviewSubmitting, setReviewSubmitting] = useState(false);
  const [reviewMessage, setReviewMessage] = useState('');
  const [reviewError, setReviewError] = useState('');

  const [recommendations, setRecommendations] = useState([]);
  const [mainImageError, setMainImageError] = useState(false);
  const [selectedVariantId, setSelectedVariantId] = useState('');
  const [isZoomOpen, setIsZoomOpen] = useState(false);

  useEffect(() => {
    async function loadProduct() {
      setLoading(true);
      setError('');

      try {
        const response = await getProductBySlug(slug);
        const productData = response.data;
        const firstImage = productData.images?.[0]?.imageUrl || FALLBACK_IMAGE;

        setProduct(productData);
        setSelectedImage(firstImage);
        setMainImageError(false);
        setSelectedVariantId('');
        setQuantity(1);

        const sessionId = getOrCreateSessionId();
        trackProductView(slug, sessionId).catch(() => {});

        const [reviewsRes, recsRes] = await Promise.allSettled([
          getProductReviews(slug),
          getRecommendations(sessionId, 4, slug),
        ]);

        if (reviewsRes.status === 'fulfilled') {
          setReviewSummary(reviewsRes.value.data);
        }

        if (recsRes.status === 'fulfilled') {
          const recs = recsRes.value.data;
          setRecommendations(Array.isArray(recs) ? recs : []);
        }
      } catch {
        setProduct(null);
        setError('Ürün bulunamadı.');
      } finally {
        setLoading(false);
      }
    }

    loadProduct();
  }, [slug]);

  function decreaseQuantity() {
    setQuantity((currentQuantity) => Math.max(1, currentQuantity - 1));
  }

  function increaseQuantity() {
    setQuantity((currentQuantity) => {
      if (product?.stock) {
        return Math.min(product.stock, currentQuantity + 1);
      }

      return currentQuantity + 1;
    });
  }

  async function addSelectedProductToCart({
    goToCart = false,
    prefillCoupon = null,
    autoApplyCoupon = false,
  } = {}) {
    setError('');

    if (!user) {
      navigate('/login', {
        state: {
          message: 'Sepete ürün eklemek için önce giriş yapmalısınız.',
        },
      });
      return;
    }

    const hasVariants = Boolean(product?.variants?.length);
    if (hasVariants && !selectedVariantId) {
      setError('Bu ürün için beden/numara seçmelisin.');
      return;
    }

    try {
      setCartLoading(true);
      await addToCart(product.id, quantity, selectedVariantId ? Number(selectedVariantId) : null);
      if (prefillCoupon) {
        navigate('/cart', {
          state: {
            prefillCoupon,
            ...(autoApplyCoupon ? { autoApplyCoupon: true } : {}),
          },
        });
        return;
      }
      if (goToCart) {
        navigate('/cart');
        return;
      }
      showToast('Ürün sepete eklendi.', 'success');
    } catch {
      const isRedirect = goToCart || prefillCoupon;
      setError(isRedirect
        ? 'Sepete eklenemedi. Lütfen tekrar dene.'
        : 'Ürün sepete eklenemedi.');
    } finally {
      setCartLoading(false);
    }
  }

  function handleAddToCart() {
    addSelectedProductToCart();
  }

  function handleBuyNow() {
    addSelectedProductToCart({ goToCart: true });
  }

  function handleUseProductCoupon() {
    if (!product?.productCouponCode) {
      return;
    }
    addSelectedProductToCart({
      prefillCoupon: product.productCouponCode,
      autoApplyCoupon: true,
    });
  }

  async function handleReviewSubmit(e) {
    e.preventDefault();
    setReviewMessage('');
    setReviewError('');
    setReviewSubmitting(true);

    try {
      await createProductReview(slug, reviewRating, reviewComment);
      setReviewComment('');
      setReviewRating(5);
      setReviewMessage('Yorumunuz gönderildi.');

      const reviewsRes = await getProductReviews(slug);
      setReviewSummary(reviewsRes.data);
    } catch (err) {
      const msg = err?.response?.data?.message;
      setReviewError(msg || 'Yorum gönderilemedi.');
    } finally {
      setReviewSubmitting(false);
    }
  }

  const images = product?.images?.length ? product.images : [
    { id: 'fallback', imageUrl: FALLBACK_IMAGE, displayOrder: 1 },
  ];
  const priceModel = product ? getPriceModel(product) : null;
  const hasVariants = Boolean(product?.variants?.length);
  const currentStock = hasVariants && selectedVariantId
    ? (product.variants.find((variant) => String(variant.id) === selectedVariantId)?.stock ?? product.stock)
    : product?.stock;
  const showLowStockWarning = Number(currentStock) > 0 && Number(currentStock) <= 3;

  return (
    <main className="catalog-page">
      {!loading && !error && product && (
        <nav className="breadcrumb" aria-label="Breadcrumb">
          <Link to="/products">Anasayfa</Link>
          <span>/</span>
          {product.category?.slug ? (
            <Link to={`/products?category=${product.category.slug}`}>{product.category?.name || 'Kategori'}</Link>
          ) : (
            <span>{product.category?.name || 'Kategori'}</span>
          )}
          <span>/</span>
          <span>{product.name}</span>
        </nav>
      )}
      <Link className="back-link" to="/products">
        Ürünlere dön
      </Link>

      {loading && <div className="alert alert--loading">Ürün yükleniyor...</div>}
      {error && <div className="alert alert--error">{error}</div>}
      {!loading && !error && product && (
        <>
          <section className="product-detail">
            <div className="gallery">
              <div className="main-image-wrap">
                {product.badge && (
                <span className={`product-badge product-badge--${product.badge.toLowerCase()}`}>
                  {formatBadge(product.badge)}
                </span>
              )}
                <img
                  src={mainImageError ? FALLBACK_IMAGE : selectedImage}
                  alt={product.name}
                  className="main-image"
                  onError={() => setMainImageError(true)}
                  onClick={() => setIsZoomOpen(true)}
                />
                <button type="button" className="zoom-trigger" onClick={() => setIsZoomOpen(true)}>
                  Görseli Yakınlaştır
                </button>
              </div>

              <div className="thumbnail-list">
                {images.map((image) => (
                  <button
                    type="button"
                    key={image.id}
                    className={selectedImage === image.imageUrl ? 'thumbnail active' : 'thumbnail'}
                    onClick={() => {
                      setSelectedImage(image.imageUrl);
                      setMainImageError(false);
                    }}
                  >
                    <img
                      src={image.imageUrl}
                      alt={`${product.name} ${image.displayOrder}`}
                      onError={(event) => {
                        event.currentTarget.src = FALLBACK_IMAGE;
                      }}
                    />
                  </button>
                ))}
              </div>
            </div>

            <div className="detail-info">
              <p className="product-category">{product.category?.name}</p>
              <h1>{product.name}</h1>
              <div className="detail-price-box">
                {priceModel?.isDiscounted && (
                  <p className="detail-price-old">{formatPrice(priceModel.originalPrice)}</p>
                )}
                <p className="detail-price">{formatPrice(priceModel?.currentPrice || product.price)}</p>
                {priceModel?.isDiscounted && (
                  <p className="detail-discount-note">%{priceModel.discountRate} indirim fırsatı</p>
                )}
              </div>
              <p className="detail-description">{product.description}</p>
              {product.productCouponCode && (
                <div className="product-coupon-box">
                  <p className="product-coupon-title">Bu ürüne özel kupon</p>
                  <code>{product.productCouponCode}</code>
                  <p className="product-coupon-note">
                    {product.productCouponLabel || 'Bu kupon sadece bu üründe geçerlidir.'}
                  </p>
                  <button
                    type="button"
                    className="secondary-button"
                    onClick={handleUseProductCoupon}
                    disabled={
                      cartLoading
                      || product.stock <= 0
                      || (hasVariants && !selectedVariantId)
                    }
                  >
                    {cartLoading ? 'Sepete ekleniyor...' : 'Kuponu Sepette Kullan'}
                  </button>
                </div>
              )}

              <div className="detail-meta">
                <span>Stok: {currentStock}</span>
                <span>Satılan: {product.soldCount}</span>
                <span>Görüntülenme: {product.viewCount}</span>
              </div>
              {showLowStockWarning && (
                <div className="low-stock-badge">Son {currentStock} ürün!</div>
              )}

              <div className="add-cart-panel">
                {hasVariants && (
                  <label className="product-variant-select">
                    <span>Beden / Numara</span>
                    <select
                      value={selectedVariantId}
                      onChange={(event) => setSelectedVariantId(event.target.value)}
                    >
                      <option value="">Seçiniz</option>
                      {product.variants.map((variant) => (
                        <option key={variant.id} value={variant.id} disabled={variant.stock <= 0}>
                          {variant.variantValue} {variant.stock <= 0 ? '(Tükendi)' : `(${variant.stock} stok)`}
                        </option>
                      ))}
                    </select>
                  </label>
                )}
                <div className="quantity-controls">
                  <button type="button" onClick={decreaseQuantity} disabled={quantity <= 1}>
                    -
                  </button>
                  <span>{quantity}</span>
                  <button
                    type="button"
                    onClick={increaseQuantity}
                    disabled={product.stock && quantity >= product.stock}
                  >
                    +
                  </button>
                </div>
                <button
                  type="button"
                  className="primary-button"
                  onClick={handleAddToCart}
                  disabled={cartLoading || product.stock <= 0 || (hasVariants && !selectedVariantId)}
                >
                  {cartLoading ? 'Ekleniyor...' : 'Sepete Ekle'}
                </button>
                <button
                  type="button"
                  className="buy-now-button"
                  onClick={handleBuyNow}
                  disabled={cartLoading || product.stock <= 0 || (hasVariants && !selectedVariantId)}
                >
                  {cartLoading ? 'Hazırlanıyor...' : 'Hemen Satın Al'}
                </button>
              </div>

              {product.store?.id ? (
                <Link
                  className="store-panel store-panel--link"
                  to={`/stores/${product.store.id}`}
                  aria-label={`${product.store.name} mağaza yorumları ve puanlar`}
                >
                  <p className="store-label">Mağaza</p>
                  <p className="store-name">
                    {product.store.name}
                    {product.store.official && <span className="official-store">Resmi Mağaza</span>}
                  </p>
                  {product.store.rating != null && (
                    <p className="store-rating">
                      <span className="store-rating-star">★</span> Puan: {product.store.rating}
                    </p>
                  )}
                  <p className="store-panel-hint">Yorumları ve mağaza değerlendirmesini görüntüle</p>
                </Link>
              ) : (
                <div className="store-panel">
                  <p className="store-label">Mağaza</p>
                  <p className="store-name">
                    {product.store?.name}
                    {product.store?.official && <span className="official-store">Resmi Mağaza</span>}
                  </p>
                  {product.store?.rating != null && (
                    <p className="store-rating">Puan: {product.store.rating}</p>
                  )}
                </div>
              )}
            </div>
          </section>
          <div className="sticky-add-to-cart-mobile">
            <div>
              <strong>{formatPrice(priceModel?.currentPrice || product.price)}</strong>
              {showLowStockWarning && <span>Son {currentStock} ürün</span>}
            </div>
            <button
              type="button"
              className="primary-button"
              onClick={handleAddToCart}
              disabled={cartLoading || product.stock <= 0 || (hasVariants && !selectedVariantId)}
            >
              {cartLoading ? 'Ekleniyor...' : 'Sepete Ekle'}
            </button>
          </div>

          <section className="reviews-section">
            <h2>Müşteri Yorumları</h2>

            {reviewSummary && reviewSummary.reviewCount > 0 && (
              <div className="review-summary">
                <span className="review-average">
                  Ortalama Puan: {reviewSummary.averageRating} / 5
                </span>
                <span className="review-count">({reviewSummary.reviewCount} yorum)</span>
              </div>
            )}

            {reviewSummary && reviewSummary.reviewCount === 0 && (
              <div className="reviews-empty-row">
                <img
                  className="empty-state-visual empty-state-visual--compact"
                  src="/assets/brand/illus-empty.png"
                  alt=""
                  decoding="async"
                />
                <p className="state-message reviews-empty-message">Henüz yorum yapılmamış.</p>
              </div>
            )}

            {reviewSummary && reviewSummary.reviews.map((review) => (
              <div key={review.id} className="review-item">
                <div className="review-header">
                  <span className="review-author">{review.userFullName}</span>
                  <span className="review-rating">{'★'.repeat(review.rating)}{'☆'.repeat(5 - review.rating)}</span>
                </div>
                <p className="review-comment">{review.comment}</p>
              </div>
            ))}

            <div className="review-form-section">
              {user ? (
                <form onSubmit={handleReviewSubmit} className="review-form">
                  <h3>Yorum Yap</h3>

                  {reviewMessage && <p className="alert alert--success">{reviewMessage}</p>}
                  {reviewError && <p className="alert alert--error">{reviewError}</p>}

                  <label htmlFor="review-rating">
                    Puan
                    <select
                      id="review-rating"
                      value={reviewRating}
                      onChange={(e) => setReviewRating(Number(e.target.value))}
                    >
                      <option value={5}>5 - Mükemmel</option>
                      <option value={4}>4 - İyi</option>
                      <option value={3}>3 - Orta</option>
                      <option value={2}>2 - Kötü</option>
                      <option value={1}>1 - Çok Kötü</option>
                    </select>
                  </label>

                  <label htmlFor="review-comment">
                    Yorumun
                    <textarea
                      id="review-comment"
                      value={reviewComment}
                      onChange={(e) => setReviewComment(e.target.value)}
                      rows={3}
                      required
                    />
                  </label>

                  <button type="submit" className="primary-button" disabled={reviewSubmitting}>
                    {reviewSubmitting ? 'Gönderiliyor...' : 'Yorum Gönder'}
                  </button>
                </form>
              ) : (
                <p className="alert alert--info">
                  Yorum yapmak için giriş yapmalısın.
                </p>
              )}
            </div>
          </section>

          {recommendations.length > 0 && (
            <section className="recommendations-section">
              <h2>Sana Önerilen Ürünler</h2>
              <div className="recommendations-list">
                {recommendations.map((rec) => (
                  <Link key={rec.id} to={`/products/${rec.slug}`} className="recommendation-card">
                    <img
                      src={rec.imageUrl || FALLBACK_IMAGE}
                      alt={rec.name}
                      className="recommendation-image"
                      onError={(event) => {
                        event.currentTarget.src = FALLBACK_IMAGE;
                      }}
                    />
                    <div className="recommendation-body">
                      <p className="recommendation-name">{rec.name}</p>
                      <p className="recommendation-price">{formatPrice(rec.price)}</p>
                      <p className="recommendation-category">{rec.categoryName}</p>
                    </div>
                  </Link>
                ))}
              </div>
            </section>
          )}
          {isZoomOpen && (
            <div className="image-lightbox" role="dialog" aria-modal="true" aria-label={`${product.name} yakınlaştırılmış görsel`}>
              <button type="button" className="image-lightbox-close" onClick={() => setIsZoomOpen(false)}>
                Kapat
              </button>
              <img src={mainImageError ? FALLBACK_IMAGE : selectedImage} alt={product.name} />
            </div>
          )}
        </>
      )}
    </main>
  );
}
