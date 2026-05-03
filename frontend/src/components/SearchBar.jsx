import { useEffect, useRef, useState } from 'react';

export default function SearchBar({ initialValue = '', onSearch, debounceMs = 320 }) {
  const [value, setValue] = useState(initialValue);
  const firstRender = useRef(true);

  useEffect(() => {
    setValue(initialValue);
  }, [initialValue]);

  useEffect(() => {
    if (firstRender.current) {
      firstRender.current = false;
      return;
    }

    const timeout = setTimeout(() => {
      onSearch(value.trim());
    }, debounceMs);

    return () => clearTimeout(timeout);
  }, [value, onSearch, debounceMs]);

  function handleSubmit(event) {
    event.preventDefault();
    onSearch(value.trim());
  }

  return (
    <form className="search-bar" onSubmit={handleSubmit}>
      <input
        type="search"
        value={value}
        placeholder="Ürün ara"
        onChange={(event) => setValue(event.target.value)}
      />
      <button type="submit">Ara</button>
    </form>
  );
}
