import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import HamburgerMenu from './HamburgerMenu.jsx';
import BrandMark from './BrandMark.jsx';
import '../styles/mobile-navigation.css';

const NAV_LINKS = [
  { to: '/', label: 'Summoner Suche', icon: '👤' },
  { to: '/champions', label: 'Champion Übersicht', icon: '⭐' },
];

const CloseIcon = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <line x1="18" y1="6" x2="6" y2="18"></line>
    <line x1="6" y1="6" x2="18" y2="18"></line>
  </svg>
);

const MobileNavigation = ({ isOpen, onClose }) => {
  const location = useLocation();

  // Close menu when route changes
  React.useEffect(() => {
    if (isOpen) {
      onClose();
    }
  }, [location.pathname, isOpen, onClose]);

  // Prevent body scroll when menu is open
  React.useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }

    return () => {
      document.body.style.overflow = '';
    };
  }, [isOpen]);

  // Close on escape key
  React.useEffect(() => {
    const handleEscape = (e) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };

    document.addEventListener('keydown', handleEscape);
    return () => document.removeEventListener('keydown', handleEscape);
  }, [isOpen, onClose]);

  return (
    <div className={`mobile-nav ${isOpen ? 'mobile-nav--open' : ''}`}>
      {/* Overlay */}
      <div
        className="mobile-nav__overlay"
        onClick={onClose}
        aria-hidden="true"
      />

      {/* Navigation Panel */}
      <div className="mobile-nav__panel" role="dialog" aria-modal="true">
        {/* Panel Header */}
        <div className="mobile-nav__header">
          <BrandMark />
          <button
            onClick={onClose}
            className="mobile-nav__close"
            aria-label="Menü schließen"
          >
            <CloseIcon />
          </button>
        </div>

        {/* Navigation Items */}
        <nav className="mobile-nav__menu" aria-label="Mobile Hauptnavigation">
          {NAV_LINKS.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              className={({ isActive }) =>
                `mobile-nav__link ${isActive ? 'mobile-nav__link--active' : ''}`
              }
              onClick={onClose}
            >
              <span className="mobile-nav__link-icon">{link.icon}</span>
              <span className="mobile-nav__link-label">{link.label}</span>
            </NavLink>
          ))}
        </nav>

        {/* Additional Actions */}
        <div className="mobile-nav__actions">
          <div className="mobile-nav__section">
            <h3 className="mobile-nav__section-title">Hilfe & Info</h3>
            <a
              href="https://developer.riotgames.com/"
              target="_blank"
              rel="noopener noreferrer"
              className="mobile-nav__link"
            >
              <span className="mobile-nav__link-icon">🔗</span>
              <span className="mobile-nav__link-label">Riot Games API</span>
            </a>
            <button className="mobile-nav__link" onClick={onClose}>
              <span className="mobile-nav__link-icon">💡</span>
              <span className="mobile-nav__link-label">App Guide</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MobileNavigation;