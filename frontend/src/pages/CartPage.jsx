import { useEffect, useRef, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { listAddresses } from '../api/addressApi';
import { clearCart, getCart, removeCartItem, updateCartItem } from '../api/cartApi';
import { validateCoupon } from '../api/couponApi';
import { createOrder } from '../api/orderApi';
import { getPaymentStatus, initiatePayment } from '../api/paymentApi';
import { listPaymentCards } from '../api/paymentCardsApi';
import { useToast } from '../context/ToastContext';

const FALLBACK_IMAGE = 'https://placehold.co/300x300?text=N11Lite';

function formatPrice(price) {
  return new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
  }).format(price || 0);
}

export default function CartPage() {
  const { showToast } = useToast();
  const location = useLocation();
  const autoApplyCouponAttempted = useRef(false);
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [couponCode, setCouponCode] = useState('');
  const [appliedCoupon, setAppliedCoupon] = useState(null);
  const [shippingAddress, setShippingAddress] = useState('');
  const [createdOrder, setCreatedOrder] = useState(null);
  const [paymentInfo, setPaymentInfo] = useState(null);
  const [paymentError, setPaymentError] = useState('');
  const [paymentWarning, setPaymentWarning] = useState(null);
  const [paymentStatusInfo, setPaymentStatusInfo] = useState(null);
  const [checkingPayment, setCheckingPayment] = useState(false);
  const [creatingOrder, setCreatingOrder] = useState(false);
  const [startingPayment, setStartingPayment] = useState(false);
  const [savedAddresses, setSavedAddresses] = useState([]);
  const [addressSource, setAddressSource] = useState('manual');
  const [selectedAddressId, setSelectedAddressId] = useState('');
  const [savedCards, setSavedCards] = useState([]);

  useEffect(() => {
    autoApplyCouponAttempted.current = false;
  }, [location.pathname, location.key]);

  useEffect(() => {
    loadCart();
  }, []);

  useEffect(() => {
    if (!loading && cart) {
      loadCheckoutExtras();
    }
  }, [loading, cart]);

  useEffect(() => {
    const suggestedCoupon = location.state?.prefillCoupon;
    const autoApply = location.state?.autoApplyCoupon === true;
    if (typeof suggestedCoupon !== 'string' || !suggestedCoupon.trim()) {
      return;
    }
    setCouponCode(suggestedCoupon.trim());
    if (!autoApply) {
      showToast('Ürüne özel kupon hazır: sepette uygula.', 'info');
    }
  }, [location.state, showToast]);

  useEffect(() => {
    const code = typeof location.state?.prefillCoupon === 'string'
      ? location.state.prefillCoupon.trim()
      : '';
    const autoApply = location.state?.autoApplyCoupon === true;
    if (!autoApply || !code || loading || !cart || createdOrder) {
      return;
    }
    if (autoApplyCouponAttempted.current) {
      return;
    }
    autoApplyCouponAttempted.current = true;

    let cancelled = false;
    (async () => {
      try {
        setUpdating(true);
        setError('');
        setMessage('');
        const response = await validateCoupon(code, cart.totalAmount);
        if (!cancelled) {
          setAppliedCoupon(response.data);
          setCouponCode(response.data.code);
          setMessage('Kupon uygulandı.');
          showToast('Kupon uygulandı.', 'success');
        }
      } catch (err) {
        if (!cancelled) {
          setAppliedCoupon(null);
          setError(err.response?.data?.message || 'Kupon uygulanamadı.');
          showToast('Kupon otomatik uygulanamadı; Kuponu Uygula ile tekrar dene.', 'info');
        }
      } finally {
        if (!cancelled) {
          setUpdating(false);
        }
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [
    loading,
    cart,
    createdOrder,
    location.state?.prefillCoupon,
    location.state?.autoApplyCoupon,
    showToast,
  ]);

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

  async function loadCheckoutExtras() {
    try {
      const [addrRes, cardRes] = await Promise.all([
        listAddresses(),
        listPaymentCards(),
      ]);
      setSavedAddresses(addrRes.data || []);
      setSavedCards(cardRes.data || []);
    } catch {
      setSavedAddresses([]);
      setSavedCards([]);
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

  function clearCheckoutAfterCartChange() {
    if (createdOrder || paymentInfo || paymentError) {
      setCreatedOrder(null);
      setPaymentInfo(null);
      setPaymentError('');
      setPaymentWarning(null);
      setPaymentStatusInfo(null);
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
      clearCouponAfterCartChange();
      clearCheckoutAfterCartChange();
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
      clearCheckoutAfterCartChange();
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
      setCreatedOrder(null);
      setPaymentInfo(null);
      setPaymentError('');
      setPaymentWarning(null);
      setPaymentStatusInfo(null);
      setMessage('Sepet temizlendi.');
    } catch {
      setError('Sepet temizlenemedi.');
    } finally {
      setUpdating(false);
    }
  }

  async function handleApplyCoupon(event) {
    event.preventDefault();

    if (createdOrder) {
      setError('Sipariş oluşturulduktan sonra kupon değiştirilemez.');
      return;
    }

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
      showToast('Kupon uygulandı.', 'success');
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
    setCreatedOrder(null);
    setPaymentInfo(null);
    setPaymentError('');
    setPaymentWarning(null);
    setPaymentStatusInfo(null);
    setMessage('Kupon kaldırıldı.');
  }

  async function handleCreateOrder() {
    if (addressSource === 'saved') {
      if (!selectedAddressId) {
        setError('Kayıtlı adres seç veya manuel adres kullan.');
        return;
      }
    } else if (!shippingAddress.trim()) {
      setError('Teslimat adresi girmelisin.');
      return;
    }

    setCreatingOrder(true);
    setError('');
    setMessage('');
    setPaymentInfo(null);
    setPaymentError('');
    setPaymentWarning(null);
    setPaymentStatusInfo(null);

    try {
      const response = await createOrder({
        shippingAddress: addressSource === 'manual' ? shippingAddress.trim() : null,
        savedAddressId: addressSource === 'saved' ? Number(selectedAddressId) : null,
        couponCode: appliedCoupon?.code || null,
      });
      setCreatedOrder(response.data);
      setMessage('Sipariş oluşturuldu. Iyzico ödeme sayfasına yönlendiriliyorsun.');
      await startPaymentForOrder(response.data.id, true);
    } catch (err) {
      setError(err.response?.data?.message || 'Sipariş oluşturulamadı.');
    } finally {
      setCreatingOrder(false);
    }
  }

  async function handleStartPayment() {
    if (!createdOrder?.id) {
      return;
    }

    await startPaymentForOrder(createdOrder.id, true);
  }

  async function startPaymentForOrder(orderId, redirectToPaymentPage = false) {
    setStartingPayment(true);
    setPaymentInfo(null);
    setPaymentError('');
    setPaymentWarning(null);
    setPaymentStatusInfo(null);
    setError('');
    setMessage('');

    try {
      const response = await initiatePayment(orderId);
      setPaymentInfo(response.data);

      if (response.data.paymentPageUrl) {
        setMessage('Ödeme bağlantısı hazır.');
        if (redirectToPaymentPage) {
          window.location.href = response.data.paymentPageUrl;
        }
      } else {
        setPaymentError('Ödeme bağlantısı alınamadı. Lütfen daha sonra tekrar deneyin.');
      }
    } catch (err) {
      const backendMessage = err.response?.data?.message || '';
      const iyzicoMissing = backendMessage.toLowerCase().includes('iyzico');

      if (iyzicoMissing) {
        setPaymentWarning({
          title: 'Iyzico sandbox bilgileri tanımlı değil',
          body: 'Ödeme backend akışı hazır. Sandbox API bilgileri ve erişilebilir callback URL tanımlandığında kullanıcı Iyzico ödeme sayfasına yönlendirilir.',
        });
      } else {
        setPaymentError(backendMessage || 'Ödeme başlatılamadı.');
      }
    } finally {
      setStartingPayment(false);
    }
  }

  async function handleCheckPaymentStatus() {
    if (!createdOrder?.id) {
      return;
    }

    setCheckingPayment(true);
    setPaymentStatusInfo(null);
    setPaymentError('');

    try {
      const response = await getPaymentStatus(createdOrder.id);
      const payment = response.data;
      let messageText = 'Ödeme henüz tamamlanmadı veya callback bekleniyor.';

      if (payment.status === 'SUCCESS') {
        messageText = 'Ödeme başarılı. Sipariş durumu güncellendi.';
      } else if (payment.status === 'FAILED') {
        messageText = 'Ödeme başarısız olarak işaretlendi.';
      }

      setPaymentStatusInfo({
        status: payment.status,
        message: messageText,
      });
    } catch (err) {
      const status = err.response?.status;
      setPaymentStatusInfo({
        status: 'BULUNAMADI',
        message: status === 404
          ? 'Ödeme kaydı henüz oluşmadı. Önce ödeme başlatmayı deneyin.'
          : 'Ödeme durumu kontrol edilemedi.',
      });
    } finally {
      setCheckingPayment(false);
    }
  }

  const items = cart?.items || [];
  const finalTotal = appliedCoupon?.finalTotal ?? cart?.totalAmount;
  const installmentEligible = Number(finalTotal || 0) >= 5000;
  const addressReady = addressSource === 'saved'
    ? Boolean(selectedAddressId)
    : Boolean(shippingAddress.trim());
  const canCreateOrder = items.length > 0 && addressReady && !createdOrder && !creatingOrder && !startingPayment;
  const checkoutStep = paymentInfo?.paymentPageUrl ? 3 : (createdOrder ? 2 : 1);

  return (
    <main className="catalog-page">
      <section className="cart-header">
        <div>
          <p className="eyebrow">Alışveriş sepeti</p>
          <h1>Sepetim</h1>
        </div>
        <Link className="details-link" to="/products">
          Alışverişe Devam Et
        </Link>
      </section>
      <section className="checkout-stepper" aria-label="Checkout adımları">
        <div className={checkoutStep >= 1 ? 'checkout-step active' : 'checkout-step'}>
          <span>1</span>
          <p>Sepet</p>
        </div>
        <div className={checkoutStep >= 2 ? 'checkout-step active' : 'checkout-step'}>
          <span>2</span>
          <p>Adres</p>
        </div>
        <div className={checkoutStep >= 3 ? 'checkout-step active' : 'checkout-step'}>
          <span>3</span>
          <p>Ödeme</p>
        </div>
      </section>

      {loading && <div className="alert alert--loading">Sepet yükleniyor...</div>}
      {error && <div className="alert alert--error">{error}</div>}
      {message && <div className="alert alert--success">{message}</div>}

      {!loading && items.length === 0 && (
        <div className="alert alert--info empty-cart-state">
          <img
            className="empty-state-visual"
            src="/assets/brand/illus-empty.png"
            alt=""
            decoding="async"
          />
          <p className="empty-cart-state-text">
            Sepetiniz boş. <Link to="/products">Ürünlere göz atın</Link>
          </p>
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
                  {item.variantValue && (
                    <p>
                      {item.variantType || 'Varyant'}: {item.variantValue}
                    </p>
                  )}
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
                  disabled={updating || Boolean(createdOrder)}
                />
                <button type="submit" disabled={updating || Boolean(createdOrder)}>
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
              Bu ekrandan sipariş oluşturabilir ve Iyzico ödeme akışını başlatabilirsin.
              Sandbox bilgileri tanımlı değilse ödeme sayfası yerine bilgilendirme gösterilir.
            </p>
            {installmentEligible && (
              <p className="checkout-note subtle">
                Bu tutarda 2-3 taksit seçenekleri ödeme sayfasında sunulabilir.
              </p>
            )}
            <div className="trust-badges">
              <span>SSL Güvenli Ödeme</span>
              <span>Kolay İade Desteği</span>
              <span>7/24 Müşteri Desteği</span>
            </div>

            <div className="checkout-panel">
              <fieldset className="address-source-fieldset">
                <legend>Teslimat adresi</legend>
                <label className="radio-line">
                  <input
                    type="radio"
                    name="addressSource"
                    checked={addressSource === 'manual'}
                    onChange={() => {
                      setAddressSource('manual');
                      setSelectedAddressId('');
                    }}
                    disabled={Boolean(createdOrder) || creatingOrder}
                  />
                  Yeni adres yaz
                </label>
                <label className="radio-line">
                  <input
                    type="radio"
                    name="addressSource"
                    checked={addressSource === 'saved'}
                    onChange={() => setAddressSource('saved')}
                    disabled={Boolean(createdOrder) || creatingOrder || savedAddresses.length === 0}
                  />
                  Kayıtlı adres
                  {savedAddresses.length === 0 && ' (önce adres kaydet)'}
                </label>
                <Link className="details-link" to="/account/addresses">
                  Adreslerimi Yönet
                </Link>
              </fieldset>

              {addressSource === 'saved' && (
                <label htmlFor="savedAddressSelect" className="saved-address-select">
                  Kayıtlı adres seç
                  <select
                    id="savedAddressSelect"
                    value={selectedAddressId}
                    onChange={(e) => setSelectedAddressId(e.target.value)}
                    disabled={Boolean(createdOrder) || creatingOrder}
                  >
                    <option value="">— Seç —</option>
                    {savedAddresses.map((a) => (
                      <option key={a.id} value={a.id}>
                        {a.label}
                        {a.defaultAddress ? ' (varsayılan)' : ''}
                      </option>
                    ))}
                  </select>
                </label>
              )}

              <label htmlFor="shippingAddress">
                {addressSource === 'saved' ? 'Önizleme (siparişte kayıtlı metin kullanılır)' : 'Teslimat adresi'}
                <textarea
                  id="shippingAddress"
                  value={
                    addressSource === 'saved' && selectedAddressId
                      ? (savedAddresses.find((a) => String(a.id) === String(selectedAddressId))?.fullAddress || '')
                      : shippingAddress
                  }
                  onChange={(event) => {
                    if (addressSource === 'manual') {
                      setShippingAddress(event.target.value);
                    }
                  }}
                  rows={4}
                  placeholder="Mahalle, cadde, bina no, ilçe / il"
                  readOnly={addressSource === 'saved'}
                  disabled={Boolean(createdOrder) || creatingOrder}
                />
              </label>

              <div className="saved-cards-panel">
                <h3>Kayıtlı kartlar (Iyzico)</h3>
                <p className="checkout-note subtle">
                  Sandbox test kartı bilgileri Iyzico dokümantasyonundan alınmalıdır; güvenlik nedeniyle tam kart numarası
                  uygulamada saklanmaz veya önceden doldurulmaz. Kayıtlı kartlar yalnızca maskeli bilgi ve son dört hane ile
                  gösterilir.
                </p>
                {savedCards.length > 0 && (
                  <ul className="saved-card-list">
                    {savedCards.map((c) => (
                      <li key={c.cardToken}>
                        <span>
                          {c.cardAlias || 'Kart'} · **** {c.lastFourDigits || '????'} ({c.cardAssociation || '—'})
                        </span>
                      </li>
                    ))}
                  </ul>
                )}
                <Link className="details-link" to="/account/cards">
                  Kartlarımı Yönet
                </Link>
              </div>

              <button
                type="button"
                className="primary-button"
                disabled={!canCreateOrder}
                onClick={handleCreateOrder}
              >
                {creatingOrder || startingPayment ? 'Ödeme sayfasına geçiliyor...' : 'Siparişi Oluştur ve Ödemeye Geç'}
              </button>
            </div>

            {createdOrder && (
              <div className="order-result">
                <h3>Sipariş Oluşturuldu</h3>
                <p>Sipariş No: #{createdOrder.id}</p>
                <p>Sipariş Durumu: {createdOrder.status}</p>
                <p>Ödeme Durumu: {createdOrder.paymentStatus}</p>
                {createdOrder.discountAmount > 0 && (
                  <p>İndirim: -{formatPrice(createdOrder.discountAmount)}</p>
                )}
                <p>Toplam Tutar: {formatPrice(createdOrder.totalAmount)}</p>

                <p className="payment-status-note">
                  {startingPayment
                    ? 'Iyzico ödeme sayfası hazırlanıyor...'
                    : 'Sipariş oluşturulduğunda ödeme sayfası otomatik açılır.'}
                </p>
              </div>
            )}

            {paymentError && <p className="alert alert--error">{paymentError}</p>}
            {paymentWarning && (
              <div className="alert alert--warning">
                <div>
                  <strong style={{ display: 'block', marginBottom: 4 }}>{paymentWarning.title}</strong>
                  <span>{paymentWarning.body}</span>
                </div>
              </div>
            )}

            {paymentInfo?.paymentPageUrl && (
              <>
                <a
                  className="payment-link"
                  href={paymentInfo.paymentPageUrl}
                  target="_blank"
                  rel="noreferrer"
                >
                  Iyzico Ödeme Sayfasına Git
                </a>
                <p className="payment-status-note">
                  Ödeme tamamlandıktan sonra ödeme durumunu buradan kontrol edebilirsin.
                </p>
              </>
            )}

            {createdOrder && !paymentInfo?.paymentPageUrl && (
              <button
                type="button"
                className="primary-button"
                disabled={startingPayment}
                onClick={handleStartPayment}
              >
                {startingPayment ? 'Ödeme sayfası hazırlanıyor...' : 'Ödemeyi Tekrar Başlat'}
              </button>
            )}

            {createdOrder && (
              <div className="payment-status-panel">
                <button
                  type="button"
                  disabled={checkingPayment}
                  onClick={handleCheckPaymentStatus}
                >
                  {checkingPayment ? 'Durum kontrol ediliyor...' : 'Ödeme Durumunu Kontrol Et'}
                </button>

                {paymentStatusInfo && (
                  <p>
                    <strong>{paymentStatusInfo.status}</strong> - {paymentStatusInfo.message}
                  </p>
                )}
              </div>
            )}

            <button type="button" disabled={updating} onClick={handleClearCart}>
              Sepeti Temizle
            </button>
          </aside>
        </section>
      )}
    </main>
  );
}
