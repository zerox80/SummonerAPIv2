import { useState, useEffect } from 'react';

export function useMobileDetection() {
  const [isMobile, setIsMobile] = useState(false);
  const [isTablet, setIsTablet] = useState(false);
  const [isTouch, setIsTouch] = useState(false);

  useEffect(() => {
    const updateDeviceDetection = () => {
      const width = window.innerWidth;
      setIsMobile(width < 768);
      setIsTablet(width >= 768 && width < 1024);

      // Check for touch capability
      setIsTouch('ontouchstart' in window || navigator.maxTouchPoints > 0);
    };

    // Initial detection
    updateDeviceDetection();

    // Listen for resize and orientation changes
    window.addEventListener('resize', updateDeviceDetection);
    window.addEventListener('orientationchange', updateDeviceDetection);

    return () => {
      window.removeEventListener('resize', updateDeviceDetection);
      window.removeEventListener('orientationchange', updateDeviceDetection);
    };
  }, []);

  return { isMobile, isTablet, isTouch };
}