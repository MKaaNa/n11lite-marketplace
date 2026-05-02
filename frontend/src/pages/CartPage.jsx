import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { clearCart, getCart, removeCartItem, updateCartItem } from '../api/cartApi';
import { validateCoupon } from '../api/couponApi';

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
  const [couponCode, setCouponCode] = useState('');
  const [appliedCoupon, setAppliedCoupon] = useState(null);

  useEffect(() => {
    loadCart();
  }, []);

  async function loadCart() {
    setLoading(true);
    setError('');

    try {
      const response = await getCart();
      setCart(response.data);
      setAppliedCoupon(null);
    } catch {
      setError('Sepet yüklenemedi.');
    } finally {
      setLoading(false);
    }
  }

  function clearCouponAfterCartChange() {
    if (appliedCoupon) {
      setAppliedCoupon(null);
      setMessage('Sepet değiştiği için kupon tekrar uygulanmalı.');
      return true;
    }

    return false;
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
      clearCouponAfterCartChange();
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
      if (!clearCouponAfterCartChange()) {
        setMessage('Ürün sepetten kaldırıldı.');
      }
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
      setAppliedCoupon(null);
      setCouponCode('');
      setMessage('Sepet temizlendi.');
    } catch {
      setError('Sepet temizlenemedi.');
    } finally {
      setUpdating(false);
    }
  }

  async function handleApplyCoupon(event) {
    event.preventDefault();

    if (!couponCode.trim()) {
      setError('Kupon kodu girmelisin.');
      return;
    }

    setUpdating(true);
    setError('');
    setMessage('');

    try {
      const response = await validateCoupon(couponCode.trim(), cart.totalAmount);
      setAppliedCoupon(response.data);
      setCouponCode(response.data.code);
      setMessage('Kupon uygulandı.');
    } catch (err) {
      setAppliedCoupon(null);
      setError(err.response?.data?.message || 'Kupon uygulanamadı.');
    } finally {
      setUpdating(false);
    }
  }

  function handleRemoveCoupon() {
    setAppliedCoupon(null);
    setCouponCode('');
    setMessage('Kupon kaldırıldı.');
  }

  const items = cart?.items || [];
  const finalTotal = appliedCoupon?.finalTotal ?? cart?.totalAmount;

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

            <form className="coupon-form" onSubmit={handleApplyCoupon}>
              <label htmlFor="couponCode">Kupon Kodu</label>
              <div className="coupon-input-row">
                <input
                  id="couponCode"
                  type="text"
                  value={couponCode}
                  onChange={(event) => setCouponCode(event.target.value)}
                  placeholder="N11WELCOME"
                  disabled={updating}
                />
                <button type="submit" disabled={updating}>
                  Kuponu Uygula
                </button>
              </div>
            </form>

            {appliedCoupon && (
              <div className="coupon-result">
                <p>İndirim: -{formatPrice(appliedCoupon.discountAmount)}</p>
                <p>Ödenecek Tutar: {formatPrice(finalTotal)}</p>
                <button type="button" disabled={updating} onClick={handleRemoveCoupon}>
                  Kuponu Kaldır
                </button>
              </div>
            )}

            <p className="checkout-note">
              Sipariş ve ödeme adımı Swagger/API üzerinden tamamlanabilir.
            </p>

            <button type="button" disabled={updating} onClick={handleClearCart}>
              Sepeti Temizle
            </button>
          </aside>
        </section>
      )}
    </main>
  );
}
