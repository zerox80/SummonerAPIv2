// Search dropdown with suggestions and history
export function initSearchDropdown() {
    const riotIdInput = document.getElementById('riotId');
    const suggestionsContainer = document.getElementById('suggestions-container');
    if (!riotIdInput || !suggestionsContainer) return;

    let debounceTimer;
    let activeIndex = -1; // Declare at top to avoid hoisting issues
    const heroSection = riotIdInput?.closest('.hero');
    const searchGroup = riotIdInput.closest('.search-group');

    function positionDropdown(force = false){
        if (!suggestionsContainer || !searchGroup) return;
        if (!force && suggestionsContainer.style.display !== 'block') return;

        // rely on CSS (left/right/top in stylesheet) and keep dropdown anchored inside search group
        suggestionsContainer.style.removeProperty('position');
        suggestionsContainer.style.removeProperty('left');
        suggestionsContainer.style.removeProperty('top');
        suggestionsContainer.style.removeProperty('width');
        suggestionsContainer.style.removeProperty('max-width');
        suggestionsContainer.style.removeProperty('z-index');
        heroSection?.classList.add('search-dropdown-open');
    }

    function resetDropdownPlacement(){
        if (!suggestionsContainer) return;
        suggestionsContainer.style.removeProperty('position');
        suggestionsContainer.style.removeProperty('left');
        suggestionsContainer.style.removeProperty('top');
        suggestionsContainer.style.removeProperty('width');
        suggestionsContainer.style.removeProperty('max-width');
        suggestionsContainer.style.removeProperty('z-index');
        heroSection?.classList.remove('search-dropdown-open');
    }

    // Local history
    const LS_KEY = 'searchHistory';
    function loadLocalHistory(){
        try {
            const raw = localStorage.getItem(LS_KEY);
            if (!raw) return [];
            const arr = JSON.parse(raw);
            return Array.isArray(arr) ? arr : [];
        } catch { return []; }
    }
    
    function saveLocalHistory(riotId){
        const v = (riotId || '').trim();
        if (!v || !v.includes('#')) return;
        const maxSize = 10;
        const list = loadLocalHistory().filter(x=>x.toLowerCase() !== v.toLowerCase());
        list.unshift(v);
        while(list.length > maxSize) list.pop();
        try { localStorage.setItem(LS_KEY, JSON.stringify(list)); } catch {}
    }
    
    function renderLocalHistory(filterTerm){
        const list = loadLocalHistory();
        const filtered = (filterTerm ? list.filter(x=>x.toLowerCase().startsWith(filterTerm.toLowerCase())) : list).slice(0,10);
        suggestionsContainer.innerHTML = '';
        let idx = 0;
        if (filtered.length) {
            const hdr = document.createElement('div');
            hdr.className = 'suggestion-section';
            hdr.textContent = 'Zuletzt gesucht';
            suggestionsContainer.appendChild(hdr);
        }
        filtered.forEach(riotId => {
            const item = document.createElement('a');
            item.href = '#';
            item.classList.add('list-group-item','list-group-item-action','d-flex','align-items-center');
            item.setAttribute('role','option');
            const optId = `ls-suggestion-${idx++}`;
            item.id = optId;
            item.setAttribute('aria-selected','false');
            const icon = document.createElement('i');
            icon.classList.add('fas','fa-history','me-2','text-secondary');
            icon.setAttribute('aria-hidden','true');
            item.appendChild(icon);
            const text = document.createElement('span');
            text.className = 'suggestion-text';
            text.textContent = riotId;
            item.appendChild(text);
            item.addEventListener('click', (e)=>{
                e.preventDefault();
                riotIdInput.value = riotId;
                suggestionsContainer.innerHTML = '';
                suggestionsContainer.style.display = 'none';
                riotIdInput.setAttribute('aria-expanded','false');
                riotIdInput.removeAttribute('aria-activedescendant');
            });
            suggestionsContainer.appendChild(item);
        });
        const trimmedQuery = (filterTerm ?? '').trim();
        if (!filtered.length) {
            if (trimmedQuery.length) {
                const empty = document.createElement('div');
                empty.className = 'suggestion-section';
                empty.textContent = 'Keine Einträge';
                suggestionsContainer.appendChild(empty);
            } else {
                suggestionsContainer.style.display = 'none';
                riotIdInput.setAttribute('aria-expanded','false');
                riotIdInput.removeAttribute('aria-activedescendant');
                resetDropdownPlacement();
                return;
            }
        }
        suggestionsContainer.style.display = 'block';
        riotIdInput.setAttribute('aria-expanded','true');
        positionDropdown();
    }

    function fetchAndDisplaySuggestions(query) {
        clearTimeout(debounceTimer);
        activeIndex = -1;
        renderLocalHistory(query);

        debounceTimer = setTimeout(() => {
            fetch(`/api/summoner-suggestions?query=${encodeURIComponent(query)}`)
                .then(response => {
                    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                    return response.json();
                })
                .then(suggestions => {
                    if (Array.isArray(suggestions) && suggestions.length > 0) {
                        let idx = suggestionsContainer.querySelectorAll('[role="option"]').length;
                        const hdr = document.createElement('div');
                        hdr.className = 'suggestion-section';
                        hdr.textContent = 'Vorschläge';
                        suggestionsContainer.appendChild(hdr);
                        suggestions.forEach(suggestion => {
                            const item = document.createElement('a');
                            item.href = '#';
                            item.classList.add('list-group-item', 'list-group-item-action', 'd-flex', 'align-items-center');
                            item.setAttribute('role','option');
                            const optId = `suggestion-opt-${idx++}`;
                            item.id = optId;
                            item.setAttribute('aria-selected','false');

                            const img = document.createElement('img');
                            img.src = suggestion.profileIconUrl || (`https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/profile-icons/${suggestion.profileIconId}.jpg`);
                            img.alt = 'Icon';
                            img.classList.add('suggestion-avatar','me-2');
                            img.onerror = function() { this.style.display = 'none'; };
                            item.appendChild(img);

                            const textContainer = document.createElement('div');
                            textContainer.className = 'suggestion-text';
                            const riotIdSpan = document.createElement('span');
                            riotIdSpan.textContent = suggestion.riotId;
                            textContainer.appendChild(riotIdSpan);

                            if (suggestion.summonerLevel) {
                                const levelSpan = document.createElement('span');
                                levelSpan.textContent = `(Lvl: ${suggestion.summonerLevel})`;
                                levelSpan.classList.add('level','text-muted','small');
                                textContainer.appendChild(levelSpan);
                            }
                            item.appendChild(textContainer);

                            item.addEventListener('click', function (e) {
                                e.preventDefault();
                                riotIdInput.value = suggestion.riotId;
                                suggestionsContainer.innerHTML = '';
                                suggestionsContainer.style.display = 'none';
                                riotIdInput.setAttribute('aria-expanded', 'false');
                                riotIdInput.removeAttribute('aria-activedescendant');
                            });

                            suggestionsContainer.appendChild(item);
                        });
                        suggestionsContainer.style.display = 'block';
                        riotIdInput.setAttribute('aria-expanded', 'true');
                    }
                })
                .catch(error => console.error('Error fetching suggestions:', error));
        }, 300);
    }

    const searchForm = riotIdInput?.closest('form');
    searchForm?.addEventListener('submit', (e)=>{
        const q = riotIdInput?.value || '';
        saveLocalHistory(q);
    });

    riotIdInput.addEventListener('input', function () {
        fetchAndDisplaySuggestions(riotIdInput.value);
    });

    riotIdInput.addEventListener('focus', function () {
        if (riotIdInput.value === '') {
            fetchAndDisplaySuggestions('');
        }
        positionDropdown(true);
    });

    // Keyboard navigation
    function updateActive(toIndex) {
        const items = Array.from(suggestionsContainer.querySelectorAll('[role="option"]'));
        items.forEach((el,i) => {
            if (i === toIndex) {
                el.classList.add('active');
                el.setAttribute('aria-selected','true');
                riotIdInput.setAttribute('aria-activedescendant', el.id);
            } else {
                el.classList.remove('active');
                el.setAttribute('aria-selected','false');
            }
        });
    }

    riotIdInput.addEventListener('keydown', function(e){
        const items = Array.from(suggestionsContainer.querySelectorAll('[role="option"]'));
        if (!items.length) return;
        if (e.key === 'ArrowDown') { e.preventDefault(); activeIndex = (activeIndex + 1) % items.length; updateActive(activeIndex); }
        else if (e.key === 'ArrowUp') { e.preventDefault(); activeIndex = (activeIndex - 1 + items.length) % items.length; updateActive(activeIndex); }
        else if (e.key === 'Enter') {
            if (activeIndex >= 0 && items[activeIndex]) { e.preventDefault(); items[activeIndex].click(); }
        } else if (e.key === 'Escape') {
            suggestionsContainer.innerHTML = '';
            suggestionsContainer.style.display = 'none';
            resetDropdownPlacement();
            riotIdInput.setAttribute('aria-expanded','false');
            riotIdInput.removeAttribute('aria-activedescendant');
            activeIndex = -1;
        }
    });

    document.addEventListener('click', function (event) {
        // Improved click-outside handler: check for modals and other overlays
        const target = event.target;
        const isInsideInput = riotIdInput.contains(target);
        const isInsideDropdown = suggestionsContainer.contains(target);
        const isInsideModal = target.closest('.modal');
        
        if (!isInsideInput && !isInsideDropdown && !isInsideModal) {
            suggestionsContainer.innerHTML = '';
            suggestionsContainer.style.display = 'none';
            riotIdInput.setAttribute('aria-expanded','false');
            riotIdInput.removeAttribute('aria-activedescendant');
            activeIndex = -1;
            resetDropdownPlacement();
        }
    });
}
