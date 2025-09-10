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
            const vis = rows.filter(r => r.style.display !== 'none');
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
