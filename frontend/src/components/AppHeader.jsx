import { Link, NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function AppHeader() {
  const { user, loading, logout } = useAuth();

  return (
    <header className="app-header">
      <Link className="brand-link" to="/">
        N11Lite
      </Link>

      <nav className="main-nav">
        <NavLink to="/">Ürünler</NavLink>

        {!loading && !user && (
          <>
            <NavLink to="/login">Giriş Yap</NavLink>
            <NavLink to="/register">Kayıt Ol</NavLink>
          </>
        )}

        {!loading && user && (
          <>
            <NavLink to="/cart">Sepet</NavLink>
            <div className="user-menu">
              <span>{user.fullName || user.email}</span>
              <button type="button" onClick={logout}>
                Çıkış Yap
              </button>
            </div>
          </>
        )}
      </nav>
    </header>
  );
}
