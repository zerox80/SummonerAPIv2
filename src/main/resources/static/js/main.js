(function(){
    const STORAGE_KEY = 'summonerapi.theme';
    const THEMES = {
        dark: {
            href: 'https://cdnjs.cloudflare.com/ajax/libs/bootswatch/5.3.7/darkly/bootstrap.min.css',
            integrity: 'sha512-p5mHOC7N2BAy1SdoU/f2XD4t7EM05/5b1QowPXdyvqrSFsWQl3HVqY60hvvnOcMVBXBwr3ysopvGgxqbaovv/Q==' ,
            bodyClass: 'theme-dark',
            metaColor: '#0b1018'
        },
        light: {
            href: 'https://cdnjs.cloudflare.com/ajax/libs/bootswatch/5.3.7/lux/bootstrap.min.css',
            integrity: 'sha384-ZBEFefafV90B0/GFh9OAL0VvtWyZLTa/hy4drOjpSQeKYQNJyIkw+tztFRqN5jHG',
            bodyClass: 'theme-light',
            metaColor: '#f7f7fb'
        }
    };

    function readStoredTheme(){
        try {
            const value = localStorage.getItem(STORAGE_KEY);
            if (value && (value === 'dark' || value === 'light')) {
                return value;
            }
        } catch (err) {
            console.debug('Unable to read stored theme', err);
        }
        return null;
    }

    function persistTheme(theme){
        try {
            localStorage.setItem(STORAGE_KEY, theme);
        } catch (err) {
            console.debug('Unable to persist theme', err);
        }
    }

    function prefersDarkMode(){
        return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    }

    function applyTheme(theme, {persist = false} = {}){
        const themeId = theme === 'light' ? 'light' : 'dark';
        const config = THEMES[themeId];
        const stylesheet = document.getElementById('themeStylesheet');
        const body = document.body;
        const toggle = document.getElementById('themeToggle');
        const meta = document.getElementById('themeColor');

        if (stylesheet) {
            stylesheet.setAttribute('href', config.href);
            stylesheet.setAttribute('integrity', config.integrity);
        }

        if (body) {
            body.classList.remove('theme-dark', 'theme-light');
            body.classList.add(config.bodyClass);
        }

        if (meta) {
            meta.setAttribute('content', config.metaColor);
        }

        if (toggle) {
            const isLight = themeId === 'light';
            const label = isLight ? 'Switch to dark theme' : 'Switch to light theme';
            toggle.setAttribute('aria-pressed', String(isLight));
            toggle.setAttribute('title', label);
            toggle.setAttribute('aria-label', label);
            const icon = toggle.querySelector('i');
            if (icon) {
                icon.classList.toggle('fa-moon', !isLight);
                icon.classList.toggle('fa-sun', isLight);
            }
        }

        document.documentElement.setAttribute('data-theme', themeId);

        if (persist) {
            persistTheme(themeId);
            persistedTheme = themeId;
        }
    }

    let persistedTheme = null;

    document.addEventListener('DOMContentLoaded', function(){
        persistedTheme = readStoredTheme();
        const initialTheme = persistedTheme || (prefersDarkMode() ? 'dark' : 'light');
        applyTheme(initialTheme);

        const toggle = document.getElementById('themeToggle');
        if (toggle) {
            toggle.addEventListener('click', function(){
                const next = (document.documentElement.getAttribute('data-theme') === 'light') ? 'dark' : 'light';
                applyTheme(next, {persist: true});
            });
        }
    });

    if (window.matchMedia) {
        const mq = window.matchMedia('(prefers-color-scheme: dark)');
        mq.addEventListener('change', function(e){
            if (!persistedTheme) {
                applyTheme(e.matches ? 'dark' : 'light');
            }
        });
    }
})();

// Autofocus on the riotId input field when the page loads
document.addEventListener('DOMContentLoaded', function() {
    const riotIdInput = document.getElementById('riotId');
    if (riotIdInput) {
        riotIdInput.focus();
    }

    // Fix any garbled separator characters in template rendering
    try {
        document.querySelectorAll('span.text-muted.mx-2').forEach(function(el){
            if (el && /\uFFFD|�/.test(el.textContent)) {
                el.textContent = '•';
            }
        });
    } catch (e) { /* safe no-op */ }
});

// Global image error fallbacks to avoid broken icons when local assets are missing
document.addEventListener('DOMContentLoaded', function(){
    try {
        // 1x1 transparent PNG (data URI) to replace broken images if we prefer not to hide
        const TRANSPARENT_PNG = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQIW2NkYGBgAAAABQABhU1aWQAAAABJRU5ErkJggg==';

        function attachImgFallback(selector, mode){
            document.querySelectorAll(selector).forEach(function(img){
                // Guard: avoid adding multiple listeners
                if (img.__fallbackBound) return;
                img.__fallbackBound = true;
                img.addEventListener('error', function(){
                    if (mode === 'hide') {
                        img.style.display = 'none';
                    } else if (mode === 'transparent') {
                        // Replace src with transparent pixel only once to avoid infinite loop
                        if (img.getAttribute('data-fallback-applied') !== '1'){
                            img.setAttribute('data-fallback-applied','1');
                            img.src = TRANSPARENT_PNG;
                        } else {
                            img.style.display = 'none';
                        }
                    }
                }, { once: false });
            });
        }

        // Local tier emblem icons may be missing; use non-breaking fallback
        attachImgFallback('.tier-icon', 'transparent');
        // Locally referenced champion icons (e.g., /ui/champions/Name.png) may be missing; hide gracefully
        attachImgFallback('.js-champ-fallback', 'hide');
    } catch (e) {
        // no-op; never break UI
    }
});

// Optional: Improve collapse icon for match history
const matchHistoryCollapse = document.getElementById('matchHistoryCollapse');
if (matchHistoryCollapse) {
    const icon = matchHistoryCollapse.previousElementSibling.querySelector('.fa-chevron-down');
    if (icon) {
        matchHistoryCollapse.addEventListener('show.bs.collapse', function () {
            icon.classList.remove('fa-chevron-down');
            icon.classList.add('fa-chevron-up');
        });
        matchHistoryCollapse.addEventListener('hide.bs.collapse', function () {
            icon.classList.remove('fa-chevron-up');
            icon.classList.add('fa-chevron-down');
        });
    }
} 

// Fallback wiring for Match History filter (All/Ranked)
// Ensures the buttons work even if inline script in index.html fails or is blocked by CSP
document.addEventListener('DOMContentLoaded', function() {
    try {
        const btnAll = document.getElementById('filterAll');
        const btnRanked = document.getElementById('filterRanked');
        if (!btnAll && !btnRanked) return; // No history controls on this page

        function isRankedQ(q){ return q === 420 || q === 440 || q === 1100; }

        function fallbackApply(mode){
            const rows = Array.from(document.querySelectorAll('.match-row'));
            const elWR = document.getElementById('summaryWR');
            const elKDA = document.getElementById('summaryKDA');
            const elCount = document.getElementById('summaryCount');
            const noMsg = document.getElementById('noMatchesMsg');
            const queueToggle = document.getElementById('queueToggle');
            const showRanked = (mode === 'ranked');

            rows.forEach(r => {
                const q = Number(r.getAttribute('data-q')) || 0;
                const show = showRanked ? isRankedQ(q) : true;
                r.style.display = show ? '' : 'none';
            });

            // Summary
            const vis = rows.filter(r => r.style.display !== 'none' && String(r.getAttribute('data-remake')) !== 'true');
            let wins=0, k=0, d=0, a=0;
            vis.forEach(r => {
                if (String(r.getAttribute('data-win')) === 'true') wins++;
                k += Number(r.getAttribute('data-k'))||0;
                d += Number(r.getAttribute('data-d'))||0;
                a += Number(r.getAttribute('data-a'))||0;
            });
            const games = vis.length;
            const wr = games ? Math.round((wins/games)*100) : 0;
            const kda = (d>0) ? (((k+a)/d).toFixed(2)) : (k+a).toFixed(2);
            if (elWR) elWR.textContent = 'WR: ' + (games ? (wr + '%') : '-');
            if (elKDA) elKDA.textContent = 'KDA: ' + (games ? kda : '-');
            if (elCount) elCount.textContent = `Last ${games} game${games===1?'':'s'}`;

            const anyVisible = vis.length > 0;
            // Default CSS hides #noMatchesMsg; explicitly set 'block' to show
            if (noMsg) noMsg.style.display = anyVisible ? 'none' : 'block';

            // Toggle visual state
            btnAll?.classList.toggle('active', mode !== 'ranked');
            btnRanked?.classList.toggle('active', mode === 'ranked');

            // Update dropdown label for consistency
            if (queueToggle) queueToggle.textContent = showRanked ? 'Ranked (any)' : 'All queues';
        }

        function setMode(mode){
            if (typeof window.setHistoryFilter === 'function') {
                window.setHistoryFilter(mode);
            } else {
                fallbackApply(mode);
            }
        }

        btnAll?.addEventListener('click', (e)=>{ e.preventDefault(); setMode('all'); });
        btnRanked?.addEventListener('click', (e)=>{ e.preventDefault(); setMode('ranked'); });

        // If the inline script hasn't installed handlers, initialize summary once
        if (typeof window.setHistoryFilter !== 'function') {
            setMode('all');
        }
    } catch (err) {
        // Never break the page if something goes wrong here
        console.error('History filter fallback error:', err);
    }
});
