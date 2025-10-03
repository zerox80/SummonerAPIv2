/**
 * Fallback history filter functionality for match filtering
 * Ensures filters work even if main implementation fails
 */
import {
    QUEUE_TYPES,
    SELECTORS,
    EVENTS,
    CSS_CLASSES,
    ARIA
} from './constants.js';

// Provide a resilient alternative when the main history filter script fails to load
export function initHistoryFiltersFallback() {
    try {
        const btnAll = document.querySelector(`#filterAll`);
        const btnRanked = document.querySelector(`#filterRanked`);
        if (!btnAll && !btnRanked) return; // No history controls on this page

        function isRankedQ(q) {
            return q === QUEUE_TYPES.RANKED_SOLO ||
                   q === QUEUE_TYPES.RANKED_FLEX ||
                   q === QUEUE_TYPES.TFT_RANKED;
        }

        let currentMode = 'all';

        // Simplified filtering routine used when the primary React/SPA logic isn't available
        function fallbackApply(mode) {
            const rows = Array.from(document.querySelectorAll('.match-row'));
            const elWR = document.querySelector(SELECTORS.SUMMARY_WR);
            const elKDA = document.querySelector(SELECTORS.SUMMARY_KDA);
            const elCount = document.querySelector(SELECTORS.SUMMARY_COUNT);
            const noMsg = document.querySelector(SELECTORS.NO_MATCHES_MSG);
            const queueToggle = document.querySelector(SELECTORS.QUEUE_TOGGLE);
            const queueDropdown = document.querySelector(SELECTORS.QUEUE_DROPDOWN);
            const showRanked = (mode === 'ranked');

            // Reset queue dropdown state
            if (queueDropdown) {
                queueDropdown.querySelectorAll('button[data-q]').forEach(btn => {
                    btn.classList.remove(CSS_CLASSES.ACTIVE);
                });
            }

            // Toggle individual matches by queue type while avoiding layout thrash
            rows.forEach(r => {
                const q = Number(r.getAttribute('data-q')) || 0;
                const show = showRanked ? isRankedQ(q) : true;
                r.style.display = show ? '' : 'none';
            });

            // Summary
            // Aggregate stats excluding remakes to match main app logic
            const vis = rows.filter(r => r.style.display !== 'none' && String(r.getAttribute('data-remake')) !== 'true');
            let wins = 0, k = 0, d = 0, a = 0;
            vis.forEach(r => {
                if (String(r.getAttribute('data-win')) === 'true') wins++;
                k += Number(r.getAttribute('data-k')) || 0;
                d += Number(r.getAttribute('data-d')) || 0;
                a += Number(r.getAttribute('data-a')) || 0;
            });
            const games = vis.length;
            const wr = games ? Math.round((wins/games) * 100) : 0;
            // Improved KDA calculation: show "Perfect" for 0 deaths with kills/assists
            let kda;
            if (d === 0 && (k > 0 || a > 0)) {
                kda = 'Perfect';
            } else if (d === 0) {
                kda = '0.00';
            } else {
                kda = ((k + a) / d).toFixed(2);
            }
            if (elWR) elWR.textContent = 'WR: ' + (games ? (wr + '%') : '-');
            if (elKDA) elKDA.textContent = 'KDA: ' + (games ? kda : '-');
            if (elCount) elCount.textContent = `Last ${games} game${games === 1 ? '' : 's'}`;

            const anyVisible = vis.length > 0;
            // Explicitly set 'block' to show message
            if (noMsg) noMsg.style.display = anyVisible ? 'none' : 'block';

            // Toggle visual state
            btnAll?.classList.toggle(CSS_CLASSES.ACTIVE, mode !== 'ranked');
            btnRanked?.classList.toggle(CSS_CLASSES.ACTIVE, mode === 'ranked');

            // Update dropdown label for consistency
            if (queueToggle) queueToggle.textContent = showRanked ? 'Ranked (any)' : 'All queues';

            // Sync with match-filters if available
            // The presence of `window.setHistoryFilter` indicates the primary logic is active
            if (typeof window.setHistoryFilter === 'function') {
                try {
                    window.__fallbackApplied = true;
                } catch(e) {}
            }
        }

        function syncPrimaryHandler(mode) {
            if (typeof window.setHistoryFilter === 'function') {
                try {
                    window.setHistoryFilter(mode);
                } catch (err) {
                    console.error('Primary history filter handler failed:', err);
                }
            }
        }

        // Proxy filters to main handler if available; otherwise use local fallback
        function setMode(mode) {
            currentMode = mode === 'ranked' ? 'ranked' : 'all';
            window.__historyFallbackMode = currentMode;
            if (typeof window.setHistoryFilter === 'function') {
                syncPrimaryHandler(currentMode);
            } else {
                fallbackApply(currentMode);
            }
        }

        window.addEventListener('historyFilters:primaryReady', () => {
            if (window.__historyFallbackMode && typeof window.setHistoryFilter === 'function') {
                syncPrimaryHandler(window.__historyFallbackMode);
            }
        });

        // Wire buttons so UI stays interactive even without the advanced script
        btnAll?.addEventListener(EVENTS.CLICK, (e) => {
            e.preventDefault();
            setMode('all');
        });
        btnRanked?.addEventListener(EVENTS.CLICK, (e) => {
            e.preventDefault();
            setMode('ranked');
        });

        if (typeof window.setHistoryFilter !== 'function') {
            setMode('all');
        } else if (window.__historyFallbackMode) {
            syncPrimaryHandler(window.__historyFallbackMode);
        }

        // If the inline script hasn't installed handlers, initialize summary once
        if (typeof window.setHistoryFilter !== 'function') {
            fallbackApply(currentMode);
        }
    } catch (err) {
        // Never break the page if something goes wrong here
        console.error('History filter fallback error:', err);
    }
}
