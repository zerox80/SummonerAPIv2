
import PropTypes from 'prop-types';
import { NavLink, useLocation } from 'react-router-dom';
import { useMemo, useState, useEffect } from 'react';
import ThemeToggle from '../components/ThemeToggle.jsx';
import BrandMark from '../components/BrandMark.jsx';
import HamburgerMenu from '../components/HamburgerMenu.jsx';
import MobileNavigation from '../components/MobileNavigation.jsx';
import BottomNavigation from '../components/BottomNavigation.jsx';
import MobileGestureArea from '../components/MobileGestureArea.jsx';
import { useMobileDetection } from '../hooks/useMobileDetection.js';
import '../styles/shell-layout.css';
import '../styles/hamburger-menu.css';
import '../styles/mobile-navigation.css';
import '../styles/bottom-navigation.css';
import '../styles/mobile-gesture-area.css';

const NAV_LINKS = [
  { to: '/', label: 'Summoner' },
  { to: '/champions', label: 'Champions' }
];

export default function ShellLayout({ children }) {
  const location = useLocation();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const { isMobile, isTablet, isTouch } = useMobileDetection();

  // Close mobile menu when switching to desktop
  useEffect(() => {
    if (!isMobile) {
      setIsMobileMenuOpen(false);
    }
  }, [isMobile]);

  const activeLabel = useMemo(() => {
    const activeLink = NAV_LINKS.find((link) => link.to === location.pathname || location.pathname.startsWith(link.to) && link.to !== '/');
    return activeLink ? activeLink.label : '';
  }, [location.pathname]);

  const handleSearchClick = () => {
    // Focus on search input if on summoner page
    const searchInput = document.querySelector('.summoner-search__input');
    if (searchInput) {
      searchInput.focus();
    } else if (location.pathname !== '/') {
      // Navigate to summoner page and focus on search
      window.location.href = '/';
    }
  };

  if (isMobile) {
    // Mobile Layout
    return (
      <div className="mobile-app-shell">
        {/* Mobile Header */}
        <header className="mobile-header glass-panel">
          <HamburgerMenu
            isOpen={isMobileMenuOpen}
            onToggle={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
          />

          <div className="mobile-header__brand">
            <BrandMark compact />
          </div>
        </header>

        {/* Mobile Navigation Slide-out */}
        <MobileNavigation
          isOpen={isMobileMenuOpen}
          onClose={() => setIsMobileMenuOpen(false)}
        />

        {/* Main Content */}
        <main className="mobile-main">
          <MobileGestureArea>
            <div className="container mobile-content">
              {children}
            </div>
          </MobileGestureArea>
        </main>

        {/* Bottom Navigation */}
        <BottomNavigation
          currentSection={activeLabel}
          onSearchClick={handleSearchClick}
        />

        {/* Mobile Footer - simplified */}
        <footer className="mobile-footer">
          <div className="container">
            <p className="mobile-footer__text">
              ❤️ für League players
            </p>
          </div>
        </footer>
      </div>
    );
  }

  // Desktop Layout (original)
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
