import { useEffect, useState } from 'react';
import { Link, NavLink, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getCart } from '../api/cartApi';

export default function AppHeader() {
  const { user, loading, logout } = useAuth();
  const location = useLocation();
  const [cartItemCount, setCartItemCount] = useState(0);
  const catalogListActive =
    location.pathname === '/' || location.pathname === '/products';

  useEffect(() => {
    async function loadCartCount() {
      if (!user) {
        setCartItemCount(0);
        return;
      }
      try {
        const response = await getCart();
        const items = response.data?.items || [];
        const count = items.reduce((sum, item) => sum + (item.quantity || 0), 0);
        setCartItemCount(count);
      } catch {
        setCartItemCount(0);
      }
    }

    loadCartCount();
  }, [user, location.pathname]);

  return (
    <header className="app-header">
      <Link className="brand-link brand-link--logo" to="/products" title="N11Lite — Ürünler">
        <img
          className="brand-logo-img"
          src="/assets/brand/logo-wide.png"
          alt="N11Lite"
          width={150}
          height={40}
        />
      </Link>

      <nav className="main-nav">
        <NavLink
          to="/products"
          className={({ isActive }) => (isActive || catalogListActive ? 'active' : undefined)}
        >
          Ürünler
        </NavLink>

        {!loading && !user && (
          <>
            <NavLink to="/login">Giriş Yap</NavLink>
            <NavLink to="/register">Kayıt Ol</NavLink>
          </>
        )}

        {!loading && user && (
          <>
            {user.role === 'ADMIN' && (
              <NavLink to="/admin/orders">Admin</NavLink>
            )}
            <NavLink to="/account/orders">Siparişlerim</NavLink>
            <NavLink to="/account/addresses">Hesabım</NavLink>
            <NavLink className="cart-nav-link" to="/cart">
              Sepet
              <span className="cart-count-dot">{cartItemCount}</span>
            </NavLink>
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
