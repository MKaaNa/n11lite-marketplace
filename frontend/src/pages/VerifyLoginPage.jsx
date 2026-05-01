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
      setError('Verification id and code are required.');
      return;
    }

    try {
      setLoading(true);
      await verifyLoginCode({
        verificationId: Number(verificationId),
        code,
      });
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Verification failed.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-panel">
        <p className="eyebrow">Email verification</p>
        <h1>Verify login</h1>

        {location.state?.email && (
          <p className="auth-hint">Code sent to {location.state.email}.</p>
        )}

        {error && <div className="state-message error-message">{error}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label>
            Verification id
            <input
              value={verificationId}
              onChange={(event) => setVerificationId(event.target.value)}
            />
          </label>
          <label>
            6-digit code
            <input
              value={code}
              maxLength="6"
              onChange={(event) => setCode(event.target.value)}
            />
          </label>

          <button type="submit" disabled={loading}>
            {loading ? 'Verifying...' : 'Verify login'}
          </button>
        </form>

        <p className="auth-switch">
          Need a new code? <Link to="/login">Login again</Link>
        </p>
      </section>
    </main>
  );
}
