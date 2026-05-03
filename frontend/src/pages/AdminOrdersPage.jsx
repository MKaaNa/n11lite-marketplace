import { useEffect, useMemo, useState } from 'react';
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

  const orderStats = useMemo(() => {
    if (!orders.length) {
      return null;
    }
    return {
      total: orders.length,
      inProgress: orders.filter((o) => o.status === 'PAYMENT_PENDING' || o.status === 'PAID').length,
      shipped: orders.filter((o) => o.status === 'SHIPPED').length,
      delivered: orders.filter((o) => o.status === 'DELIVERED').length,
    };
  }, [orders]);

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
    <main className="admin-page">
      <header className="admin-page-header">
        <div className="admin-header-top">
          <div>
            <p className="eyebrow">Yönetim Paneli</p>
            <h1>Tüm Siparişler</h1>
          </div>
        </div>
        {!loading && orderStats && (
          <div className="admin-stats-grid">
            <div className="admin-stat-card">
              <div>
                <span className="admin-stat-value">{orderStats.total}</span>
                <span className="admin-stat-label">Toplam</span>
              </div>
            </div>
            <div className="admin-stat-card">
              <div>
                <span className="admin-stat-value">{orderStats.inProgress}</span>
                <span className="admin-stat-label">İşlemde</span>
              </div>
            </div>
            <div className="admin-stat-card">
              <div>
                <span className="admin-stat-value">{orderStats.shipped}</span>
                <span className="admin-stat-label">Kargoda</span>
              </div>
            </div>
            <div className="admin-stat-card">
              <div>
                <span className="admin-stat-value">{orderStats.delivered}</span>
                <span className="admin-stat-label">Teslim</span>
              </div>
            </div>
          </div>
        )}
      </header>

      {loading && <div className="alert alert--loading">Siparişler yükleniyor...</div>}
      {error && <div className="alert alert--error">{error}</div>}

      {!loading && orders.length === 0 && (
        <div className="alert alert--info">Henüz sipariş yok.</div>
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
                  <td>
                    <span className={`order-status-badge order-status-badge--${order.status.toLowerCase()}`}>
                      {STATUS_LABELS[order.status] || order.status}
                    </span>
                  </td>
                  <td>
                    <span className={`pay-status-badge pay-status-badge--${
                      order.paymentStatus === 'SUCCESS' ? 'success'
                      : order.paymentStatus === 'FAILED' ? 'failed'
                      : 'pending'
                    }`}>
                      {order.paymentStatus}
                    </span>
                  </td>
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
