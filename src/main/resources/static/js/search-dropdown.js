// Search dropdown with suggestions and history
export function initSearchDropdown() {
    const riotIdInput = document.getElementById('riotId');
    const suggestionsContainer = document.getElementById('suggestions-container');
    if (!riotIdInput || !suggestionsContainer) return;

    let debounceTimer;
    const heroSection = riotIdInput?.closest('.hero');
    const dropdownOriginalParent = suggestionsContainer?.parentElement || null;
    const dropdownOriginalNextSibling = suggestionsContainer?.nextSibling || null;
    const dropdownPlaceholder = document.createElement('div');
    dropdownPlaceholder.style.display = 'none';
    let dropdownMovedToBody = false;

    function moveDropdownToBody(){
        if (!suggestionsContainer || dropdownMovedToBody || !dropdownOriginalParent) return;
        dropdownOriginalParent.insertBefore(dropdownPlaceholder, dropdownOriginalNextSibling);
        document.body.appendChild(suggestionsContainer);
        dropdownMovedToBody = true;
    }

    function restoreDropdownParent(){
        if (!suggestionsContainer || !dropdownMovedToBody || !dropdownOriginalParent) return;
        dropdownOriginalParent.insertBefore(suggestionsContainer, dropdownPlaceholder);
        dropdownPlaceholder.remove();
        dropdownMovedToBody = false;
    }

    function positionDropdown(force = false){
        if (!suggestionsContainer || !riotIdInput) return;
        if (!force && suggestionsContainer.style.display !== 'block') return;

        // Wait for CSS transition to finish before calculating position
        setTimeout(() => {
            const rect = riotIdInput.getBoundingClientRect();
            if (!rect || !rect.width) return;
            moveDropdownToBody();
            const left = rect.left;
            const top = rect.bottom + 8; // Reduced gap
            const width = rect.width;
            suggestionsContainer.style.position = 'fixed';
            suggestionsContainer.style.zIndex = '999999';
            suggestionsContainer.style.left = `${Math.round(left)}px`;
            suggestionsContainer.style.top = `${Math.round(top)}px`;
            suggestionsContainer.style.width = `${Math.round(width)}px`;
            suggestionsContainer.style.maxWidth = `${Math.round(width)}px`;
            suggestionsContainer.style.boxShadow = '0 20px 40px rgba(0,0,0,0.45)';
            suggestionsContainer.style.border = '1px solid rgba(15,20,30,0.75)';
            requestAnimationFrame(() => {
                const dropdownHeight = suggestionsContainer.offsetHeight || 0;
                if (dropdownHeight > 0 && dropdownPlaceholder) {
                    dropdownPlaceholder.style.display = 'block';
                    dropdownPlaceholder.style.height = `${dropdownHeight + 24}px`;
                }
            });
        }, 160);

        heroSection?.classList.add('search-dropdown-open');
    }

    function resetDropdownPlacement(){
        if (!suggestionsContainer) return;
        suggestionsContainer.style.position = '';
        suggestionsContainer.style.zIndex = '';
        suggestionsContainer.style.left = '';
        suggestionsContainer.style.top = '';
        suggestionsContainer.style.width = '';
        suggestionsContainer.style.maxWidth = '';
        suggestionsContainer.style.boxShadow = '';
        suggestionsContainer.style.border = '';
        heroSection?.classList.remove('search-dropdown-open');
        dropdownPlaceholder.style.display = 'none';
        dropdownPlaceholder.style.height = '0';
        restoreDropdownParent();
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
        }, query === '' ? 0 : 300);
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
    let activeIndex = -1;
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
        if (!riotIdInput.contains(event.target) && !suggestionsContainer.contains(event.target)) {
            suggestionsContainer.innerHTML = '';
            suggestionsContainer.style.display = 'none';
            riotIdInput.setAttribute('aria-expanded','false');
            riotIdInput.removeAttribute('aria-activedescendant');
            activeIndex = -1;
        }
    });
}
