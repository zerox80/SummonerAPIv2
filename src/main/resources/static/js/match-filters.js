// Match filtering + summary widget
export function initMatchFilters() {
    let rows = Array.from(document.querySelectorAll('.match-row'));
    const filters = document.getElementById('historyFilters');
    const searchInput = document.getElementById('matchSearch');
    const elWR = document.getElementById('summaryWR');
    const elKDA = document.getElementById('summaryKDA');
    const elCount = document.getElementById('summaryCount');
    const queueToggle = document.getElementById('queueToggle');
    const queueDropdown = document.getElementById('queueDropdown');
    const noMsg = document.getElementById('noMatchesMsg');
    if (!rows.length && noMsg) { noMsg.style.display = ''; }
    
    // Track if already initialized to prevent duplicate listeners
    if (window.__matchFiltersInitialized) {
        console.warn('Match filters already initialized, skipping duplicate init');
        return window.__matchFiltersCleanup;
    }
    window.__matchFiltersInitialized = true;

    const QUEUE_NAMES = {
        420: 'Ranked Solo/Duo',
        440: 'Ranked Flex',
        400: 'Normal Draft',
        430: 'Normal Blind',
        450: 'ARAM',
        700: 'Clash',
        1700: 'Arena',
        1900: 'URF',
        900: 'URF (old)',
        0: 'Custom',
        1100: 'TFT Ranked',
        1090: 'TFT Normal',
        1130: 'TFT Double Up',
        1160: 'TFT Hyper Roll'
    };
    
    function isRankedQ(q){ return q === 420 || q === 440 || q === 1100; }
    const RANKED_SENTINEL = 'ranked';

    // Helper function to normalize queue ID
    function normalizeQueueId(qAttr) {
        if (!qAttr || typeof qAttr !== 'string' || qAttr.trim() === '') return 0;
        const q = Number(qAttr);
        return Number.isNaN(q) || q < 0 ? 0 : q;
    }

    // Populate dropdown with present queues
    (function buildQueueMenu(){
        if (!queueDropdown) return;
        const present = Array.from(new Set(rows.map(r => {
            const qAttr = r.getAttribute('data-q');
            return normalizeQueueId(qAttr);
        })));
        const items = present.length ? present : Object.keys(QUEUE_NAMES).map(Number);
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
    })();

    // Expose a helper to refresh internal row cache after DOM changes
    window.refreshMatchRows = function(){
        rows = Array.from(document.querySelectorAll('.match-row'));
        apply();
    };

    let selectedQueue = null;
    let forcedFilter = 'all';

    function currentFilter(){ return forcedFilter; }

    function matchesFilter(row){
        const qAttr = row.getAttribute('data-q');
        const queueId = normalizeQueueId(qAttr);
        
        if (selectedQueue === RANKED_SENTINEL) return isRankedQ(queueId);
        if (selectedQueue !== null) {
            const normalizedQueue = typeof selectedQueue === 'number' ? selectedQueue : Number(selectedQueue);
            if (!Number.isNaN(normalizedQueue)) {
                return queueId === normalizedQueue;
            }
        }
        const f = currentFilter();
        if (f === 'all') return true;
        if (f === 'ranked') return isRankedQ(queueId);
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
        }, 200); 
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
        searchInput?.removeEventListener('input', handleSearchInput);
        filters?.removeEventListener('click', filterClickHandler);
        queueDropdown?.removeEventListener('click', queueClickHandler);
        window.removeEventListener('beforeunload', cleanup);
        delete window.__matchFiltersInitialized;
        delete window.__matchFiltersCleanup;
    };
    
    searchInput?.addEventListener('input', handleSearchInput);
    filters?.addEventListener('click', filterClickHandler);
    queueDropdown?.addEventListener('click', queueClickHandler);
    window.addEventListener('beforeunload', cleanup);
    
    // Store cleanup function globally for reuse
    window.__matchFiltersCleanup = cleanup;
    
    apply();
    
    // Return cleanup function
    return cleanup;
}
