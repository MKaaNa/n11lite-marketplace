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
            <h1>{data.storeName}</h1>
            <p className="store-reviews-meta">
              Yorum ortalaması: <strong>{data.averageRating}</strong> / 5 —{' '}
              <strong>{data.reviewCount}</strong> yorum
            </p>
            <p className="store-reviews-disclaimer alert alert--info">
              Bu yorumlar mağazanın ürünlerine yapılmıştır.
            </p>
          </header>

          {data.reviewCount === 0 && (
            <p className="state-message">Bu mağazanın ürünlerine henüz yorum yapılmamış.</p>
          )}

          {data.reviews?.length > 0 && (
            <ul className="store-reviews-list">
              {data.reviews.map((review) => (
                <li key={review.id} className="review-item store-review-item">
                  <div className="review-header">
                    <span className="review-author">{review.userFullName}</span>
                    <span className="review-rating">
                      {'★'.repeat(review.rating)}
                      {'☆'.repeat(5 - review.rating)}
                    </span>
                  </div>
                  <p className="store-review-product">
                    Ürün:{' '}
                    <Link to={`/products/${review.productSlug}`}>{review.productName}</Link>
                  </p>
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
