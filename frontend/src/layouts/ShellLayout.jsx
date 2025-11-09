
import PropTypes from 'prop-types';
import { NavLink, useLocation } from 'react-router-dom';
import { useMemo } from 'react';
import ThemeToggle from '../components/ThemeToggle.jsx';
import BrandMark from '../components/BrandMark.jsx';
import '../styles/shell-layout.css';

const NAV_LINKS = [
  { to: '/', label: 'Summoner' },
  { to: '/champions', label: 'Champions' }
];


export default function ShellLayout({ children }) {
  const location = useLocation();
  const activeLabel = useMemo(() => {
    const activeLink = NAV_LINKS.find((link) => link.to === location.pathname || location.pathname.startsWith(link.to) && link.to !== '/');
    return activeLink ? activeLink.label : '';
  }, [location.pathname]);

  return (
    <div className="app-shell">
      <header className="app-shell__top">
        <div className="container app-shell__toolbar glass-panel">
          <BrandMark />
          <nav className="app-shell__nav" aria-label="Hauptnavigation">
            {NAV_LINKS.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                className={({ isActive }) => `app-shell__nav-link${isActive ? ' app-shell__nav-link--active' : ''}`}
              >
                <span>{link.label}</span>
              </NavLink>
            ))}
          </nav>
          <div className="app-shell__actions">
            {activeLabel && <span className="app-shell__context gradient-text">{activeLabel}</span>}
            <ThemeToggle />
          </div>
        </div>
      </header>
      <main className="app-shell__main">
        <div className="container app-shell__content">
          {children}
        </div>
      </main>
      <footer className="app-shell__footer">
        <div className="container">
          <p>
            Designed with ❤️ for League players. Daten von Riot Games. Nicht offiziell mit Riot Games verbunden.
          </p>
        </div>
      </footer>
    </div>
  );
}

ShellLayout.propTypes = {
  children: PropTypes.node.isRequired
};
