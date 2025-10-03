import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import PropTypes from 'prop-types';

const ThemeContext = createContext({
  mode: 'dark',
  toggle: () => {}
});

const STORAGE_KEY = 'summonerapi-theme';
const MEDIA_QUERY = '(prefers-color-scheme: dark)';

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

export function useTheme() {
  return useContext(ThemeContext);
}
