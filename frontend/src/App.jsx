import { Route, Routes } from 'react-router-dom';
import AppHeader from './components/AppHeader.jsx';
import ProductDetailPage from './pages/ProductDetailPage.jsx';
import ProductListPage from './pages/ProductListPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import VerifyLoginPage from './pages/VerifyLoginPage.jsx';

export default function App() {
  return (
    <>
      <AppHeader />
      <Routes>
        <Route path="/" element={<ProductListPage />} />
        <Route path="/products/:slug" element={<ProductDetailPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/verify-login" element={<VerifyLoginPage />} />
      </Routes>
    </>
  );
}

