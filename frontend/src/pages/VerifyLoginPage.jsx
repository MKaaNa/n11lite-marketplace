import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function VerifyLoginPage() {
  const { verifyLoginCode } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const verificationId = location.state?.verificationId;
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    if (!verificationId) {
      setError('Oturum doğrulaması bulunamadı. Lütfen tekrar giriş yap.');
      return;
    }

    if (!code) {
      setError('Doğrulama kodu zorunludur.');
      return;
    }

    try {
      setLoading(true);
      await verifyLoginCode({
        verificationId: Number(verificationId),
        code,
      });
      navigate('/');
    } catch {
      setError('Doğrulama başarısız. Kodu kontrol edin.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-panel">
        <span className="auth-brand">N11Lite</span>
        <p className="eyebrow">Email doğrulama</p>
        <h1>Girişi Doğrula</h1>
        <img
          className="auth-illustration"
          src="/assets/brand/illus-auth.png"
          alt=""
          decoding="async"
        />

        {location.state?.email && (
          <p className="auth-hint">Kod {location.state.email} adresine gönderildi.</p>
        )}

        <div className="mailpit-helper">
          <p>
            Local demo ortamında doğrulama kodunu Mailpit ekranından görebilirsin.
            Gerçek SMTP tanımlandığında kod gerçek e-posta adresine gönderilir.
          </p>
          <a href="http://localhost:8025" target="_blank" rel="noreferrer">
            Mail Kutusunu Aç
          </a>
        </div>

        {error && <div className="alert alert--error">{error}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label>
            6 haneli kod
            <input
              className="verify-code-input"
              value={code}
              maxLength="6"
              onChange={(event) => setCode(event.target.value)}
            />
          </label>

          <button type="submit" disabled={loading}>
            {loading ? 'Doğrulanıyor...' : 'Girişi Doğrula'}
          </button>
        </form>

        <p className="auth-switch">
          Yeni kod mu gerekiyor? <Link to="/login">Tekrar giriş yap</Link>
        </p>
      </section>
    </main>
  );
}
