// Match filtering + summary widget
import {
  DEBOUNCE_DELAYS,
  QUEUE_TYPES,
  QUEUE_NAMES,
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
    const searchStatusRegion = document.querySelector(SELECTORS.MATCH_SEARCH_STATUS);
    if (!rows.length && noMsg) { noMsg.style.display = ''; }

    // Check if already initialized
    const instanceKey = filters || searchInput || document.body;
    if (instances.has(instanceKey)) {
        return instances.get(instanceKey);
    }

    function normalizeQueueId(qAttr) {
        if (!qAttr || typeof qAttr !== 'string' || qAttr.trim() === '') return 0;
        const q = Number(qAttr);
        return Number.isNaN(q) || q < 0 ? 0 : q;
    }

    function buildQueueMenu() {
        if (!queueDropdown) return;
        queueDropdown.setAttribute('role', 'menu');
        const present = Array.from(new Set(rows.map(r => normalizeQueueId(r.getAttribute('data-q')))));
        const defaultQueues = Object.keys(QUEUE_NAMES)
            .map(key => Number(key))
            .filter(num => Number.isFinite(num) && num >= 0);
        const items = present.length ? present : defaultQueues;
        const uniqueSorted = Array.from(new Set(items)).sort((a, b) => a - b);
        queueDropdown.innerHTML = '';

        const allItem = document.createElement('li');
        const allBtn = document.createElement('button');
        allBtn.className = 'dropdown-item';
        allBtn.setAttribute('data-q', '');
        allBtn.setAttribute('role', 'menuitemradio');
        allBtn.setAttribute('aria-checked', 'true');
        allBtn.textContent = 'All queues';
        allItem.appendChild(allBtn);
        queueDropdown.appendChild(allItem);

        const rankedAny = document.createElement('li');
        const rankedBtn = document.createElement('button');
        rankedBtn.className = 'dropdown-item';
        rankedBtn.setAttribute('data-q', 'ranked');
        rankedBtn.setAttribute('role', 'menuitemradio');
        rankedBtn.setAttribute('aria-checked', 'false');
        rankedBtn.textContent = 'Ranked (any)';
        rankedAny.appendChild(rankedBtn);
        queueDropdown.appendChild(rankedAny);

        const divider = document.createElement('li');
        const hr = document.createElement('hr');
        hr.className = 'dropdown-divider';
        divider.appendChild(hr);
        queueDropdown.appendChild(divider);

        uniqueSorted.forEach(q => {
            const li = document.createElement('li');
            const btn = document.createElement('button');
            btn.className = 'dropdown-item';
            btn.setAttribute('data-q', String(q));
            btn.setAttribute('role', 'menuitemradio');
            btn.setAttribute('aria-checked', 'false');
            const label = QUEUE_NAMES[q] || ('Queue ' + q);
            btn.textContent = label;
            li.appendChild(btn);
            queueDropdown.appendChild(li);
        });

        if (queueToggle) {
            queueToggle.textContent = 'All queues';
            queueToggle.setAttribute('aria-label', 'Filter matches by queue');
            queueToggle.setAttribute('aria-live', 'polite');
        }
    }

    function updateQueueControls() {
        if (queueDropdown) {
            queueDropdown.querySelectorAll('button[data-q]').forEach(btn => {
                const btnVal = btn.getAttribute('data-q');
                const isMatch = (btnVal === '' && selectedQueue === null) ||
                    (btnVal === 'ranked' && selectedQueue === RANKED_SENTINEL) ||
                    (btnVal !== '' && btnVal !== 'ranked' && Number(btnVal) === selectedQueue);
                btn.setAttribute('aria-checked', isMatch ? 'true' : 'false');
            });
        }
        if (queueToggle) {
            if (selectedQueue === null) queueToggle.textContent = 'All queues';
            else if (selectedQueue === RANKED_SENTINEL) queueToggle.textContent = 'Ranked (any)';
            else queueToggle.textContent = QUEUE_NAMES[selectedQueue] || ('Queue ' + selectedQueue);
            queueToggle.setAttribute('aria-label', `Current queue filter: ${queueToggle.textContent}`);
        }
    }

    buildQueueMenu();

    const refreshMatchRows = function (forceMenuRefresh = false) {
        rows = Array.from(document.querySelectorAll('.match-row'));
        if (forceMenuRefresh) {
            buildQueueMenu();
        }
        updateQueueControls();
        apply();
    };
    window.refreshMatchRows = refreshMatchRows;

    let selectedQueue = null;
    let forcedFilter = 'all';

    function currentFilter() {
        return forcedFilter;
    }

    function matchesFilter(row) {
        const queueId = normalizeQueueId(row.getAttribute('data-q'));
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

    function matchesSearch(row, term) {
        if (!term) return true;
        const hay = (row.querySelector('.search-index')?.innerText || '').toLowerCase();
        return hay.includes(term.toLowerCase());
    }

    function apply() {
        const term = searchInput?.value?.trim().toLowerCase() || '';
        rows.forEach(r => {
            const show = matchesFilter(r) && matchesSearch(r, term);
            r.style.display = show ? '' : 'none';
        });
        const summaryText = updateSummary();
        const visibleRows = rows.filter(r => r.style.display !== 'none');
        const anyVisible = visibleRows.length > 0;
        if (noMsg) noMsg.style.display = anyVisible ? 'none' : 'block';
        if (searchStatusRegion) {
            const baseMessage = anyVisible
                ? `Showing ${visibleRows.length} match${visibleRows.length === 1 ? '' : 'es'}.`
                : 'No matches are visible for the current filters.';
            const queueMessage = selectedQueue === RANKED_SENTINEL
                ? ' Ranked queues only.'
                : (selectedQueue !== null ? ` Queue filter: ${queueToggle?.textContent || 'Custom queue'}.` : '');
            const modeMessage = forcedFilter === 'ranked' && selectedQueue !== RANKED_SENTINEL
                ? ' Ranked mode enabled.'
                : '';
            searchStatusRegion.textContent = summaryText
                ? `${baseMessage} ${summaryText}${queueMessage}${modeMessage}`.trim()
                : `${baseMessage}${queueMessage}${modeMessage}`.trim();
        }
    }

    function updateSummary() {
        const vis = rows.filter(r => {
            if (r.style.display === 'none') return false;
            const attr = r.getAttribute('data-remake');
            const isRemake = attr === 'true' || attr === true;
            return !isRemake;
        });
        let wins = 0, k = 0, d = 0, a = 0;
        vis.forEach(r => {
            const winAttr = r.getAttribute('data-win');
            if (winAttr === 'true' || winAttr === true) wins++;
            k += Number(r.getAttribute('data-k')) || 0;
            d += Number(r.getAttribute('data-d')) || 0;
            a += Number(r.getAttribute('data-a')) || 0;
        });
        const games = vis.length;
        const wr = games ? Math.round((wins / games) * 100) : 0;
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
        if (!games) {
            return '';
        }
        return `Win rate ${wr}% with ${kda === 'Perfect' ? 'a perfect KDA' : `a ${kda} KDA`}.`;
    }

    window.setHistoryFilter = function (name) {
        if (!filters) return;
        const target = (name === 'ranked')
            ? document.getElementById('filterRanked')
            : document.getElementById('filterAll');
        filters.querySelectorAll('.nav-link').forEach(b => {
            b.classList.remove('active');
            b.setAttribute('aria-pressed', 'false');
        });
        if (target) {
            target.classList.add('active');
            target.setAttribute('aria-pressed', 'true');
        }
        forcedFilter = (name === 'ranked') ? 'ranked' : 'all';
        selectedQueue = (name === 'ranked') ? RANKED_SENTINEL : null;
        updateQueueControls();
        apply();
    };

    let searchTimer = null;
    const handleSearchInput = () => {
        if (searchTimer) clearTimeout(searchTimer);
        searchTimer = setTimeout(() => {
            searchTimer = null;
            apply();
        }, DEBOUNCE_DELAYS.FILTER);
    };

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
        if (filters) {
            filters.querySelectorAll('.nav-link').forEach(b => {
                b.classList.remove('active');
                b.setAttribute('aria-pressed', 'false');
            });
            const allBtn = filters.querySelector('[data-filter="all"]');
            if (allBtn) {
                allBtn.classList.add('active');
                allBtn.setAttribute('aria-pressed', 'true');
            }
        }
        updateQueueControls();
        apply();
    };

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

    instances.set(instanceKey, cleanup);

    updateQueueControls();
    apply();

    return cleanup;
}
