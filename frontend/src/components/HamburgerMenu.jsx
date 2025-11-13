import React from 'react';

const HamburgerMenu = ({ isOpen, onToggle, ...props }) => {
  return (
    <button
      className={`hamburger-menu ${isOpen ? 'hamburger-menu--open' : ''}`}
      onClick={onToggle}
      aria-label={isOpen ? 'Menü schließen' : 'Menü öffnen'}
      aria-expanded={isOpen}
      {...props}
    >
      <span className="hamburger-menu__line"></span>
      <span className="hamburger-menu__line"></span>
      <span className="hamburger-menu__line"></span>
    </button>
  );
};

export default HamburgerMenu;