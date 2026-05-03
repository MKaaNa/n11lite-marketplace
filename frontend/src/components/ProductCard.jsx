import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';

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

  // Demo-friendly presentation: current seeded price is discounted price.
  const discountRate = 20;
  const originalPrice = Number((product.price / (1 - discountRate / 100)).toFixed(2));
  return { isDiscounted: true, currentPrice: product.price, originalPrice, discountRate };
}

function getDemoRating(product) {
  const rating = (4.5 + ((product.id || 1) % 5) * 0.08).toFixed(1);
  const reviewCount = ((product.id || 1) * 137) % 900 + 84;
  return { rating, reviewCount };
}

export default function ProductCard({ product }) {
  const [imageError, setImageError] = useState(false);
  const priceModel = useMemo(() => getPriceModel(product), [product]);
  const demoRating = useMemo(() => getDemoRating(product), [product]);
  const imageUrl = !imageError && product.mainImageUrl
    ? product.mainImageUrl
    : 'https://placehold.co/800x800?text=Gorsel+Hazirlaniyor';

  return (
    <article className="product-card">
      <Link className="product-card-link" to={`/products/${product.slug}`}>
        <div className="product-image-wrap">
          <div className="product-card-badges">
            {product.badge && (
              <span className={`product-badge product-badge--${product.badge.toLowerCase()}`}>
                {formatBadge(product.badge)}
              </span>
            )}
            {product.store?.official && product.badge !== 'FREE_SHIPPING' && (
              <span className="shipping-badge">Ücretsiz Kargo</span>
            )}
          </div>
          <img
            src={imageUrl}
            alt={product.name}
            className="product-image"
            loading="lazy"
            onError={() => setImageError(true)}
          />
        </div>
      </Link>

      <div className="product-card-body">
        <p className="product-category">{product.category?.name}</p>
        <h2>
          <Link to={`/products/${product.slug}`}>{product.name}</Link>
        </h2>
        <div className="product-price-box">
          <div className="product-price-row">
            <p className="product-price">{formatPrice(priceModel.currentPrice)}</p>
            {priceModel.isDiscounted && (
              <span className="product-discount-note">-%{priceModel.discountRate}</span>
            )}
          </div>
          {priceModel.isDiscounted && (
            <p className="product-price-old">{formatPrice(priceModel.originalPrice)}</p>
          )}
        </div>
        <p className="product-store">
          {product.store?.name}
          {product.store?.official && <span className="verified-store">●</span>}
        </p>
        <div className="product-card-meta">
          <span>★ {demoRating.rating}</span>
          <span>({demoRating.reviewCount})</span>
          <span>Stok: {product.stock}</span>
        </div>
        <Link className="add-card-link" to={`/products/${product.slug}`}>
          Sepete Ekle
        </Link>
      </div>
    </article>
  );
}
