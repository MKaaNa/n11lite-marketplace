import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { clearCart, getCart, removeCartItem, updateCartItem } from '../api/cartApi';

const FALLBACK_IMAGE = 'https://placehold.co/300x300?text=N11Lite';

function formatPrice(price) {
  return new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
  }).format(price || 0);
}

export default function CartPage() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    loadCart();
  }, []);

  async function loadCart() {
    setLoading(true);
    setError('');

    try {
      const response = await getCart();
      setCart(response.data);
    } catch {
      setError('Sepet yüklenemedi.');
    } finally {
      setLoading(false);
    }
  }

  async function handleQuantityChange(item, quantity) {
    if (quantity < 1) {
      return;
    }

    setUpdating(true);
    setError('');
    setMessage('');

    try {
      const response = await updateCartItem(item.id, quantity);
      setCart(response.data);
    } catch {
      setError('Adet güncellenemedi.');
    } finally {
      setUpdating(false);
    }
  }

  async function handleRemove(itemId) {
    setUpdating(true);
    setError('');
    setMessage('');

    try {
      const response = await removeCartItem(itemId);
      setCart(response.data);
      setMessage('Ürün sepetten kaldırıldı.');
    } catch {
      setError('Ürün sepetten kaldırılamadı.');
    } finally {
      setUpdating(false);
    }
  }

  async function handleClearCart() {
    setUpdating(true);
    setError('');
    setMessage('');

    try {
      const response = await clearCart();
      setCart(response.data);
      setMessage('Sepet temizlendi.');
    } catch {
      setError('Sepet temizlenemedi.');
    } finally {
      setUpdating(false);
    }
  }

  const items = cart?.items || [];

  return (
    <main className="catalog-page">
      <section className="cart-header">
        <div>
          <p className="eyebrow">Alışveriş sepeti</p>
          <h1>Sepetim</h1>
        </div>
        <Link className="details-link" to="/">
          Alışverişe Devam Et
        </Link>
      </section>

      {loading && <div className="state-message">Sepet yükleniyor...</div>}
      {error && <div className="state-message error-message">{error}</div>}
      {message && <div className="state-message success-message">{message}</div>}

      {!loading && items.length === 0 && (
        <div className="state-message">
          Sepetiniz boş. <Link to="/">Ürünlere göz atın</Link>
        </div>
      )}

      {!loading && items.length > 0 && (
        <section className="cart-layout">
          <div className="cart-items">
            {items.map((item) => (
              <article className="cart-item" key={item.id}>
                <img
                  src={item.imageUrl || FALLBACK_IMAGE}
                  alt={item.productName}
                  className="cart-item-image"
                />

                <div className="cart-item-info">
                  <Link to={`/products/${item.productSlug}`}>{item.productName}</Link>
                  {item.storeName && <p>{item.storeName}</p>}
                  <p>Birim fiyat: {formatPrice(item.unitPrice)}</p>
                </div>

                <div className="quantity-controls">
                  <button
                    type="button"
                    disabled={updating || item.quantity <= 1}
                    onClick={() => handleQuantityChange(item, item.quantity - 1)}
                  >
                    -
                  </button>
                  <span>{item.quantity}</span>
                  <button
                    type="button"
                    disabled={updating}
                    onClick={() => handleQuantityChange(item, item.quantity + 1)}
                  >
                    +
                  </button>
                </div>

                <p className="cart-line-total">{formatPrice(item.lineTotal)}</p>

                <button
                  type="button"
                  disabled={updating}
                  onClick={() => handleRemove(item.id)}
                >
                  Kaldır
                </button>
              </article>
            ))}
          </div>

          <aside className="cart-summary">
            <h2>Özet</h2>
            <p>Toplam: {formatPrice(cart.totalAmount)}</p>
            <button type="button" disabled={updating} onClick={handleClearCart}>
              Sepeti Temizle
            </button>
          </aside>
        </section>
      )}
    </main>
  );
}
