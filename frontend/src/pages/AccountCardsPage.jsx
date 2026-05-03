import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { deletePaymentCard, listPaymentCards, registerPaymentCard } from '../api/paymentCardsApi';
import { useToast } from '../context/ToastContext';

export default function AccountCardsPage() {
  const { showToast } = useToast();
  const [cards, setCards] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [form, setForm] = useState({
    cardAlias: '',
    cardHolderName: '',
    cardNumber: '',
    expireMonth: '',
    expireYear: '',
  });

  useEffect(() => {
    loadCards();
  }, []);

  async function loadCards() {
    setLoading(true);
    setError('');
    try {
      const response = await listPaymentCards();
      setCards(response.data || []);
    } catch {
      setError('Kartlar yüklenemedi.');
    } finally {
      setLoading(false);
    }
  }

  async function handleRegisterCard(event) {
    event.preventDefault();
    setSaving(true);
    setError('');
    try {
      await registerPaymentCard({
        cardAlias: form.cardAlias.trim(),
        cardHolderName: form.cardHolderName.trim(),
        cardNumber: form.cardNumber.replace(/\s/g, ''),
        expireMonth: form.expireMonth.padStart(2, '0'),
        expireYear: form.expireYear.length === 2 ? `20${form.expireYear}` : form.expireYear,
      });
      setForm({
        cardAlias: '',
        cardHolderName: '',
        cardNumber: '',
        expireMonth: '',
        expireYear: '',
      });
      showToast('Kart kaydedildi.', 'success');
      await loadCards();
    } catch (err) {
      setError(err.response?.data?.message || 'Kart kaydedilemedi.');
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(token) {
    if (!window.confirm('Bu kart silinsin mi?')) {
      return;
    }
    setError('');
    try {
      await deletePaymentCard(token);
      showToast('Kart silindi.', 'info');
      await loadCards();
    } catch (err) {
      setError(err.response?.data?.message || 'Kart silinemedi.');
    }
  }

  return (
    <main className="catalog-page account-page">
      <section className="account-header">
        <div>
          <p className="eyebrow">Hesabım</p>
          <h1>Kartlarım</h1>
        </div>
        <Link className="details-link" to="/cart">Sepete Dön</Link>
      </section>

      <div className="account-tabs">
        <Link to="/account/orders">Siparişlerim</Link>
        <Link to="/account/addresses">Adreslerim</Link>
        <Link to="/account/cards" className="active">Kartlarım</Link>
      </div>

      {error && <div className="alert alert--error">{error}</div>}
      {loading && <div className="alert alert--loading">Kartlar yükleniyor...</div>}

      <section className="account-layout">
        <div className="account-list">
          <p className="checkout-note">
            Güvenlik nedeniyle tam kart numarası uygulamada saklanmaz; sadece maskeli bilgi gösterilir.
          </p>
          {!loading && cards.length === 0 && (
            <div className="alert alert--info">Henüz kayıtlı kart yok.</div>
          )}
          {cards.map((card) => (
            <article key={card.cardToken} className="account-item-card">
              <div>
                <p className="account-item-title">{card.cardAlias || 'Kart'}</p>
                <p className="account-item-body">
                  **** {card.lastFourDigits || '????'} ({card.cardAssociation || '—'})
                </p>
              </div>
              <div className="account-item-actions">
                <button type="button" onClick={() => handleDelete(card.cardToken)}>Sil</button>
              </div>
            </article>
          ))}
        </div>

        <aside className="account-form-panel">
          <h2>Yeni Kart Ekle</h2>
          <form className="account-form" onSubmit={handleRegisterCard}>
            <label>
              Kart adı
              <input
                value={form.cardAlias}
                onChange={(event) => setForm((current) => ({ ...current, cardAlias: event.target.value }))}
              />
            </label>
            <label>
              Kart üzerindeki isim
              <input
                value={form.cardHolderName}
                onChange={(event) => setForm((current) => ({ ...current, cardHolderName: event.target.value }))}
              />
            </label>
            <label>
              Kart numarası
              <input
                value={form.cardNumber}
                onChange={(event) => setForm((current) => ({ ...current, cardNumber: event.target.value }))}
                autoComplete="off"
              />
            </label>
            <div className="card-expiry-row">
              <label>
                Ay
                <input
                  value={form.expireMonth}
                  maxLength={2}
                  onChange={(event) => setForm((current) => ({ ...current, expireMonth: event.target.value }))}
                />
              </label>
              <label>
                Yıl
                <input
                  value={form.expireYear}
                  maxLength={4}
                  onChange={(event) => setForm((current) => ({ ...current, expireYear: event.target.value }))}
                />
              </label>
            </div>
            <button type="submit" className="primary-button" disabled={saving}>
              {saving ? 'Kaydediliyor...' : 'Kartı Kaydet'}
            </button>
          </form>
        </aside>
      </section>
    </main>
  );
}
