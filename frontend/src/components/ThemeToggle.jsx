import { useState } from 'react';
import { useTheme } from '../providers/ThemeProvider.jsx';
import '../styles/theme-toggle.css';

/**
 * A button that toggles the application's theme.
 *
 * @returns {React.ReactElement} The rendered component.
 */
export default function ThemeToggle() {
  const { mode, toggle } = useTheme();
  const [isAnimating, setAnimating] = useState(false);

  const handleToggle = () => {
    setAnimating(true);
    toggle();
  };

  return (
    <button
      type="button"
      className={`theme-toggle${isAnimating ? ' theme-toggle--pulse' : ''}`}
      onClick={handleToggle}
      onAnimationEnd={() => setAnimating(false)}
      aria-label={mode === 'dark' ? 'Zu hell wechseln' : 'Zu dunkel wechseln'}
    >
      <span className="theme-toggle__icon" aria-hidden>
        {mode === 'dark' ? '🌙' : '☀️'}
      </span>
      <span className="theme-toggle__label">{mode === 'dark' ? 'Dark' : 'Light'}</span>
    </button>
  );
}
