import { useCallback, useEffect, useState } from 'react';
import { getCategories, getProducts } from '../api/catalogApi';
import CategoryFilter from '../components/CategoryFilter';
import ProductCard from '../components/ProductCard';
import SearchBar from '../components/SearchBar';

const PAGE_SIZE = 15;
const SKELETON_COUNT = 15;

/** 0-based page index; max 5 page buttons in the sliding window. */
function getVisiblePageIndices(currentPage, totalPages, maxButtons = 5) {
  if (totalPages <= 0) return [];
  const span = Math.min(maxButtons, totalPages);
  let start = Math.max(0, currentPage - Math.floor(maxButtons / 2));
  let end = start + span;
  if (end > totalPages) {
    end = totalPages;
    start = Math.max(0, end - span);
  }
  return Array.from({ length: end - start }, (_, i) => start + i);
}

export default function ProductListPage() {
  const [categories, setCategories] = useState([]);
  const [productsPage, setProductsPage] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [search, setSearch] = useState('');
  const [sort, setSort] = useState('recommended');
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function loadCategories() {
      try {
        const response = await getCategories();
        setCategories(response.data);
      } catch {
        setError('Kategoriler yüklenemedi.');
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
          sort,
        });
        setProductsPage(response.data);
      } catch {
        setError('Ürünler yüklenemedi.');
      } finally {
        setLoading(false);
      }
    }

    loadProducts();
  }, [selectedCategory, search, page, sort]);

  const handleCategoryChange = useCallback((categorySlug) => {
    setSelectedCategory(categorySlug);
    setPage(0);
  }, []);

  const handleSearch = useCallback((value) => {
    setSearch(value);
    setPage(0);
  }, []);

  const products = productsPage?.content || [];
  const totalPages = productsPage?.totalPages || 0;
  const pageNumbers = getVisiblePageIndices(page, totalPages, 5);

  const handleSortChange = useCallback((event) => {
    setSort(event.target.value);
    setPage(0);
  }, []);

  return (
    <main>
      <section className="marketplace-hero">
        <div className="hero-inner">
          <div className="hero-copy">
            <p className="eyebrow">N11Lite Marketplace</p>
            <h1>Dijitalden fiziğe, aradığın her şey N11Lite’ta!</h1>
            <p>
              Güvenli alışveriş, avantajlı fiyatlar ve hızlı teslimat ile demo pazar yeri
              deneyimini uçtan uca keşfet.
            </p>
          </div>

          <div className="hero-commerce">
            <SearchBar initialValue={search} onSearch={handleSearch} debounceMs={320} />
            <div className="hero-highlights" aria-label="Pazar yeri avantajları">
              <span><strong>Güvenli alışveriş</strong><small>3D Secure ile koruma</small></span>
              <span><strong>Hızlı teslimat</strong><small>Siparişler kapına gelsin</small></span>
              <span><strong>Kupon fırsatları</strong><small>Sepette indirimleri yakala</small></span>
            </div>
          </div>

          <div className="hero-visual" aria-hidden="true">
            <div className="hero-bag">N11Lite</div>
            <div className="hero-gift hero-gift-left" />
            <div className="hero-gift hero-gift-right" />
          </div>
        </div>
      </section>

      <section className="catalog-page">
        <div className="catalog-toolbar">
          <CategoryFilter
            categories={categories}
            selectedCategory={selectedCategory}
            onSelectCategory={handleCategoryChange}
          />
          <select
            className="sort-select"
            value={sort}
            onChange={handleSortChange}
            aria-label="Sıralama"
          >
            <option value="recommended">Önerilen</option>
            <option value="price_asc">Fiyat artan</option>
            <option value="price_desc">Fiyat azalan</option>
            <option value="newest">En yeni</option>
            <option value="best_selling">Çok satan</option>
          </select>
        </div>

        <div className="section-heading">
          <h2>Öne Çıkan Ürünler</h2>
          {productsPage && <span>{productsPage.totalElements} ürün</span>}
        </div>

      {error && <div className="alert alert--error">{error}</div>}
      {loading && (
        <div className="product-grid product-grid--skeleton" aria-label="Ürünler yükleniyor">
          {Array.from({ length: SKELETON_COUNT }).map((_, index) => (
            <article key={`skeleton-${index}`} className="product-card product-card--skeleton" aria-hidden="true">
              <div className="skeleton-image" />
              <div className="product-card-body">
                <div className="skeleton-line skeleton-line--sm" />
                <div className="skeleton-line" />
                <div className="skeleton-line skeleton-line--md" />
                <div className="skeleton-line skeleton-line--price" />
                <div className="skeleton-line skeleton-line--sm" />
                <div className="skeleton-button" />
              </div>
            </article>
          ))}
        </div>
      )}

      {!loading && !error && products.length === 0 && (
        <div className="alert alert--info catalog-empty-state">
          <img
            className="empty-state-visual"
            src="/assets/brand/illus-empty.png"
            alt=""
            decoding="async"
          />
          <p>Ürün bulunamadı.</p>
        </div>
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
              Önceki
            </button>
            {pageNumbers.map((pageIndex) => (
              <button
                type="button"
                key={pageIndex}
                className={page === pageIndex ? 'page-number active' : 'page-number'}
                onClick={() => setPage(pageIndex)}
              >
                {pageIndex + 1}
              </button>
            ))}
            <button
              type="button"
              disabled={productsPage.last || products.length < PAGE_SIZE}
              onClick={() => setPage((currentPage) => currentPage + 1)}
            >
              Sonraki
            </button>
          </div>
        </>
      )}
      </section>
    </main>
  );
}
