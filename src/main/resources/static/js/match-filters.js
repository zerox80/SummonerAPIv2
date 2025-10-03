// Match filtering + summary widget
import {
  DEBOUNCE_DELAYS,
  QUEUE_TYPES,
  QUEUE_NAMES,
  CSS_CLASSES,
  ARIA,
  EVENTS,
  SELECTORS
} from './constants.js';

// Instance tracking using WeakMap for better memory management
const instances = new WeakMap();
const RANKED_SENTINEL = Symbol('ranked-any');

function isRankedQueue(queueId) {
    return queueId === QUEUE_TYPES.RANKED_SOLO ||
        queueId === QUEUE_TYPES.RANKED_FLEX ||
        queueId === QUEUE_TYPES.TFT_RANKED;
}

export function initMatchFilters() {
    let rows = Array.from(document.querySelectorAll('.match-row'));
    const filters = document.querySelector(SELECTORS.HISTORY_FILTERS);
    const searchInput = document.querySelector(SELECTORS.MATCH_SEARCH);
    const elWR = document.querySelector(SELECTORS.SUMMARY_WR);
    const elKDA = document.querySelector(SELECTORS.SUMMARY_KDA);
    const elCount = document.querySelector(SELECTORS.SUMMARY_COUNT);
    const queueToggle = document.querySelector(SELECTORS.QUEUE_TOGGLE);
    const queueDropdown = document.querySelector(SELECTORS.QUEUE_DROPDOWN);
    const noMsg = document.querySelector(SELECTORS.NO_MATCHES_MSG);
    if (!rows.length && noMsg) { noMsg.style.display = ''; }

    // Check if already initialized
    const instanceKey = filters || searchInput || document.body;
    if (instances.has(instanceKey)) {
        return instances.get(instanceKey);
    }

    // Helper function to normalize queue ID
    function normalizeQueueId(qAttr) {
        if (!qAttr || typeof qAttr !== 'string' || qAttr.trim() === '') return 0;
        const q = Number(qAttr);
        return Number.isNaN(q) || q < 0 ? 0 : q;
    }

    function buildQueueMenu(){
        if (!queueDropdown) return;
        const present = Array.from(new Set(rows.map(r => {
            const qAttr = r.getAttribute('data-q');
            return normalizeQueueId(qAttr);
        })));
        const defaultQueues = Object.keys(QUEUE_NAMES)
            .map(key => Number(key))
            .filter(num => Number.isFinite(num) && num >= 0);
        const items = present.length ? present : defaultQueues;
        const uniqueSorted = Array.from(new Set(items)).sort((a,b)=>a-b);
        queueDropdown.innerHTML = '';
        
        // All queues option
        const allItem = document.createElement('li');
        const allBtn = document.createElement('button');
        allBtn.className = 'dropdown-item';
        allBtn.setAttribute('data-q', '');
        allBtn.textContent = 'All queues';
        allItem.appendChild(allBtn);
        queueDropdown.appendChild(allItem);
        
        // Ranked (any) option
        const rankedAny = document.createElement('li');
        const rankedBtn = document.createElement('button');
        rankedBtn.className = 'dropdown-item';
        rankedBtn.setAttribute('data-q', 'ranked');
        rankedBtn.textContent = 'Ranked (any)';
        rankedAny.appendChild(rankedBtn);
        queueDropdown.appendChild(rankedAny);
        
        // Divider
        const divider = document.createElement('li');
        const hr = document.createElement('hr');
        hr.className = 'dropdown-divider';
        divider.appendChild(hr);
        queueDropdown.appendChild(divider);
        
        // Individual queues
        uniqueSorted.forEach(q => {
            const li = document.createElement('li');
            const btn = document.createElement('button');
            btn.className = 'dropdown-item';
            btn.setAttribute('data-q', String(q));
            const label = QUEUE_NAMES[q] || ('Queue ' + q);
            btn.textContent = label;
            li.appendChild(btn);
            queueDropdown.appendChild(li);
        });
        if (queueToggle) queueToggle.textContent = 'All queues';
    }

    buildQueueMenu();

    // Expose a helper to refresh internal row cache after DOM changes
    const refreshMatchRows = function(forceMenuRefresh = false){
        rows = Array.from(document.querySelectorAll('.match-row'));
        if (forceMenuRefresh) {
            buildQueueMenu();
        }
        apply();
    };
    window.refreshMatchRows = refreshMatchRows;

    let selectedQueue = null;
    let forcedFilter = 'all';

    function currentFilter(){ return forcedFilter; }

    function matchesFilter(row){
        const qAttr = row.getAttribute('data-q');
        const queueId = normalizeQueueId(qAttr);
        
        if (selectedQueue === RANKED_SENTINEL) return isRankedQueue(queueId);
        if (selectedQueue !== null) {
            const normalizedQueue = typeof selectedQueue === 'number' ? selectedQueue : Number(selectedQueue);
            if (!Number.isNaN(normalizedQueue)) {
                return queueId === normalizedQueue;
            }
        }
        const f = currentFilter();
        if (f === 'all') return true;
        if (f === 'ranked') return isRankedQueue(queueId);
        return true;
    }

    function matchesSearch(row, term){
        if (!term) return true;
        const hay = (row.querySelector('.search-index')?.innerText || '').toLowerCase();
        return hay.includes(term.toLowerCase());
    }

    function apply(){
        const term = searchInput?.value?.trim().toLowerCase() || '';
        rows.forEach(r => {
            const show = matchesFilter(r) && matchesSearch(r, term);
            r.style.display = show ? '' : 'none';
        });
        updateSummary();
        const anyVisible = rows.some(r => r.style.display !== 'none');
        if (noMsg) noMsg.style.display = anyVisible ? 'none' : 'block';
    }

    function updateSummary(){
        const vis = rows.filter(r => {
            if (r.style.display === 'none') return false;
            const attr = r.getAttribute('data-remake');
            const isRemake = attr === 'true' || attr === true;
            return !isRemake;
        });
        let wins=0, k=0, d=0, a=0;
        vis.forEach(r => {
            const winAttr = r.getAttribute('data-win');
            if (winAttr === 'true' || winAttr === true) wins++;
            k += Number(r.getAttribute('data-k'))||0;
            d += Number(r.getAttribute('data-d'))||0;
            a += Number(r.getAttribute('data-a'))||0;
        });
        const games = vis.length;
        const wr = games ? Math.round((wins/games)*100) : 0;
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
        if (elCount) elCount.textContent = `Last ${games} game${games===1?'':'s'}`;
    }

    window.setHistoryFilter = function(name){
        if (!filters) return;
        const target = (name === 'ranked') ? document.getElementById('filterRanked') : document.getElementById('filterAll');
        filters.querySelectorAll('.nav-link').forEach(b=>b.classList.remove('active'));
        target && target.classList.add('active');
        forcedFilter = (name === 'ranked') ? 'ranked' : 'all';
        selectedQueue = (name === 'ranked') ? RANKED_SENTINEL : null;
        if (queueToggle) queueToggle.textContent = (selectedQueue === RANKED_SENTINEL ? 'Ranked (any)' : 'All queues');
        apply();
    };

    // Event listeners are now attached above in the cleanup section

    let searchTimer = null; 
    const handleSearchInput = () => { 
        if (searchTimer) clearTimeout(searchTimer); 
        searchTimer = setTimeout(() => {
            searchTimer = null;
            apply();
        }, DEBOUNCE_DELAYS.FILTER); 
    };
    
    // Event handler references for proper cleanup
    const filterClickHandler = (e) => {
        const btn = e.target.closest('button[data-filter]');
        if (!btn) return;
        e.preventDefault();
        const which = btn.getAttribute('data-filter') === 'ranked' ? 'ranked' : 'all';
        window.setHistoryFilter(which);
    };
    
    const queueClickHandler = (e) => {
        const item = e.target.closest('button[data-q]');
        if (!item) return;
        e.preventDefault();
        const v = item.getAttribute('data-q');
        selectedQueue = (v === '' ? null : (v === 'ranked' ? RANKED_SENTINEL : Number(v)));
        if (queueToggle) {
            if (selectedQueue === null) queueToggle.textContent = 'All queues';
            else if (selectedQueue === RANKED_SENTINEL) queueToggle.textContent = 'Ranked (any)';
            else queueToggle.textContent = (QUEUE_NAMES[selectedQueue] || ('Queue ' + selectedQueue));
        }
        if (filters) {
            filters.querySelectorAll('.nav-link').forEach(b=>b.classList.remove('active'));
            const allBtn = filters.querySelector('[data-filter="all"]');
            if (allBtn) allBtn.classList.add('active');
        }
        apply();
    };
    
    // Cleanup on page unload
    const cleanup = () => {
        if (searchTimer) {
            clearTimeout(searchTimer);
            searchTimer = null;
        }
        searchInput?.removeEventListener(EVENTS.INPUT, handleSearchInput);
        filters?.removeEventListener(EVENTS.CLICK, filterClickHandler);
        queueDropdown?.removeEventListener(EVENTS.CLICK, queueClickHandler);
        window.removeEventListener(EVENTS.BEFOREUNLOAD, cleanup);
        if (window.refreshMatchRows === refreshMatchRows) {
            window.refreshMatchRows = undefined;
        }
        instances.delete(instanceKey);
    };

    searchInput?.addEventListener(EVENTS.INPUT, handleSearchInput);
    filters?.addEventListener(EVENTS.CLICK, filterClickHandler);
    queueDropdown?.addEventListener(EVENTS.CLICK, queueClickHandler);
    window.addEventListener(EVENTS.BEFOREUNLOAD, cleanup);

    // Store cleanup function globally for reuse
    instances.set(instanceKey, cleanup);

    apply();

    return cleanup;
}
