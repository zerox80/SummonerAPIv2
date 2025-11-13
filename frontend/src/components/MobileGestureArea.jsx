import React from 'react';
import { useSwipeNavigation } from '../hooks/useSwipeNavigation.js';
import '../styles/mobile-gesture-area.css';

const MobileGestureArea = ({ children }) => {
  const { handleTouchStart } = useSwipeNavigation();

  return (
    <div
      className="mobile-gesture-area"
      onTouchStart={handleTouchStart}
      role="application"
      aria-label="Wischbereich für Navigation"
    >
      {children}
    </div>
  );
};

export default MobileGestureArea;