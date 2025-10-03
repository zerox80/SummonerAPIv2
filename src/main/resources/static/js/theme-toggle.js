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

export function initThemeToggle(chartUpdateCallback) {
    const themeStylesheet = document.querySelector('#themeStylesheet');
    const themeToggleBtn = document.querySelector(SELECTORS.THEME_TOGGLE);
    const themeMeta = document.querySelector('#themeColor');
    const bodyEl = document.body;

    // Check if already initialized
    if (instances.has(themeToggleBtn || {})) {
        return instances.get(themeToggleBtn || {});
    }

    const BOOTSWATCH_BASE = 'https://cdnjs.cloudflare.com/ajax/libs/bootswatch/5.3.7';
    const BOOTSWATCH_SRI = {
        darkly: 'sha512-p5mHOC7N2BAy1SdoU/f2XD4t7EM05/5b1QowPXdyvqrSFsWQl3HVqY60hvvnOcMVBXBwr3ysopvGgxqbaovv/Q==',
        lux: 'sha384-ZBEFefafV90B0/GFh9OAL0VvtWyZLTa/hy4drOjpSQeKYQNJyIkw+tztFRqN5jHG'
    };

    function applyTheme(theme) {
        if (!themeStylesheet) {
            console.warn('Theme stylesheet element not found');
            return;
        }
        const themeName = THEMES[theme] || THEMES.dark;
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
        bodyEl.classList.add(theme === 'light' ? 'theme-light' : 'theme-dark');
        
        const navEl = document.querySelector('nav.navbar');
        if (navEl) {
            navEl.classList.remove('navbar-dark','bg-dark','navbar-light','bg-light');
            if (theme === 'light') { 
                navEl.classList.add('navbar-light','bg-light'); 
            } else { 
                navEl.classList.add('navbar-dark','bg-dark'); 
            }
        }
        
        const riotIdInput = document.getElementById('riotId');
        if (riotIdInput) {
            riotIdInput.classList.toggle('text-light', theme !== 'light');
            riotIdInput.classList.toggle('text-dark', theme === 'light');
        }
        
        if (themeToggleBtn) {
            const nextLabel = theme === 'light' ? 'Switch to dark mode' : 'Switch to light mode';
            themeToggleBtn.innerHTML = theme === 'light'
                ? '<i class="fa-regular fa-moon" aria-hidden="true"></i>'
                : '<i class="fa-regular fa-sun" aria-hidden="true"></i>';
            themeToggleBtn.setAttribute('aria-label', nextLabel);
            themeToggleBtn.setAttribute('aria-pressed', theme === 'light' ? 'false' : 'true');
            themeToggleBtn.setAttribute('title', nextLabel);
        }
        
        if (themeMeta) themeMeta.setAttribute('content', theme === 'light' ? '#f7f7fb' : '#0b1018');
        
        localStorage.setItem(STORAGE_KEYS.THEME, theme);
        
        // Notify charts to update colors after stylesheet loads
        if (chartUpdateCallback && themeStylesheet) {
            let callbackExecuted = false;

            const executeCallback = () => {
                if (callbackExecuted) return;
                callbackExecuted = true;
                try {
                    chartUpdateCallback(theme);
                } catch (e) {
                    console.error('Chart update callback failed:', e);
                }
            };

            const onLoad = () => {
                themeStylesheet.removeEventListener(EVENTS.LOAD, onLoad);
                requestAnimationFrame(() => {
                    requestAnimationFrame(executeCallback);
                });
            };

            themeStylesheet.addEventListener(EVENTS.LOAD, onLoad);
            setTimeout(executeCallback, 300);
        }
    }

    const savedTheme = localStorage.getItem(STORAGE_KEYS.THEME) || THEMES.DARK;
    requestAnimationFrame(() => applyTheme(savedTheme));

    themeToggleBtn?.addEventListener(EVENTS.CLICK, () => {
        const nextTheme = bodyEl.classList.contains(CSS_CLASSES.THEME_LIGHT) ? THEMES.DARK : THEMES.LIGHT;
        applyTheme(nextTheme);
    });

    // Store instance
    instances.set(themeToggleBtn || {}, { applyTheme });

    return { applyTheme };
}
