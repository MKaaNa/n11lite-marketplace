import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const { registerUser } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    email: '',
    password: '',
    fullName: '',
    phone: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  function handleChange(event) {
    setForm({
      ...form,
      [event.target.name]: event.target.value,
    });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setMessage('');

    if (!form.email || !form.password || !form.fullName) {
      setError('Email, şifre ve ad soyad zorunludur.');
      return;
    }

    try {
      setLoading(true);
      await registerUser(form);
      setMessage('Kayıt başarılı. Giriş sayfasına yönlendiriliyorsunuz.');
      setTimeout(() => navigate('/login'), 700);
    } catch {
      setError('Kayıt başarısız. Bilgileri kontrol edin.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-panel">
        <span className="auth-brand">N11Lite</span>
        <p className="eyebrow">Hesap oluştur</p>
        <h1>Kayıt Ol</h1>
        <img
          className="auth-illustration"
          src="/assets/brand/illus-auth.png"
          alt=""
          decoding="async"
        />

        {error && <div className="alert alert--error">{error}</div>}
        {message && <div className="alert alert--success">{message}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label>
            Ad Soyad
            <input name="fullName" value={form.fullName} onChange={handleChange} />
          </label>
          <label>
            E-posta
            <input name="email" type="email" value={form.email} onChange={handleChange} />
          </label>
          <label>
            Şifre
            <input name="password" type="password" value={form.password} onChange={handleChange} />
          </label>
          <label>
            Telefon
            <input name="phone" value={form.phone} onChange={handleChange} />
          </label>

          <button type="submit" disabled={loading}>
            {loading ? 'Kaydediliyor...' : 'Kayıt Ol'}
          </button>
        </form>

        <p className="auth-switch">
          Zaten hesabınız var mı? <Link to="/login">Giriş Yap</Link>
        </p>
      </section>
    </main>
  );
}
