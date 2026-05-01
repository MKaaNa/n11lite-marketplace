import { Route, Routes } from 'react-router-dom';
import ProductListPage from './pages/ProductListPage.jsx';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<ProductListPage />} />
    </Routes>
  );
}

