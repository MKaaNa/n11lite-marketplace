import { createContext, useCallback, useContext, useMemo, useState } from 'react';

const ToastContext = createContext(null);

let toastCounter = 0;

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const removeToast = useCallback((id) => {
    setToasts((currentToasts) => currentToasts.filter((toast) => toast.id !== id));
  }, []);

  const showToast = useCallback((message, type = 'success', duration = 2800) => {
    const id = `toast-${Date.now()}-${toastCounter += 1}`;
    setToasts((currentToasts) => [...currentToasts, { id, message, type }]);

    window.setTimeout(() => {
      setToasts((currentToasts) => currentToasts.filter((toast) => toast.id !== id));
    }, duration);
  }, []);

  const value = useMemo(() => ({ showToast }), [showToast]);

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="toast-viewport" aria-live="polite" aria-label="Bildirimler">
        {toasts.map((toast) => (
          <div key={toast.id} className={`toast toast--${toast.type}`} role="status">
            <span>{toast.message}</span>
            <button type="button" onClick={() => removeToast(toast.id)} aria-label="Bildirimi kapat">
              ×
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within ToastProvider');
  }
  return context;
}
