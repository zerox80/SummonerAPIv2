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

    // Populate dropdown with present queues
    (function buildQueueMenu(){
        if (!queueDropdown) return;
        const present = Array.from(new Set(rows.map(r => {
            const qAttr = r.getAttribute('data-q');
            if (!qAttr || qAttr === 'null' || qAttr === 'undefined') return 0;
            const q = Number(qAttr);
            return Number.isNaN(q) ? 0 : q;
        })));
        const items = present.length ? present : Object.keys(QUEUE_NAMES).map(Number);
        const uniqueSorted = Array.from(new Set(items)).sort((a,b)=>a-b);
        queueDropdown.innerHTML = '';
        const allItem = document.createElement('li');
        allItem.innerHTML = '<button class="dropdown-item" data-q="">All queues</button>';
        queueDropdown.appendChild(allItem);
        const rankedAny = document.createElement('li');
        rankedAny.innerHTML = '<button class="dropdown-item" data-q="ranked">Ranked (any)</button>';
        queueDropdown.appendChild(rankedAny);
        const divider = document.createElement('li'); divider.innerHTML = '<hr class="dropdown-divider">'; queueDropdown.appendChild(divider);
        uniqueSorted.forEach(q=>{
            const li = document.createElement('li');
            const label = QUEUE_NAMES[q] || ('Queue ' + q);
            li.innerHTML = `<button class="dropdown-item" data-q="${q}">${label}</button>`;
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
        if (!qAttr || qAttr === 'null' || qAttr === 'undefined') {
            const queueId = 0;
            if (selectedQueue === RANKED_SENTINEL) return isRankedQ(queueId);
            if (selectedQueue !== null && typeof selectedQueue === 'number') return queueId === selectedQueue;
            const f = currentFilter();
            if (f === 'all') return true;
            if (f === 'ranked') return isRankedQ(queueId);
            return true;
        }
        const q = Number(qAttr);
        const queueId = Number.isNaN(q) ? 0 : q;
        if (selectedQueue === RANKED_SENTINEL) return isRankedQ(queueId);
        if (selectedQueue !== null && typeof selectedQueue === 'number') return queueId === selectedQueue;
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
        if (d === 0) {
            kda = (k + a > 0) ? 'Perfect' : '0.00';
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

    // Single event listener for filter buttons (removed duplicates)
    filters?.addEventListener('click', (e)=>{
        const btn = e.target.closest('button[data-filter]');
        if (!btn) return;
        e.preventDefault();
        const which = btn.getAttribute('data-filter') === 'ranked' ? 'ranked' : 'all';
        window.setHistoryFilter(which);
    });

    queueDropdown?.addEventListener('click', (e)=>{
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
    });

    let searchTimer; 
    const handleSearchInput = () => { 
        clearTimeout(searchTimer); 
        searchTimer = setTimeout(apply, 200); 
    };
    searchInput?.addEventListener('input', handleSearchInput);
    apply();
    
    // Return cleanup function
    return () => {
        if (searchTimer) clearTimeout(searchTimer);
        searchInput?.removeEventListener('input', handleSearchInput);
    };
}
