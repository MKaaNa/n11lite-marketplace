import { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { resendLoginVerification } from '../api/authApi';
import { useAuth } from '../context/AuthContext';

const CODE_TTL_MS = 2 * 60 * 1000;

function formatCountdown(remainingMs) {
  const totalSec = Math.max(0, Math.floor(remainingMs / 1000));
  const m = Math.floor(totalSec / 60);
  const s = totalSec % 60;
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
}

export default function VerifyLoginPage() {
  const { verifyLoginCode } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [verificationId, setVerificationId] = useState(() => location.state?.verificationId);
  const [code, setCode] = useState('');
  const [loading, setLoading] = useState(false);
  const [resendLoading, setResendLoading] = useState(false);
  const [error, setError] = useState('');
  const [deadlineMs, setDeadlineMs] = useState(() => Date.now() + CODE_TTL_MS);
  const [tick, setTick] = useState(() => Date.now());

  useEffect(() => {
    const id = setInterval(() => setTick(Date.now()), 1000);
    return () => clearInterval(id);
  }, []);

  const remainingMs = Math.max(0, deadlineMs - tick);
  const expired = remainingMs <= 0;

  async function handleResend() {
    setError('');
    if (!verificationId) {
      setError('Oturum doğrulaması bulunamadı. Lütfen tekrar giriş yap.');
      return;
    }
    try {
      setResendLoading(true);
      const { data } = await resendLoginVerification({
        verificationId: Number(verificationId),
      });
      if (data?.verificationId == null) {
        setError('Yanıt beklenenden farklı. Giriş sayfasından tekrar deneyin.');
        return;
      }
      setVerificationId(data.verificationId);
      setDeadlineMs(Date.now() + CODE_TTL_MS);
      setCode('');
    } catch {
      setError('Kod gönderilemedi. Giriş sayfasından tekrar deneyin.');
    } finally {
      setResendLoading(false);
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    if (!verificationId) {
      setError('Oturum doğrulaması bulunamadı. Lütfen tekrar giriş yap.');
      return;
    }

    if (expired) {
      setError('Kodun süresi doldu. Yeni kod almak için aşağıdaki Tekrar gönder düğmesini kullanın.');
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

        <p
          className={
            expired ? 'verify-code-meta verify-code-meta--expired' : 'verify-code-meta'
          }
        >
          {expired ? (
            <>
              Kodun geçerlilik süresi (<strong>2 dakika</strong>) doldu.
            </>
          ) : (
            <>
              Kod geçerliliği: <strong>{formatCountdown(remainingMs)}</strong> (toplam 2 dakika)
            </>
          )}
        </p>

        {error && <div className="alert alert--error">{error}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label>
            6 haneli kod
            <input
              className="verify-code-input"
              value={code}
              maxLength="6"
              inputMode="numeric"
              autoComplete="one-time-code"
              disabled={expired}
              onChange={(event) => setCode(event.target.value.replace(/\D/g, ''))}
            />
          </label>

          <button type="submit" disabled={loading || expired}>
            {loading ? 'Doğrulanıyor...' : 'Girişi Doğrula'}
          </button>
        </form>

        {expired && (
          <div className="auth-resend-row">
            <button
              type="button"
              className="secondary-button"
              disabled={resendLoading || !verificationId}
              onClick={handleResend}
            >
              {resendLoading ? 'Gönderiliyor...' : 'Kodu tekrar gönder'}
            </button>
          </div>
        )}

        <p className="auth-switch">
          Yeni kod mu gerekiyor? <Link to="/login">Tekrar giriş yap</Link>
        </p>
      </section>
    </main>
  );
}
