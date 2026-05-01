import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { addToCart } from '../api/cartApi';
import { getProductBySlug } from '../api/catalogApi';
import { getOrCreateSessionId, getRecommendations, trackProductView } from '../api/recommendationApi';
import { createProductReview, getProductReviews } from '../api/reviewApi';
import { useAuth } from '../context/AuthContext';

const FALLBACK_IMAGE = 'https://placehold.co/600x600?text=N11Lite';

function formatPrice(price) {
  return new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
  }).format(price);
}

export default function ProductDetailPage() {
  const { slug } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [product, setProduct] = useState(null);
  const [selectedImage, setSelectedImage] = useState(FALLBACK_IMAGE);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [cartLoading, setCartLoading] = useState(false);
  const [error, setError] = useState('');
  const [cartMessage, setCartMessage] = useState('');

  const [reviewSummary, setReviewSummary] = useState(null);
  const [reviewRating, setReviewRating] = useState(5);
  const [reviewComment, setReviewComment] = useState('');
  const [reviewSubmitting, setReviewSubmitting] = useState(false);
  const [reviewMessage, setReviewMessage] = useState('');
  const [reviewError, setReviewError] = useState('');

  const [recommendations, setRecommendations] = useState([]);

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
        setQuantity(1);

        const sessionId = getOrCreateSessionId();
        trackProductView(slug, sessionId).catch(() => {});

        const [reviewsRes, recsRes] = await Promise.allSettled([
          getProductReviews(slug),
          getRecommendations(sessionId),
        ]);

        if (reviewsRes.status === 'fulfilled') {
          setReviewSummary(reviewsRes.value.data);
        }

        if (recsRes.status === 'fulfilled') {
          setRecommendations(recsRes.value.data);
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

  async function handleAddToCart() {
    setCartMessage('');
    setError('');

    if (!user) {
      navigate('/login', {
        state: {
          message: 'Sepete ürün eklemek için önce giriş yapmalısınız.',
        },
      });
      return;
    }

    try {
      setCartLoading(true);
      await addToCart(product.id, quantity);
      setCartMessage('Ürün sepete eklendi.');
    } catch {
      setError('Ürün sepete eklenemedi.');
    } finally {
      setCartLoading(false);
    }
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

  return (
    <main className="catalog-page">
      <Link className="back-link" to="/">
        Ürünlere dön
      </Link>

      {loading && <div className="state-message">Ürün yükleniyor...</div>}
      {error && <div className="state-message error-message">{error}</div>}
      {cartMessage && <div className="state-message success-message">{cartMessage}</div>}

      {!loading && !error && product && (
        <>
          <section className="product-detail">
            <div className="gallery">
              <div className="main-image-wrap">
                {product.badge && <span className="product-badge">{product.badge}</span>}
                <img src={selectedImage} alt={product.name} className="main-image" />
              </div>

              <div className="thumbnail-list">
                {images.map((image) => (
                  <button
                    type="button"
                    key={image.id}
                    className={selectedImage === image.imageUrl ? 'thumbnail active' : 'thumbnail'}
                    onClick={() => setSelectedImage(image.imageUrl)}
                  >
                    <img src={image.imageUrl} alt={`${product.name} ${image.displayOrder}`} />
                  </button>
                ))}
              </div>
            </div>

            <div className="detail-info">
              <p className="product-category">{product.category?.name}</p>
              <h1>{product.name}</h1>
              <p className="detail-price">{formatPrice(product.price)}</p>
              <p className="detail-description">{product.description}</p>

              <div className="detail-meta">
                <span>Stok: {product.stock}</span>
                <span>Satılan: {product.soldCount}</span>
                <span>Görüntülenme: {product.viewCount}</span>
              </div>

              <div className="add-cart-panel">
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
                  disabled={cartLoading || product.stock <= 0}
                >
                  {cartLoading ? 'Ekleniyor...' : 'Sepete Ekle'}
                </button>
              </div>

              <div className="store-panel">
                <p className="store-label">Mağaza</p>
                <p className="store-name">
                  {product.store?.name}
                  {product.store?.official && <span className="official-store">Resmi Mağaza</span>}
                </p>
                {product.store?.rating && <p className="store-rating">Puan: {product.store.rating}</p>}
              </div>
            </div>
          </section>

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
              <p className="state-message">Henüz yorum yapılmamış.</p>
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

                  {reviewMessage && <p className="state-message success-message">{reviewMessage}</p>}
                  {reviewError && <p className="state-message error-message">{reviewError}</p>}

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
                <p className="state-message">
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
                    />
                    <p className="recommendation-name">{rec.name}</p>
                    <p className="recommendation-price">{formatPrice(rec.price)}</p>
                    <p className="recommendation-category">{rec.categoryName}</p>
                  </Link>
                ))}
              </div>
            </section>
          )}
        </>
      )}
    </main>
  );
}
