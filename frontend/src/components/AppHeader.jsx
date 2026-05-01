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
        <NavLink to="/">Products</NavLink>

        {!loading && !user && (
          <>
            <NavLink to="/login">Login</NavLink>
            <NavLink to="/register">Register</NavLink>
          </>
        )}

        {!loading && user && (
          <>
            <NavLink to="/cart">Cart</NavLink>
            <div className="user-menu">
              <span>{user.fullName || user.email}</span>
              <button type="button" onClick={logout}>
                Logout
              </button>
            </div>
          </>
        )}
      </nav>
    </header>
  );
}
