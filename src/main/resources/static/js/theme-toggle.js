// Theme toggle functionality
export function initThemeToggle(chartUpdateCallback) {
    const themeStylesheet = document.getElementById('themeStylesheet');
    const themeToggleBtn = document.getElementById('themeToggle');
    const bodyEl = document.body;
    const THEMES = { dark: 'darkly', light: 'lux' };
    const BOOTSWATCH_BASE = 'https://cdnjs.cloudflare.com/ajax/libs/bootswatch/5.3.7';
    const BOOTSWATCH_SRI = {
        darkly: 'sha512-p5mHOC7N2BAy1SdoU/f2XD4t7EM05/5b1QowPXdyvqrSFsWQl3HVqY60hvvnOcMVBXBwr3ysopvGgxqbaovv/Q==',
        lux: 'sha384-ZBEFefafV90B0/GFh9OAL0VvtWyZLTa/hy4drOjpSQeKYQNJyIkw+tztFRqN5jHG'
    };

    function applyTheme(theme) {
        const themeName = THEMES[theme] || THEMES.dark;
        const href = `${BOOTSWATCH_BASE}/${themeName}/bootstrap.min.css`;
        const sri = BOOTSWATCH_SRI[themeName];
        themeStylesheet.href = href;
        if (sri) {
            themeStylesheet.setAttribute('integrity', sri);
            themeStylesheet.setAttribute('crossorigin', 'anonymous');
        } else {
            themeStylesheet.removeAttribute('integrity');
            themeStylesheet.setAttribute('crossorigin', 'anonymous');
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
        
        const themeMeta = document.getElementById('themeColor');
        if (themeMeta) themeMeta.setAttribute('content', theme === 'light' ? '#f7f7fb' : '#0b1018');
        
        localStorage.setItem('theme', theme);
        
        // Notify charts to update colors after stylesheet loads
        if (chartUpdateCallback) {
            let callbackExecuted = false;
            let loadListenerFired = false;
            let timeoutFired = false;
            
            const executeCallback = () => {
                if (callbackExecuted) return;
                // Only execute if both triggers have attempted (or timeout passed)
                if (!loadListenerFired && !timeoutFired) return;
                callbackExecuted = true;
                try {
                    chartUpdateCallback(theme);
                } catch(e) {
                    console.error('Chart update callback failed:', e);
                }
            };
            
            // Wait for stylesheet to load and apply
            themeStylesheet.addEventListener('load', () => {
                loadListenerFired = true;
                requestAnimationFrame(() => {
                    requestAnimationFrame(() => {
                        executeCallback();
                    });
                });
            }, { once: true });
            
            // Fallback timeout in case load event doesn't fire
            setTimeout(() => {
                timeoutFired = true;
                executeCallback();
            }, 300);
        }
    }

    const savedTheme = localStorage.getItem('theme') || 'dark';
    applyTheme(savedTheme);
    
    themeToggleBtn?.addEventListener('click', () => {
        const nextTheme = bodyEl.classList.contains('theme-light') ? 'dark' : 'light';
        applyTheme(nextTheme);
    });
    
    return { applyTheme };
}
