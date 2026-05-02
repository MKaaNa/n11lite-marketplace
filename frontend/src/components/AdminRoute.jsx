import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function AdminRoute({ children }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <div className="state-message">Yükleniyor...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (user.role !== 'ADMIN') {
    return <div className="state-message error-message">Bu sayfaya erişim yetkin yok.</div>;
  }

  return children;
}
