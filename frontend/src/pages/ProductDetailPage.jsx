import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { addToCart } from '../api/cartApi';
import { getProductBySlug } from '../api/catalogApi';
import { useAuth } from '../context/AuthContext';

const FALLBACK_IMAGE = 'https://placehold.co/600x600?text=N11Lite';

function formatPrice(price) {
  return new Intl.NumberFormat('tr-TR', {
    style: 'currency',
    currency: 'TRY',
  }).format(price);
}

export default function ProductDetailPage() {
  const { slug } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [product, setProduct] = useState(null);
  const [selectedImage, setSelectedImage] = useState(FALLBACK_IMAGE);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [cartLoading, setCartLoading] = useState(false);
  const [error, setError] = useState('');
  const [cartMessage, setCartMessage] = useState('');

  useEffect(() => {
    async function loadProduct() {
      setLoading(true);
      setError('');

      try {
        const response = await getProductBySlug(slug);
        const productData = response.data;
        const firstImage = productData.images?.[0]?.imageUrl || FALLBACK_IMAGE;

        setProduct(productData);
        setSelectedImage(firstImage);
        setQuantity(1);
      } catch {
        setProduct(null);
        setError('Ürün bulunamadı.');
      } finally {
        setLoading(false);
      }
    }

    loadProduct();
  }, [slug]);

  function decreaseQuantity() {
    setQuantity((currentQuantity) => Math.max(1, currentQuantity - 1));
  }

  function increaseQuantity() {
    setQuantity((currentQuantity) => {
      if (product?.stock) {
        return Math.min(product.stock, currentQuantity + 1);
      }

      return currentQuantity + 1;
    });
  }

  async function handleAddToCart() {
    setCartMessage('');
    setError('');

    if (!user) {
      navigate('/login', {
        state: {
          message: 'Sepete ürün eklemek için önce giriş yapmalısınız.',
        },
      });
      return;
    }

    try {
      setCartLoading(true);
      await addToCart(product.id, quantity);
      setCartMessage('Ürün sepete eklendi.');
    } catch {
      setError('Ürün sepete eklenemedi.');
    } finally {
      setCartLoading(false);
    }
  }

  const images = product?.images?.length ? product.images : [
    { id: 'fallback', imageUrl: FALLBACK_IMAGE, displayOrder: 1 },
  ];

  return (
    <main className="catalog-page">
      <Link className="back-link" to="/">
        Ürünlere dön
      </Link>

      {loading && <div className="state-message">Ürün yükleniyor...</div>}
      {error && <div className="state-message error-message">{error}</div>}
      {cartMessage && <div className="state-message success-message">{cartMessage}</div>}

      {!loading && !error && product && (
        <section className="product-detail">
          <div className="gallery">
            <div className="main-image-wrap">
              {product.badge && <span className="product-badge">{product.badge}</span>}
              <img src={selectedImage} alt={product.name} className="main-image" />
            </div>

            <div className="thumbnail-list">
              {images.map((image) => (
                <button
                  type="button"
                  key={image.id}
                  className={selectedImage === image.imageUrl ? 'thumbnail active' : 'thumbnail'}
                  onClick={() => setSelectedImage(image.imageUrl)}
                >
                  <img src={image.imageUrl} alt={`${product.name} ${image.displayOrder}`} />
                </button>
              ))}
            </div>
          </div>

          <div className="detail-info">
            <p className="product-category">{product.category?.name}</p>
            <h1>{product.name}</h1>
            <p className="detail-price">{formatPrice(product.price)}</p>
            <p className="detail-description">{product.description}</p>

            <div className="detail-meta">
              <span>Stok: {product.stock}</span>
              <span>Satılan: {product.soldCount}</span>
              <span>Görüntülenme: {product.viewCount}</span>
            </div>

            <div className="add-cart-panel">
              <div className="quantity-controls">
                <button type="button" onClick={decreaseQuantity} disabled={quantity <= 1}>
                  -
                </button>
                <span>{quantity}</span>
                <button
                  type="button"
                  onClick={increaseQuantity}
                  disabled={product.stock && quantity >= product.stock}
                >
                  +
                </button>
              </div>
              <button
                type="button"
                className="primary-button"
                onClick={handleAddToCart}
                disabled={cartLoading || product.stock <= 0}
              >
                {cartLoading ? 'Ekleniyor...' : 'Sepete Ekle'}
              </button>
            </div>

            <div className="store-panel">
              <p className="store-label">Mağaza</p>
              <p className="store-name">
                {product.store?.name}
                {product.store?.official && <span className="official-store">Resmi Mağaza</span>}
              </p>
              {product.store?.rating && <p className="store-rating">Puan: {product.store.rating}</p>}
            </div>
          </div>
        </section>
      )}
    </main>
  );
}
