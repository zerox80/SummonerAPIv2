/**
 * A theme toggle button for switching between light and dark modes.
 *
 * <p>This module provides a button component that allows users to switch the
 * application's color scheme. It uses the `useTheme` hook to access the
 * current theme and the toggle function from the {@link module:providers/ThemeProvider}.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Toggles between light and dark modes</li>
 *   <li>Displays an icon and label for the current mode</li>
 *   <li>Includes a subtle animation on toggle</li>
 *   <li>Accessible with ARIA labels</li>
 * </ul>
 *
 * @module components/ThemeToggle
 * @author zerox80
 * @version 2.0
 */
import { useState } from 'react';
import { useTheme } from '../providers/ThemeProvider.jsx';
import '../styles/theme-toggle.css';

/**
 * Renders a button that toggles the application's color scheme.
 *
 * <p>This component displays the current theme (light or dark) and allows the
 * user to switch to the other theme by clicking the button. It uses the
 * `useTheme` hook to interact with the application's theme context.</p>
 *
 * @component
 * @returns {React.ReactElement} The rendered theme toggle button.
 *
 * @example
 * // Place the ThemeToggle component in your application's header or settings menu.
 * <header>
 *   <ThemeToggle />
 * </header>
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
