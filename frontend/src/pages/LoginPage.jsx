import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const { startLogin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  function handleChange(event) {
    setForm({
      ...form,
      [event.target.name]: event.target.value,
    });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    if (!form.email || !form.password) {
      setError('Email and password are required.');
      return;
    }

    try {
      setLoading(true);
      const response = await startLogin(form);
      navigate('/verify-login', {
        state: {
          verificationId: response.verificationId,
          email: form.email,
        },
      });
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-panel">
        <p className="eyebrow">Welcome back</p>
        <h1>Login</h1>

        {error && <div className="state-message error-message">{error}</div>}
        {location.state?.message && (
          <div className="state-message">{location.state.message}</div>
        )}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label>
            Email
            <input name="email" type="email" value={form.email} onChange={handleChange} />
          </label>
          <label>
            Password
            <input name="password" type="password" value={form.password} onChange={handleChange} />
          </label>

          <button type="submit" disabled={loading}>
            {loading ? 'Sending code...' : 'Login'}
          </button>
        </form>

        <p className="auth-hint">A verification code will be sent to your email.</p>
        <p className="auth-switch">
          New here? <Link to="/register">Register</Link>
        </p>
      </section>
    </main>
  );
}
