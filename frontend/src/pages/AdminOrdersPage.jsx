import { useEffect, useState } from 'react';
import { getAdminOrders, updateOrderStatus } from '../api/adminApi';

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

export default function AdminOrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [updating, setUpdating] = useState(null);

  useEffect(() => {
    loadOrders();
  }, []);

  async function loadOrders() {
    setLoading(true);
    setError('');
    try {
      const response = await getAdminOrders();
      setOrders(response.data);
    } catch {
      setError('Siparişler yüklenemedi.');
    } finally {
      setLoading(false);
    }
  }

  async function handleStatusUpdate(orderId, status) {
    setUpdating(orderId);
    setError('');
    try {
      const response = await updateOrderStatus(orderId, status);
      setOrders((prev) => prev.map((o) => (o.id === orderId ? response.data : o)));
    } catch (err) {
      setError(err.response?.data?.message || 'Durum güncellenemedi.');
    } finally {
      setUpdating(null);
    }
  }

  return (
    <main className="catalog-page">
      <section className="cart-header">
        <div>
          <p className="eyebrow">Yönetim Paneli</p>
          <h1>Tüm Siparişler</h1>
        </div>
      </section>

      {loading && <div className="state-message">Siparişler yükleniyor...</div>}
      {error && <div className="state-message error-message">{error}</div>}

      {!loading && orders.length === 0 && (
        <div className="state-message">Henüz sipariş yok.</div>
      )}

      {!loading && orders.length > 0 && (
        <div className="admin-orders-table-wrapper">
          <table className="admin-orders-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Müşteri</th>
                <th>Durum</th>
                <th>Ödeme</th>
                <th>Toplam</th>
                <th>Kupon</th>
                <th>İndirim</th>
                <th>Tarih</th>
                <th>İşlem</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id}>
                  <td>{order.id}</td>
                  <td>{order.userEmail}</td>
                  <td>{STATUS_LABELS[order.status] || order.status}</td>
                  <td>{order.paymentStatus}</td>
                  <td>{formatPrice(order.totalAmount)}</td>
                  <td>{order.couponCode || '-'}</td>
                  <td>
                    {order.discountAmount && Number(order.discountAmount) > 0
                      ? `-${formatPrice(order.discountAmount)}`
                      : '-'}
                  </td>
                  <td>{formatDate(order.createdAt)}</td>
                  <td>
                    <div className="admin-order-actions">
                      {(order.status === 'PAID') && (
                        <button
                          type="button"
                          className="admin-action-btn"
                          disabled={updating === order.id}
                          onClick={() => handleStatusUpdate(order.id, 'SHIPPED')}
                        >
                          Kargoya Ver
                        </button>
                      )}
                      {(order.status === 'SHIPPED') && (
                        <button
                          type="button"
                          className="admin-action-btn"
                          disabled={updating === order.id}
                          onClick={() => handleStatusUpdate(order.id, 'DELIVERED')}
                        >
                          Teslim Edildi
                        </button>
                      )}
                      {(order.status === 'PAYMENT_PENDING' || order.status === 'PAID' || order.status === 'SHIPPED') && (
                        <button
                          type="button"
                          className="admin-action-btn admin-action-cancel"
                          disabled={updating === order.id}
                          onClick={() => handleStatusUpdate(order.id, 'CANCELLED')}
                        >
                          İptal Et
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </main>
  );
}
