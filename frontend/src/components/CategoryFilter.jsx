export default function CategoryFilter({ categories, selectedCategory, onSelectCategory }) {
  const categoryIcons = {
    'digital-codes': '</>',
    electronics: '⚡',
    'home-living': '⌂',
    books: '▤',
    fashion: '✧',
  };

  return (
    <div className="category-filter">
      <button
        type="button"
        className={!selectedCategory ? 'filter-button active' : 'filter-button'}
        onClick={() => onSelectCategory('')}
      >
        <span className="filter-icon" aria-hidden="true">▦</span>
        Tümü
      </button>

      {categories.map((category) => (
        <button
          type="button"
          key={category.id}
          className={selectedCategory === category.slug ? 'filter-button active' : 'filter-button'}
          onClick={() => onSelectCategory(category.slug)}
        >
          <span className="filter-icon" aria-hidden="true">{categoryIcons[category.slug] || '•'}</span>
          {category.name}
        </button>
      ))}
    </div>
  );
}
