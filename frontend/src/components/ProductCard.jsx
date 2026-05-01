import { Link } from 'react-router-dom';

function formatPrice(price) {
  return new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
  }).format(price);
}

export default function ProductCard({ product }) {
  return (
    <article className="product-card">
      <Link className="product-card-link" to={`/products/${product.slug}`}>
        <div className="product-image-wrap">
          {product.badge && <span className="product-badge">{product.badge}</span>}
          <img
            src={product.mainImageUrl || 'https://placehold.co/600x600?text=N11Lite'}
            alt={product.name}
            className="product-image"
          />
        </div>
      </Link>

      <div className="product-card-body">
        <p className="product-category">{product.category?.name}</p>
        <h2>
          <Link to={`/products/${product.slug}`}>{product.name}</Link>
        </h2>
        <p className="product-price">{formatPrice(product.price)}</p>
        <p className="product-stock">Stock: {product.stock}</p>
        <p className="product-store">
          {product.store?.name}
          {product.store?.official && <span className="official-store">Official</span>}
        </p>
        <Link className="details-link" to={`/products/${product.slug}`}>
          View details
        </Link>
      </div>
    </article>
  );
}
