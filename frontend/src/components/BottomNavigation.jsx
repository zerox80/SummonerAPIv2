import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import ThemeToggle from './ThemeToggle.jsx';
import '../styles/bottom-navigation.css';

// Icons - using simple SVG components
const SummonerIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
    <circle cx="12" cy="7" r="4"></circle>
  </svg>
);

const ChampionIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
  </svg>
);

const SearchIcon = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
    <circle cx="11" cy="11" r="8"></circle>
    <path d="m21 21-4.35-4.35"></path>
  </svg>
);

const BottomNavigation = ({ currentSection, onSearchClick }) => {
  const navigate = useNavigate();

  return (
    <nav className="bottom-nav" role="navigation" aria-label="Mobile Navigation">
      <NavLink
        to="/"
        className={({ isActive }) =>
          `bottom-nav__item ${isActive ? 'bottom-nav__item--active' : ''}`
        }
      >
        <SummonerIcon />
        <span>Summoner</span>
      </NavLink>

      <NavLink
        to="/champions"
        className={({ isActive }) =>
          `bottom-nav__item ${isActive ? 'bottom-nav__item--active' : ''}`
        }
      >
        <ChampionIcon />
        <span>Champions</span>
      </NavLink>

      <button
        className="bottom-nav__item bottom-nav__item--fab"
        onClick={onSearchClick}
        aria-label="Summoner suchen"
      >
        <SearchIcon />
      </button>

      <div className="bottom-nav__item">
        <ThemeToggle />
      </div>
    </nav>
  );
};

export default BottomNavigation;