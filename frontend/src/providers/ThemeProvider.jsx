/**
 * Theme provider and custom hook for managing application-wide color schemes.
 *
 * <p>This module provides a React context-based theme management system that allows
 * users to toggle between light and dark modes. The theme preference is persisted
 * in local storage and defaults to the user's system preference.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Light and dark mode support</li>
 *   <li>Persistence of theme preference in local storage</li>
 *   <li>Automatic detection of system color scheme preference</li>
 *   <li>Custom hook for easy access to theme state and toggle function</li>
 * </ul>
 *
 * @module providers/ThemeProvider
 * @author zerox80
 * @version 2.0
 */
import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import PropTypes from 'prop-types';

const ThemeContext = createContext({
  mode: 'dark',
  toggle: () => {}
});

const STORAGE_KEY = 'summonerapi-theme';
const MEDIA_QUERY = '(prefers-color-scheme: dark)';

/**
 * Determines the initial theme mode by checking local storage and system preferences.
 *
 * <p>This function retrieves the theme mode from local storage if available.
 * If not, it falls back to the user's operating system preference for dark mode.
 * This ensures a consistent theme experience across sessions and devices.</p>
 *
 * @returns {('light'|'dark')} The initial theme mode ('light' or 'dark').
 */
function getInitialMode() {
  if (typeof window === 'undefined') {
    return 'dark';
  }
  const stored = window.localStorage.getItem(STORAGE_KEY);
  if (stored === 'light' || stored === 'dark') {
    return stored;
  }
  return window.matchMedia(MEDIA_QUERY).matches ? 'dark' : 'light';
}

/**
 * Provides theme state and a toggle function to its children via React context.
 *
 * <p>This component wraps the application and manages the current theme mode (light/dark).
 * It applies the corresponding CSS class to the root HTML element and persists the
 * theme preference to local storage. It also listens for changes in the system's
 * color scheme preference.</p>
 *
 * @param {object} props - The component props.
 * @param {React.ReactNode} props.children - The child components to be rendered within the provider.
 * @returns {React.ReactElement} The rendered provider component.
 *
 * @example
 * // In your main application file (e.g., App.jsx)
 * import ThemeProvider from './providers/ThemeProvider';
 *
 * function App() {
 *   return (
 *     <ThemeProvider>
 *       <YourAppContent />
 *     </ThemeProvider>
 *   );
 * }
 */
export default function ThemeProvider({ children }) {
  const [mode, setMode] = useState(getInitialMode);

  useEffect(() => {
    const root = document.documentElement;
    root.classList.remove('light', 'dark');
    root.classList.add(mode);
    window.localStorage.setItem(STORAGE_KEY, mode);
  }, [mode]);

  useEffect(() => {
    const media = window.matchMedia(MEDIA_QUERY);
    const handler = (event) => {
      setMode(event.matches ? 'dark' : 'light');
    };
    media.addEventListener('change', handler);
    return () => media.removeEventListener('change', handler);
  }, []);

  const value = useMemo(() => ({
    mode,
    toggle: () => setMode((prev) => (prev === 'dark' ? 'light' : 'dark'))
  }), [mode]);

  return (
    <ThemeContext.Provider value={value}>
      {children}
    </ThemeContext.Provider>
  );
}

ThemeProvider.propTypes = {
  children: PropTypes.node.isRequired
};

/**
 * Custom hook for accessing the current theme and toggle function.
 *
 * <p>This hook provides a convenient way for components to access the current
 * theme mode ('light' or 'dark') and the function to toggle between them.
 * It must be used within a component that is a descendant of {@link ThemeProvider}.</p>
 *
 * @returns {{mode: ('light'|'dark'), toggle: Function}} An object containing the current theme mode and the toggle function.
 * @throws {Error} If used outside of a ThemeProvider.
 *
 * @example
 * // In a component that needs theme access
 * import { useTheme } from './providers/ThemeProvider';
 *
 * function ThemeToggleButton() {
 *   const { mode, toggle } = useTheme();
 *   return (
 *     <button onClick={toggle}>
 *       Switch to {mode === 'light' ? 'Dark' : 'Light'} Mode
 *     </button>
 *   );
 * }
 */
export function useTheme() {
  return useContext(ThemeContext);
}
