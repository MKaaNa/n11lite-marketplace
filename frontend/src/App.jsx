import { Route, Routes } from 'react-router-dom';
import ProductDetailPage from './pages/ProductDetailPage.jsx';
import ProductListPage from './pages/ProductListPage.jsx';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<ProductListPage />} />
      <Route path="/products/:slug" element={<ProductDetailPage />} />
    </Routes>
  );
}

