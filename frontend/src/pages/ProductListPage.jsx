import { useEffect, useState } from 'react';
import { getCategories, getProducts } from '../api/catalogApi';
import CategoryFilter from '../components/CategoryFilter';
import ProductCard from '../components/ProductCard';
import SearchBar from '../components/SearchBar';

const PAGE_SIZE = 12;

export default function ProductListPage() {
  const [categories, setCategories] = useState([]);
  const [productsPage, setProductsPage] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function loadCategories() {
      try {
        const response = await getCategories();
        setCategories(response.data);
      } catch {
        setError('Categories could not be loaded.');
      }
    }

    loadCategories();
  }, []);

  useEffect(() => {
    async function loadProducts() {
      setLoading(true);
      setError('');

      try {
        const response = await getProducts({
          category: selectedCategory,
          search,
          page,
          size: PAGE_SIZE,
        });
        setProductsPage(response.data);
      } catch {
        setError('Products could not be loaded.');
      } finally {
        setLoading(false);
      }
    }

    loadProducts();
  }, [selectedCategory, search, page]);

  function handleCategoryChange(categorySlug) {
    setSelectedCategory(categorySlug);
    setPage(0);
  }

  function handleSearch(value) {
    setSearch(value);
    setPage(0);
  }

  const products = productsPage?.content || [];
  const totalPages = productsPage?.totalPages || 0;

  return (
    <main className="catalog-page">
      <section className="catalog-header">
        <div>
          <p className="eyebrow">N11Lite Marketplace</p>
          <h1>Products</h1>
        </div>
        <SearchBar initialValue={search} onSearch={handleSearch} />
      </section>

      <CategoryFilter
        categories={categories}
        selectedCategory={selectedCategory}
        onSelectCategory={handleCategoryChange}
      />

      {error && <div className="state-message error-message">{error}</div>}
      {loading && <div className="state-message">Loading products...</div>}

      {!loading && !error && products.length === 0 && (
        <div className="state-message">No products found.</div>
      )}

      {!loading && !error && products.length > 0 && (
        <>
          <div className="product-grid">
            {products.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>

          <div className="pagination">
            <button
              type="button"
              disabled={page === 0}
              onClick={() => setPage((currentPage) => currentPage - 1)}
            >
              Previous
            </button>
            <span>
              Page {productsPage.pageNumber + 1} / {totalPages}
            </span>
            <button
              type="button"
              disabled={productsPage.last}
              onClick={() => setPage((currentPage) => currentPage + 1)}
            >
              Next
            </button>
          </div>
        </>
      )}
    </main>
  );
}
