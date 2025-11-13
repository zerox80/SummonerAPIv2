import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';

export function useSwipeNavigation() {
  const navigate = useNavigate();

  const handleTouchStart = useCallback((e) => {
    const touch = e.touches[0];
    const startX = touch.clientX;
    const startY = touch.clientY;

    const handleTouchEnd = (e) => {
      const touch = e.changedTouches[0];
      const endX = touch.clientX;
      const endY = touch.clientY;

      const deltaX = endX - startX;
      const deltaY = endY - startY;

      // Only consider horizontal swipes with minimal vertical movement
      if (Math.abs(deltaX) > 50 && Math.abs(deltaY) < 100) {
        if (deltaX > 0) {
          // Swipe right - go to summoner page
          navigate('/');
        } else {
          // Swipe left - go to champions page
          navigate('/champions');
        }
      }

      // Clean up event listeners
      document.removeEventListener('touchend', handleTouchEnd);
    };

    document.addEventListener('touchend', handleTouchEnd, { passive: true });
  }, [navigate]);

  return { handleTouchStart };
}