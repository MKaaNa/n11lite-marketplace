import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getMyOrders } from '../api/orderApi';

const STATUS_LABELS = {
  PAYMENT_PENDING: 'Ödeme Bekliyor',
  PAID: 'Ödendi',
  SHIPPED: 'Kargoda',
  DELIVERED: 'Teslim Edildi',
  FAILED: 'Başarısız',
  CANCELLED: 'İptal Edildi',
};

function formatPrice(price) {
  return new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
  }).format(price || 0);
}

function formatDate(dateStr) {
  if (!dateStr) return '-';
  return new Date(dateStr).toLocaleDateString('tr-TR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export default function AccountOrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadOrders();
  }, []);

  async function loadOrders() {
    setLoading(true);
    setError('');
    try {
      const response = await getMyOrders();
      setOrders(Array.isArray(response.data) ? response.data : []);
    } catch {
      setError('Siparişleriniz yüklenemedi.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="catalog-page account-page">
      <section className="account-header">
        <div>
          <p className="eyebrow">Hesabım</p>
          <h1>Siparişlerim</h1>
        </div>
        <Link className="details-link" to="/cart">Sepete Dön</Link>
      </section>

      <div className="account-tabs">
        <Link to="/account/orders" className="active">Siparişlerim</Link>
        <Link to="/account/addresses">Adreslerim</Link>
        <Link to="/account/cards">Kartlarım</Link>
      </div>

      {loading && <div className="alert alert--loading">Siparişler yükleniyor...</div>}
      {error && <div className="alert alert--error">{error}</div>}

      {!loading && !error && orders.length === 0 && (
        <div className="state-message">
          Henüz siparişiniz bulunmuyor. <Link to="/products">Ürünlere göz at</Link>.
        </div>
      )}

      {!loading && orders.length > 0 && (
        <section className="account-order-list">
          {orders.map((order) => (
            <article key={order.id} className="account-order-card">
              <div className="account-order-top">
                <div>
                  <strong>Sipariş #{order.id}</strong>
                  <p>{formatDate(order.createdAt)}</p>
                </div>
                <span className={`order-status-badge order-status-badge--${order.status.toLowerCase()}`}>
                  {STATUS_LABELS[order.status] || order.status}
                </span>
              </div>

              <div className="account-order-meta">
                <span>Ödeme: {order.paymentStatus || '-'}</span>
                <span>Toplam: {formatPrice(order.totalAmount)}</span>
                <span>Kupon: {order.couponCode || '-'}</span>
              </div>

              {Array.isArray(order.items) && order.items.length > 0 && (
                <ul className="account-order-items">
                  {order.items.slice(0, 3).map((item) => (
                    <li key={item.id || `${order.id}-${item.productId}`}>
                      <span>{item.productName}</span>
                      <strong>{item.quantity} adet</strong>
                    </li>
                  ))}
                  {order.items.length > 3 && (
                    <li className="account-order-more">+{order.items.length - 3} ürün daha</li>
                  )}
                </ul>
              )}
            </article>
          ))}
        </section>
      )}
    </main>
  );
}
