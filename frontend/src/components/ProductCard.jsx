function formatPrice(price) {
  return new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
  }).format(price);
}

export default function ProductCard({ product }) {
  return (
    <article className="product-card">
      <div className="product-image-wrap">
        {product.badge && <span className="product-badge">{product.badge}</span>}
        <img
          src={product.mainImageUrl || 'https://placehold.co/600x600?text=N11Lite'}
          alt={product.name}
          className="product-image"
        />
      </div>

      <div className="product-card-body">
        <p className="product-category">{product.category?.name}</p>
        <h2>{product.name}</h2>
        <p className="product-price">{formatPrice(product.price)}</p>
        <p className="product-stock">Stock: {product.stock}</p>
        <p className="product-store">
          {product.store?.name}
          {product.store?.official && <span className="official-store">Official</span>}
        </p>
      </div>
    </article>
  );
}
