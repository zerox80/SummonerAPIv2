// Theme toggle functionality
import {
  THEMES,
  BOOTSWATCH_THEMES,
  STORAGE_KEYS,
  CSS_CLASSES,
  ARIA,
  EVENTS,
  SELECTORS
} from './constants.js';

// Instance tracking using WeakMap for better memory management
const instances = new WeakMap();

// Entrypoint that wires up theming controls and returns helpers for external reuse
export function initThemeToggle(chartUpdateCallback) {
    const themeStylesheet = document.querySelector('#themeStylesheet');
    const themeToggleBtn = document.querySelector(SELECTORS.THEME_TOGGLE);
    const themeMeta = document.querySelector('#themeColor');
    const bodyEl = document.body;

    // Check if already initialized
    const instanceKey = themeToggleBtn || document.body;
    if (instances.has(instanceKey)) {
        return instances.get(instanceKey);
    }

    const BOOTSWATCH_BASE = 'https://cdnjs.cloudflare.com/ajax/libs/bootswatch/5.3.7';
    const BOOTSWATCH_SRI = {
        darkly: 'sha512-p5mHOC7N2BAy1SdoU/f2XD4t7EM05/5b1QowPXdyvqrSFsWQl3HVqY60hvvnOcMVBXBwr3ysopvGgxqbaovv/Q==',
        lux: 'sha384-ZBEFefafV90B0/GFh9OAL0VvtWyZLTa/hy4drOjpSQeKYQNJyIkw+tztFRqN5jHG'
    };

    // Core routine that switches global stylesheets, component state, and persisted theme
    function applyTheme(theme) {
        if (!themeStylesheet) {
            console.warn('Theme stylesheet element not found');
            return;
        }
        const normalizedTheme = (theme === THEMES.LIGHT || theme === THEMES.DARK) ? theme : THEMES.DARK;
        const themeName = BOOTSWATCH_THEMES[normalizedTheme] || BOOTSWATCH_THEMES.dark;
        const href = `${BOOTSWATCH_BASE}/${themeName}/bootstrap.min.css`;
        const sri = BOOTSWATCH_SRI[themeName];
        themeStylesheet.setAttribute('href', href);
        if (sri) {
            themeStylesheet.setAttribute('integrity', sri);
            themeStylesheet.setAttribute('crossorigin', 'anonymous');
        } else {
            themeStylesheet.removeAttribute('integrity');
            themeStylesheet.removeAttribute('crossorigin');
        }
        bodyEl.classList.remove('theme-dark', 'theme-light');
        bodyEl.classList.add(normalizedTheme === THEMES.LIGHT ? 'theme-light' : 'theme-dark');

        // Keep navbar colors aligned with the currently active theme
        const navEl = document.querySelector('nav.navbar');
        if (navEl) {
            navEl.classList.remove('navbar-dark','bg-dark','navbar-light','bg-light');
            if (normalizedTheme === THEMES.LIGHT) { 
                navEl.classList.add('navbar-light','bg-light'); 
            } else { 
                navEl.classList.add('navbar-dark','bg-dark'); 
            }
        }
        
        // Adjust Riot ID input text color to maintain contrast in both themes
        const riotIdInput = document.getElementById('riotId');
        if (riotIdInput) {
            riotIdInput.classList.toggle('text-light', normalizedTheme !== THEMES.LIGHT);
            riotIdInput.classList.toggle('text-dark', normalizedTheme === THEMES.LIGHT);
        }

        if (themeToggleBtn) {
            // Swap icon/ARIA metadata so assistive tech tracks the new theme state
            const nextLabel = normalizedTheme === THEMES.LIGHT ? 'Switch to dark mode' : 'Switch to light mode';
            themeToggleBtn.innerHTML = normalizedTheme === THEMES.LIGHT
                ? '<i class="fa-regular fa-moon" aria-hidden="true"></i>'
                : '<i class="fa-regular fa-sun" aria-hidden="true"></i>';
            themeToggleBtn.setAttribute('aria-label', nextLabel);
            themeToggleBtn.setAttribute('aria-pressed', normalizedTheme === THEMES.LIGHT ? 'false' : 'true');
            themeToggleBtn.setAttribute('title', nextLabel);
        }

        // Update browser UI color (mobile address bar, etc.) via meta tag
        if (themeMeta) themeMeta.setAttribute('content', normalizedTheme === THEMES.LIGHT ? '#f7f7fb' : '#0b1018');

        try {
            localStorage.setItem(STORAGE_KEYS.THEME, normalizedTheme);
        } catch (e) {
            console.debug('Unable to persist theme preference:', e);
        }

        // Notify charts to update colors after stylesheet loads
        if (chartUpdateCallback && themeStylesheet) {
            let callbackExecuted = false;

            const executeCallback = () => {
                if (callbackExecuted) return;
                callbackExecuted = true;
                try {
                    chartUpdateCallback(normalizedTheme);
                } catch (e) {
                    console.error('Chart update callback failed:', e);
                }
            };

            // Wait for CSS to be parsed before recalculating chart colors
            const onLoad = () => {
                themeStylesheet.removeEventListener(EVENTS.LOAD, onLoad);
                requestAnimationFrame(() => {
                    requestAnimationFrame(executeCallback);
                });
            };

            themeStylesheet.addEventListener(EVENTS.LOAD, onLoad, { once: true });
            setTimeout(executeCallback, 300);
        }
    }

    // Restore saved theme (or default) on next frame to avoid flash of unstyled content
    let savedTheme = THEMES.DARK;
    try {
        savedTheme = localStorage.getItem(STORAGE_KEYS.THEME) || THEMES.DARK;
    } catch {
        savedTheme = THEMES.DARK;
    }
    requestAnimationFrame(() => applyTheme(savedTheme));

    // Toggle handler flips between light/dark classes and persists selection
    themeToggleBtn?.addEventListener(EVENTS.CLICK, () => {
        const isCurrentlyLight = bodyEl.classList.contains('theme-light');
        const nextTheme = isCurrentlyLight ? THEMES.DARK : THEMES.LIGHT;
        applyTheme(nextTheme);
    });

    // Store instance
    if (!instances.has(instanceKey)) {
        instances.set(instanceKey, { applyTheme });
    }

    return { applyTheme };
}
