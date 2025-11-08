/**
 * The main shell layout for the application.
 *
 * <p>This module provides the main layout structure for the application, including
 * the header, main content area, and footer. It also includes the primary
 * navigation and theme toggle button.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Consistent layout across all pages</li>
 *   <li>Primary navigation with active link highlighting</li>
 *   <li>Includes brand mark and theme toggle</li>
 *   <li>Responsive design for different screen sizes</li>
 * </ul>
 *
 * @module layouts/ShellLayout
 * @author zerox80
 * @version 2.0
 */
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

/**
 * Renders the main shell layout for the application, including the header, navigation, content area, and footer.
 *
 * <p>This component wraps the page content and provides a consistent structure
 * for the entire application. It includes the top navigation bar with links to
 * the main sections of the site, the brand mark, and the theme toggle button.
 * The main content of the current page is rendered as children of this component.</p>
 *
 * @component
 * @param {object} props - The component props.
 * @param {React.ReactNode} props.children - The page content to render within the layout.
 * @returns {React.ReactElement} The rendered shell layout component.
 *
 * @example
 * // In your router setup
 * <Routes>
 *   <Route element={<ShellLayout />}>
 *     <Route path="/" element={<SummonerPage />} />
 *     <Route path="/champions" element={<ChampionsPage />} />
 *   </Route>
 * </Routes>
 */
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
