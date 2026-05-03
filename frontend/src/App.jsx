import { Route, Routes } from 'react-router-dom';
import AppHeader from './components/AppHeader.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
import AdminRoute from './components/AdminRoute.jsx';
import ProductDetailPage from './pages/ProductDetailPage.jsx';
import ProductListPage from './pages/ProductListPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import VerifyLoginPage from './pages/VerifyLoginPage.jsx';
import CartPage from './pages/CartPage.jsx';
import AdminOrdersPage from './pages/AdminOrdersPage.jsx';
import StoreReviewsPage from './pages/StoreReviewsPage.jsx';

export default function App() {
  return (
    <>
      <AppHeader />
      <Routes>
        <Route path="/" element={<ProductListPage />} />
        <Route path="/products" element={<ProductListPage />} />
        <Route path="/stores/:storeId" element={<StoreReviewsPage />} />
        <Route path="/products/:slug" element={<ProductDetailPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/verify-login" element={<VerifyLoginPage />} />
        <Route
          path="/cart"
          element={(
            <ProtectedRoute>
              <CartPage />
            </ProtectedRoute>
          )}
        />
        <Route
          path="/admin/orders"
          element={(
            <AdminRoute>
              <AdminOrdersPage />
            </AdminRoute>
          )}
        />
      </Routes>
    </>
  );
}

