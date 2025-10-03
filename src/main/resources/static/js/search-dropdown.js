// Search dropdown with suggestions and history
export function initSearchDropdown() {
    const riotIdInput = document.getElementById('riotId');
    const suggestionsContainer = document.getElementById('suggestions-container');
    if (!riotIdInput || !suggestionsContainer) return;

    let debounceTimer = null;
    let activeIndex = -1; // Declare at top to avoid hoisting issues
    const heroSection = riotIdInput?.closest('.hero');
    const searchGroup = riotIdInput.closest('.search-group');
    let isDestroyed = false;
    let currentAbortController = null; // For canceling in-flight requests

    function positionDropdown(force = false) {
        if (!suggestionsContainer || !searchGroup) return;
        if (!force && suggestionsContainer.style.display !== 'block') return;

        // rely on CSS (left/right/top in stylesheet) and keep dropdown anchored inside search group
        suggestionsContainer.style.removeProperty('position');
        suggestionsContainer.style.removeProperty('left');
        suggestionsContainer.style.removeProperty('top');
        suggestionsContainer.style.removeProperty('width');
        suggestionsContainer.style.removeProperty('max-width');
        suggestionsContainer.style.removeProperty('z-index');

        if (suggestionsContainer.style.display === 'block') {
            heroSection?.classList.add('search-dropdown-open');
        }
    }

    function resetDropdownPlacement() {
        if (!suggestionsContainer) return;
        suggestionsContainer.style.removeProperty('position');
        suggestionsContainer.style.removeProperty('left');
        suggestionsContainer.style.removeProperty('top');
        suggestionsContainer.style.removeProperty('width');
        suggestionsContainer.style.removeProperty('max-width');
        suggestionsContainer.style.removeProperty('z-index');
        heroSection?.classList.remove('search-dropdown-open');
    }

    function cancelInFlightRequest() {
        if (!currentAbortController) return;
        try {
            currentAbortController.abort();
        } catch (e) {
            console.debug('AbortController abort failed:', e);
        }
        currentAbortController = null;
    }

    function clearActiveState() {
        if (!suggestionsContainer) return;
        suggestionsContainer.querySelectorAll('[role="option"]').forEach(el => {
            el.classList.remove('active');
            el.setAttribute('aria-selected', 'false');
        });
        riotIdInput.removeAttribute('aria-activedescendant');
        activeIndex = -1;
    }

    function hideSuggestions() {
        cancelInFlightRequest();
        suggestionsContainer.innerHTML = '';
        suggestionsContainer.style.display = 'none';
        riotIdInput.setAttribute('aria-expanded', 'false');
        clearActiveState();
        resetDropdownPlacement();
    }

    // Cleanup function to prevent memory leaks
    const cleanup = () => {
        isDestroyed = true;
        if (debounceTimer) {
            clearTimeout(debounceTimer);
            debounceTimer = null;
        }
        hideSuggestions();
    };

    // Local history
    const LS_KEY = 'searchHistory';
    function loadLocalHistory() {
        try {
            const raw = localStorage.getItem(LS_KEY);
            if (!raw) return [];
            const arr = JSON.parse(raw);
            return Array.isArray(arr) ? arr : [];
        } catch {
            return [];
        }
    }

    function saveLocalHistory(riotId) {
        const v = (riotId || '').trim();
        // Validate format: must contain exactly one '#' and have non-empty parts
        if (!v || !v.includes('#')) return;
        const parts = v.split('#');
        if (parts.length !== 2 || !parts[0].trim() || !parts[1].trim()) return;
        const maxSize = 10;
        const list = loadLocalHistory().filter(x => x.toLowerCase() !== v.toLowerCase());
        list.unshift(v);
        while (list.length > maxSize) list.pop();
        try {
            localStorage.setItem(LS_KEY, JSON.stringify(list));
        } catch {
            // ignore quota errors
        }
    }

    function renderLocalHistory(filterTerm) {
        clearActiveState();
        const list = loadLocalHistory();
        const filtered = (filterTerm ? list.filter(x => x.toLowerCase().startsWith(filterTerm.toLowerCase())) : list).slice(0, 10);
        suggestionsContainer.innerHTML = '';
        let idx = 0;
        if (filtered.length) {
            const hdr = document.createElement('div');
            hdr.className = 'suggestion-section';
            hdr.textContent = 'Recent Searches';
            suggestionsContainer.appendChild(hdr);
        }
        filtered.forEach(riotId => {
            const item = document.createElement('a');
            item.href = '#';
            item.classList.add('list-group-item', 'list-group-item-action', 'd-flex', 'align-items-center');
            item.setAttribute('role', 'option');
            const optId = `ls-suggestion-${idx++}`;
            item.id = optId;
            item.setAttribute('aria-selected', 'false');
            const icon = document.createElement('i');
            icon.classList.add('fas', 'fa-history', 'me-2', 'text-secondary');
            icon.setAttribute('aria-hidden', 'true');
            item.appendChild(icon);
            const text = document.createElement('span');
            text.className = 'suggestion-text';
            text.textContent = riotId;
            item.appendChild(text);
            item.addEventListener('click', e => {
                e.preventDefault();
                riotIdInput.value = riotId;
                hideSuggestions();
            });
            suggestionsContainer.appendChild(item);
        });
        const trimmedQuery = (filterTerm ?? '').trim();
        if (!filtered.length) {
            if (trimmedQuery.length) {
                const empty = document.createElement('div');
                empty.className = 'suggestion-section suggestion-empty';
                empty.setAttribute('role', 'note');
                empty.textContent = 'No entries found';
                suggestionsContainer.appendChild(empty);
            } else {
                hideSuggestions();
                return;
            }
        }
        suggestionsContainer.style.display = 'block';
        riotIdInput.setAttribute('aria-expanded', 'true');
        positionDropdown();
    }

    function fetchAndDisplaySuggestions(query) {
        if (isDestroyed) return;
        if (debounceTimer) {
            clearTimeout(debounceTimer);
            debounceTimer = null;
        }
        const requestedQuery = typeof query === 'string' ? query : '';
        const trimmedQuery = requestedQuery.trim();
        const normalizedRequestedQuery = trimmedQuery.toLowerCase();

        try {
            renderLocalHistory(requestedQuery);
        } catch (e) {
            console.error('Error rendering local history:', e);
            return;
        }

        if (!trimmedQuery) {
            cancelInFlightRequest();
            return;
        }

        debounceTimer = setTimeout(() => {
            if (isDestroyed) return;
            cancelInFlightRequest();
            currentAbortController = new AbortController();
            const thisAbortController = currentAbortController;

            fetch(`/api/summoner-suggestions?query=${encodeURIComponent(requestedQuery)}`, {
                signal: thisAbortController.signal
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(suggestions => {
                    if (isDestroyed) return;
                    const normalizedCurrentValue = (riotIdInput.value ?? '').trim().toLowerCase();
                    if (normalizedCurrentValue !== normalizedRequestedQuery) {
                        return;
                    }
                    if (!suggestions || !Array.isArray(suggestions) || suggestions.length === 0) {
                        return;
                    }

                    suggestionsContainer.querySelectorAll('.suggestion-empty').forEach(el => el.remove());

                    let idx = suggestionsContainer.querySelectorAll('[role="option"]').length;
                    const hdr = document.createElement('div');
                    hdr.className = 'suggestion-section';
                    hdr.textContent = 'Suggestions';
                    suggestionsContainer.appendChild(hdr);
                    suggestions.forEach(suggestion => {
                        if (!suggestion || !suggestion.riotId) return; // Skip invalid suggestions

                        const item = document.createElement('a');
                        item.href = '#';
                        item.classList.add('list-group-item', 'list-group-item-action', 'd-flex', 'align-items-center');
                        item.setAttribute('role', 'option');
                        const optId = `suggestion-opt-${idx++}`;
                        item.id = optId;
                        item.setAttribute('aria-selected', 'false');

                        const img = document.createElement('img');
                        // Validate profileIconId before building fallback URL
                        const hasValidIcon = suggestion.profileIconUrl && suggestion.profileIconUrl.trim();
                        const hasValidIconId = suggestion.profileIconId && !isNaN(Number(suggestion.profileIconId));
                        const iconUrl = hasValidIcon
                            ? suggestion.profileIconUrl
                            : (hasValidIconId ? `https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/profile-icons/${suggestion.profileIconId}.jpg` : '');
                        if (iconUrl) {
                            img.src = iconUrl;
                        } else {
                            img.style.display = 'none';
                        }
                        img.alt = 'Icon';
                        img.classList.add('suggestion-avatar', 'me-2');
                        img.onerror = function () {
                            this.style.display = 'none';
                        };
                        item.appendChild(img);

                        const textContainer = document.createElement('div');
                        textContainer.className = 'suggestion-text';
                        const riotIdSpan = document.createElement('span');
                        riotIdSpan.textContent = suggestion.riotId;
                        textContainer.appendChild(riotIdSpan);

                        if (suggestion.summonerLevel) {
                            const levelSpan = document.createElement('span');
                            levelSpan.textContent = `(Lvl: ${suggestion.summonerLevel})`;
                            levelSpan.classList.add('level', 'text-muted', 'small');
                            textContainer.appendChild(levelSpan);
                        }
                        item.appendChild(textContainer);

                        item.addEventListener('click', e => {
                            e.preventDefault();
                            riotIdInput.value = suggestion.riotId;
                            hideSuggestions();
                        });

                        suggestionsContainer.appendChild(item);
                    });
                    suggestionsContainer.style.display = 'block';
                    riotIdInput.setAttribute('aria-expanded', 'true');
                    positionDropdown(true);
                })
                .catch(error => {
                    // Ignore AbortError (user is typing quickly)
                    if (error.name === 'AbortError') return;
                    console.error('Error fetching suggestions:', error);
                    // Silently fail - local history is still shown
                })
                .finally(() => {
                    if (currentAbortController === thisAbortController) {
                        currentAbortController = null;
                    }
                });
        }, 300);
    }

    const searchForm = riotIdInput.closest('form');

    const handleFormSubmit = () => {
        const q = riotIdInput?.value || '';
        saveLocalHistory(q);
    };

    const handleInput = () => {
        fetchAndDisplaySuggestions(riotIdInput.value);
    };

    const handleFocus = () => {
        fetchAndDisplaySuggestions(riotIdInput.value);
        positionDropdown(true);
    };

    const handleBlur = () => {
        setTimeout(() => {
            if (isDestroyed) return;
            const activeElement = document.activeElement;
            if (!activeElement) {
                hideSuggestions();
                return;
            }
            if (activeElement === riotIdInput) return;
            if (!suggestionsContainer.contains(activeElement)) {
                hideSuggestions();
            }
        }, 0);
    };

    // Keyboard navigation
    function updateActive(toIndex) {
        const items = Array.from(suggestionsContainer.querySelectorAll('[role="option"]'));
        items.forEach((el, i) => {
            if (i === toIndex) {
                el.classList.add('active');
                el.setAttribute('aria-selected', 'true');
                riotIdInput.setAttribute('aria-activedescendant', el.id);
            } else {
                el.classList.remove('active');
                el.setAttribute('aria-selected', 'false');
            }
        });
    }

    const handleKeydown = e => {
        const items = Array.from(suggestionsContainer.querySelectorAll('[role="option"]'));
        if (e.key === 'Escape') {
            hideSuggestions();
            return;
        }
        if (e.key === 'Tab') {
            hideSuggestions();
            return;
        }
        if (!items.length) return;
        if (e.key === 'ArrowDown') {
            e.preventDefault();
            e.stopPropagation();
            activeIndex = (activeIndex + 1) % items.length;
            updateActive(activeIndex);
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            e.stopPropagation();
            activeIndex = (activeIndex - 1 + items.length) % items.length;
            updateActive(activeIndex);
        } else if (e.key === 'Enter') {
            if (activeIndex >= 0 && items[activeIndex]) {
                e.preventDefault();
                items[activeIndex].click();
            }
        }
    };

    const handleClickOutside = event => {
        // Improved click-outside handler: check for modals and other overlays
        const target = event.target;
        const isInsideInput = riotIdInput.contains(target);
        const isInsideDropdown = suggestionsContainer.contains(target);
        const isInsideModal = target.closest('.modal');

        if (!isInsideInput && !isInsideDropdown && !isInsideModal) {
            hideSuggestions();
        }
    };

    // Prevent duplicate initialization
    if (window.__searchDropdownInitialized) {
        console.warn('Search dropdown already initialized');
        return window.__searchDropdownCleanup;
    }
    window.__searchDropdownInitialized = true;

    document.addEventListener('click', handleClickOutside);
    searchForm?.addEventListener('submit', handleFormSubmit);
    riotIdInput.addEventListener('input', handleInput);
    riotIdInput.addEventListener('focus', handleFocus);
    riotIdInput.addEventListener('blur', handleBlur);
    riotIdInput.addEventListener('keydown', handleKeydown);

    const fullCleanup = () => {
        cleanup();
        document.removeEventListener('click', handleClickOutside);
        searchForm?.removeEventListener('submit', handleFormSubmit);
        riotIdInput.removeEventListener('input', handleInput);
        riotIdInput.removeEventListener('focus', handleFocus);
        riotIdInput.removeEventListener('blur', handleBlur);
        riotIdInput.removeEventListener('keydown', handleKeydown);
        delete window.__searchDropdownInitialized;
        delete window.__searchDropdownCleanup;
    };

    window.__searchDropdownCleanup = fullCleanup;

    // Return cleanup function to allow proper teardown
    return fullCleanup;
}
