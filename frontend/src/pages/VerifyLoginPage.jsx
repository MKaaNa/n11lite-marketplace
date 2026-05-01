import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function VerifyLoginPage() {
  const { verifyLoginCode } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [verificationId, setVerificationId] = useState(location.state?.verificationId || '');
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    if (!verificationId || !code) {
      setError('Doğrulama numarası ve kod zorunludur.');
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
        <p className="eyebrow">Email doğrulama</p>
        <h1>Girişi Doğrula</h1>

        {location.state?.email && (
          <p className="auth-hint">Kod {location.state.email} adresine gönderildi.</p>
        )}

        {error && <div className="state-message error-message">{error}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label>
            Doğrulama numarası
            <input
              value={verificationId}
              onChange={(event) => setVerificationId(event.target.value)}
            />
          </label>
          <label>
            6 haneli kod
            <input
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
