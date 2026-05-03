import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  createAddress,
  deleteAddress,
  listAddresses,
  setDefaultAddress,
} from '../api/addressApi';
import { useToast } from '../context/ToastContext';

export default function AccountAddressesPage() {
  const { showToast } = useToast();
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [form, setForm] = useState({
    label: '',
    fullAddress: '',
  });

  useEffect(() => {
    loadAddresses();
  }, []);

  async function loadAddresses() {
    setLoading(true);
    setError('');
    try {
      const response = await listAddresses();
      setAddresses(response.data || []);
    } catch {
      setError('Adresler yüklenemedi.');
    } finally {
      setLoading(false);
    }
  }

  async function handleCreateAddress(event) {
    event.preventDefault();
    if (!form.label.trim() || !form.fullAddress.trim()) {
      setError('Adres etiketi ve adres metni zorunludur.');
      return;
    }

    setSaving(true);
    setError('');
    try {
      await createAddress({
        label: form.label.trim(),
        fullAddress: form.fullAddress.trim(),
        defaultAddress: addresses.length === 0,
      });
      setForm({ label: '', fullAddress: '' });
      showToast('Adres kaydedildi.', 'success');
      await loadAddresses();
    } catch (err) {
      setError(err.response?.data?.message || 'Adres kaydedilemedi.');
    } finally {
      setSaving(false);
    }
  }

  async function handleSetDefault(addressId) {
    setError('');
    try {
      await setDefaultAddress(addressId);
      showToast('Varsayılan adres güncellendi.', 'success');
      await loadAddresses();
    } catch (err) {
      setError(err.response?.data?.message || 'Varsayılan adres güncellenemedi.');
    }
  }

  async function handleDelete(addressId) {
    if (!window.confirm('Bu adres silinsin mi?')) {
      return;
    }
    setError('');
    try {
      await deleteAddress(addressId);
      showToast('Adres silindi.', 'info');
      await loadAddresses();
    } catch (err) {
      setError(err.response?.data?.message || 'Adres silinemedi.');
    }
  }

  return (
    <main className="catalog-page account-page">
      <section className="account-header">
        <div>
          <p className="eyebrow">Hesabım</p>
          <h1>Adreslerim</h1>
        </div>
        <Link className="details-link" to="/cart">Sepete Dön</Link>
      </section>

      <div className="account-tabs">
        <Link to="/account/orders">Siparişlerim</Link>
        <Link to="/account/addresses" className="active">Adreslerim</Link>
        <Link to="/account/cards">Kartlarım</Link>
      </div>

      {error && <div className="alert alert--error">{error}</div>}
      {loading && <div className="alert alert--loading">Adresler yükleniyor...</div>}

      <section className="account-layout">
        <div className="account-list">
          {!loading && addresses.length === 0 && (
            <div className="alert alert--info">Henüz kayıtlı adres yok.</div>
          )}
          {addresses.map((address) => (
            <article key={address.id} className="account-item-card">
              <div>
                <p className="account-item-title">
                  {address.label}
                  {address.defaultAddress && <span className="official-store">Varsayılan</span>}
                </p>
                <p className="account-item-body">{address.fullAddress}</p>
              </div>
              <div className="account-item-actions">
                {!address.defaultAddress && (
                  <button type="button" onClick={() => handleSetDefault(address.id)}>Varsayılan Yap</button>
                )}
                <button type="button" onClick={() => handleDelete(address.id)}>Sil</button>
              </div>
            </article>
          ))}
        </div>

        <aside className="account-form-panel">
          <h2>Yeni Adres Ekle</h2>
          <form className="account-form" onSubmit={handleCreateAddress}>
            <label>
              Adres etiketi
              <input
                value={form.label}
                onChange={(event) => setForm((current) => ({ ...current, label: event.target.value }))}
                placeholder="Ev, İş"
              />
            </label>
            <label>
              Açık adres
              <textarea
                rows={5}
                value={form.fullAddress}
                onChange={(event) => setForm((current) => ({ ...current, fullAddress: event.target.value }))}
                placeholder="Mahalle, cadde, bina no, ilçe / il"
              />
            </label>
            <button type="submit" className="primary-button" disabled={saving}>
              {saving ? 'Kaydediliyor...' : 'Adresi Kaydet'}
            </button>
          </form>
        </aside>
      </section>
    </main>
  );
}
