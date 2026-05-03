import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getStoreReviews } from '../api/storeApi';

export default function StoreReviewsPage() {
  const { storeId } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError('');
      try {
        const response = await getStoreReviews(storeId);
        if (!cancelled) {
          setData(response.data);
        }
      } catch {
        if (!cancelled) {
          setData(null);
          setError('Mağaza yorumları yüklenemedi veya mağaza bulunamadı.');
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    if (storeId) {
      load();
    }

    return () => {
      cancelled = true;
    };
  }, [storeId]);

  return (
    <main className="catalog-page store-reviews-page">
      <Link className="back-link" to="/products">
        Ürünlere dön
      </Link>

      {loading && <div className="alert alert--loading">Yükleniyor...</div>}
      {error && <div className="alert alert--error">{error}</div>}

      {!loading && !error && data && (
        <>
          <header className="store-reviews-header">
            <div className="store-summary-card">
              <div className="store-summary-main">
                <p className="store-summary-label">Mağaza Değerlendirmesi</p>
                <h1>{data.storeName}</h1>
                <p className="store-reviews-disclaimer">
                  Bu yorumlar mağazanın ürünlerine yapılmıştır.
                </p>
              </div>
              <div className="store-summary-metrics">
                <div className="store-metric-box">
                  <span className="store-metric-title">Ortalama</span>
                  <strong>{data.averageRating}</strong>
                  <span className="store-metric-stars">★★★★★</span>
                </div>
                <div className="store-metric-box">
                  <span className="store-metric-title">Yorum</span>
                  <strong>{data.reviewCount}</strong>
                </div>
              </div>
            </div>
          </header>

          {data.reviewCount === 0 && (
            <p className="state-message">Bu mağazanın ürünlerine henüz yorum yapılmamış.</p>
          )}

          {data.reviews?.length > 0 && (
            <ul className="store-reviews-list">
              {data.reviews.map((review) => (
                <li key={review.id} className="store-review-card">
                  <div className="review-header">
                    <span className="review-author">{review.userFullName}</span>
                    <span className="review-rating review-rating--aligned">
                      {'★'.repeat(review.rating)}
                      {'☆'.repeat(5 - review.rating)}
                    </span>
                  </div>
                  <Link className="store-review-product-badge" to={`/products/${review.productSlug}`}>
                    {review.productName}
                  </Link>
                  <p className="review-comment">{review.comment}</p>
                </li>
              ))}
            </ul>
          )}
        </>
      )}
    </main>
  );
}
