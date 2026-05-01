export default function CategoryFilter({ categories, selectedCategory, onSelectCategory }) {
  return (
    <div className="category-filter">
      <button
        type="button"
        className={!selectedCategory ? 'filter-button active' : 'filter-button'}
        onClick={() => onSelectCategory('')}
      >
        Tümü
      </button>

      {categories.map((category) => (
        <button
          type="button"
          key={category.id}
          className={selectedCategory === category.slug ? 'filter-button active' : 'filter-button'}
          onClick={() => onSelectCategory(category.slug)}
        >
          {category.name}
        </button>
      ))}
    </div>
  );
}
