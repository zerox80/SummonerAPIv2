// Search dropdown with suggestions and history
// Coordinates Riot ID lookup UX: debounced remote requests, local history, accessibility, and teardown
import {
  DEBOUNCE_DELAYS,
  CACHE_DURATION,
  STORAGE_KEYS,
  CSS_CLASSES,
  ARIA,
  EVENTS,
  KEYS,
  SELECTORS,
  URLS
} from './constants.js';

// Instance tracking using WeakMap for better memory management
// Ensures the component is only initialized once per input and can be GC'd when DOM nodes go away
const instances = new WeakMap();
const suggestionCache = new Map();

/**
 * Initializes search dropdown functionality for a Riot ID input field
 * @param {HTMLInputElement} riotIdInput - The input element for Riot ID search
 * @returns {Function|null} Cleanup function to remove event listeners, or null if already initialized
 */
export function initSearchDropdown() {
    const riotIdInput = document.querySelector(SELECTORS.RIOT_ID_INPUT);
    const suggestionsContainer = document.querySelector(SELECTORS.SUGGESTIONS_CONTAINER);
    if (!riotIdInput || !suggestionsContainer) return;

    // Check if already initialized for this element
    if (instances.has(riotIdInput)) {
        return instances.get(riotIdInput);
    }

    // Mark initialization in progress to avoid duplicate listener attachment
    instances.set(riotIdInput, () => {});

    let debounceTimer = null;
    let activeIndex = -1; // Declare at top to avoid hoisting issues
    const heroSection = riotIdInput?.closest('.hero');
    const searchGroup = riotIdInput.closest('.search-group');
    const searchForm = riotIdInput.closest('form');
    let isDestroyed = false;
    let currentAbortController = null; // For canceling in-flight requests
    let justOpened = false;
    let justOpenedTimer = null;

    // Flag input for screen readers to know suggestion list visibility
    const setExpandedState = (expanded) => {
        riotIdInput.setAttribute('aria-expanded', expanded ? 'true' : 'false');
        if (!expanded) {
            riotIdInput.removeAttribute('aria-activedescendant');
        }
    };

    // Signal async work so AT users get progress feedback
    const setBusyState = (busy) => {
        if (busy) {
            riotIdInput.setAttribute('aria-busy', 'true');
        } else {
            riotIdInput.removeAttribute('aria-busy');
        }
    };

    /**
     * Positions the suggestions dropdown relative to the search input
     * @param {boolean} force - Force repositioning even if dropdown is not visible
     */
    // Lightweight positioning hook: we rely mostly on CSS but ensure hero section styling updates
    function positionDropdown(force = false) {
        if (!suggestionsContainer || !searchGroup) return;
        if (!force && suggestionsContainer.style.display !== 'block') return;

        // rely on CSS (left/right/top in stylesheet) and keep dropdown anchored inside search group
        suggestionsContainer.style.removeProperty('position');
        suggestionsContainer.style.removeProperty('left');
        suggestionsContainer.style.removeProperty('top');
        suggestionsContainer.style.removeProperty('width');
        suggestionsContainer.style.removeProperty('min-width');
        suggestionsContainer.style.removeProperty('max-width');
        suggestionsContainer.style.removeProperty('z-index');

        if (suggestionsContainer.style.display === 'block') {
            heroSection?.classList.add(CSS_CLASSES.DROPDOWN_OPEN);
        }

        const groupRect = searchGroup?.getBoundingClientRect();
        if (groupRect && groupRect.width) {
            suggestionsContainer.style.width = `${groupRect.width}px`;
            suggestionsContainer.style.minWidth = `${groupRect.width}px`;
        }
    }

    // Reset inline overrides so layout reverts to stylesheet defaults
    function resetDropdownPlacement() {
        if (!suggestionsContainer) return;
        suggestionsContainer.style.removeProperty('position');
        suggestionsContainer.style.removeProperty('left');
        suggestionsContainer.style.removeProperty('top');
        suggestionsContainer.style.removeProperty('width');
        suggestionsContainer.style.removeProperty('min-width');
        suggestionsContainer.style.removeProperty('max-width');
        suggestionsContainer.style.removeProperty('z-index');
        heroSection?.classList.remove(CSS_CLASSES.DROPDOWN_OPEN);
    }

    // Guard against multiple concurrent fetches when users type quickly
    function cancelInFlightRequest() {
        if (!currentAbortController) return;
        try {
            currentAbortController.abort();
        } catch (e) {
            console.debug('AbortController abort failed:', e);
        } finally {
            // Ensure cleanup even if abort fails
            currentAbortController = null;
        }
    }

    // Remove keyboard focus styling from all suggestions
    function clearActiveState() {
        if (!suggestionsContainer) return;
        suggestionsContainer.querySelectorAll('[role="option"]').forEach(el => {
            el.classList.remove('active');
            el.setAttribute('aria-selected', 'false');
            el.setAttribute('tabindex', '-1');
        });
        riotIdInput.removeAttribute('aria-activedescendant');
        activeIndex = -1;
    }

    // Close dropdown and clean timers/network state
    function hideSuggestions() {
        cancelInFlightRequest();
        suggestionsContainer.innerHTML = '';
        suggestionsContainer.style.display = 'none';
        setExpandedState(false);
        clearActiveState();
        resetDropdownPlacement();
        setBusyState(false);
        justOpened = false;
        if (justOpenedTimer) {
            clearTimeout(justOpenedTimer);
            justOpenedTimer = null;
        }
    }

    // Cleanup function to prevent memory leaks
    // Idempotent cleanup entry used by higher level teardown
    const cleanup = () => {
        isDestroyed = true;
        if (debounceTimer) {
            clearTimeout(debounceTimer);
            debounceTimer = null;
        }
        if (justOpenedTimer) {
            clearTimeout(justOpenedTimer);
            justOpenedTimer = null;
        }
        hideSuggestions();
    };

    // Local history
    const LS_KEY = STORAGE_KEYS.SEARCH_HISTORY;
    // Resilient localStorage parser with format validation
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

    // Insert the latest Riot ID into history while trimming duplicates and size
    function saveLocalHistory(riotId) {
        const v = (riotId || '').trim();
        // Validate format: must contain exactly one '#' and have non-empty parts
        if (!v || !v.includes('#')) return;
        const parts = v.split('#');
        if (parts.length !== 2 || !parts[0].trim() || !parts[1].trim()) return;
        const maxSize = STORAGE_KEYS.MAX_HISTORY_SIZE;
        const list = loadLocalHistory().filter(x => x.toLowerCase() !== v.toLowerCase());
        list.unshift(v);
        while (list.length > maxSize) list.pop();
        try {
            localStorage.setItem(LS_KEY, JSON.stringify(list));
        } catch {
            // ignore quota errors
        }
    }

    // Populate dropdown with locally cached searches, optionally filtered by current query
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
            hdr.setAttribute('role', 'presentation');
            hdr.setAttribute('tabindex', '-1');
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
            bindOptionInteraction(item, () => commitSelection(riotId));
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
                suggestionsContainer.style.display = 'block';
                setExpandedState(true);
            } else {
                hideSuggestions();
                return;
            }
        } else {
            suggestionsContainer.style.display = 'block';
            setExpandedState(true);
        }
        positionDropdown();
    }

    function appendRemoteSuggestions(suggestions, normalizedRequestedQuery) {
        if (isDestroyed) return;
        const normalizedCurrentValue = (riotIdInput.value ?? '').trim().toLowerCase();
        if (normalizedCurrentValue !== normalizedRequestedQuery) {
            return;
        }

        if (!suggestions || !Array.isArray(suggestions) || suggestions.length === 0) {
            setExpandedState(suggestionsContainer.querySelectorAll('[role="option"]').length > 0);
            return;
        }

        suggestionsContainer.querySelectorAll('.suggestion-empty').forEach(el => el.remove());
        clearActiveState();

        const hdr = document.createElement('div');
        hdr.className = 'suggestion-section';
        hdr.textContent = 'Suggestions';
        hdr.setAttribute('role', 'presentation');
        hdr.setAttribute('tabindex', '-1');
        suggestionsContainer.appendChild(hdr);

        let idx = suggestionsContainer.querySelectorAll('[role="option"]').length;
        suggestions.forEach(suggestion => {
            if (!suggestion || !suggestion.riotId) return;

            const item = document.createElement('a');
            item.href = '#';
            item.classList.add('list-group-item', 'list-group-item-action', 'd-flex', 'align-items-center');
            item.setAttribute('role', 'option');
            const optId = `suggestion-opt-${idx++}`;
            item.id = optId;
            item.setAttribute('aria-selected', 'false');

            const img = document.createElement('img');
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

            bindOptionInteraction(item, () => commitSelection(suggestion.riotId));
            suggestionsContainer.appendChild(item);
        });

        suggestionsContainer.style.display = 'block';
        setExpandedState(suggestionsContainer.querySelectorAll('[role="option"]').length > 0);
        positionDropdown(true);
    }

    // Debounced remote fetch layered on top of local history suggestions
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
            setBusyState(false);
            return;
        }

        const useCache = CACHE_DURATION.SUGGESTIONS > 0;
        if (useCache) {
            const cached = suggestionCache.get(normalizedRequestedQuery);
            if (cached && (Date.now() - cached.timestamp) <= CACHE_DURATION.SUGGESTIONS) {
                appendRemoteSuggestions(cached.data, normalizedRequestedQuery);
                setBusyState(false);
                return;
            }
        }

        debounceTimer = setTimeout(() => {
            if (isDestroyed) return;
            cancelInFlightRequest();
            currentAbortController = new AbortController();
            const thisAbortController = currentAbortController;
            setBusyState(true);

            fetch(`${URLS.API.SUGGESTIONS}?query=${encodeURIComponent(requestedQuery)}`, {
                signal: thisAbortController.signal
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(suggestions => {
                    if (useCache) {
                        suggestionCache.set(normalizedRequestedQuery, {
                            timestamp: Date.now(),
                            data: Array.isArray(suggestions) ? [...suggestions] : []
                        });
                    }
                    appendRemoteSuggestions(suggestions, normalizedRequestedQuery);
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
                    setBusyState(false);
                });
        }, DEBOUNCE_DELAYS.SEARCH);
    }

    // Finalize selection: populate input, close dropdown, submit form if available
    function commitSelection(value) {
        const trimmed = (value ?? '').toString().trim();
        if (!trimmed) {
            hideSuggestions();
            return;
        }
        const riotIdPattern = /^[^#\s]+#[A-Za-z0-9]{3,5}$/i;
        if (!riotIdPattern.test(trimmed)) {
            hideSuggestions();
            return;
        }
        riotIdInput.value = trimmed;
        hideSuggestions();
        if (searchForm) {
            if (typeof searchForm.requestSubmit === 'function') {
                searchForm.requestSubmit();
            } else {
                searchForm.submit();
            }
        }
    }

    // Attach click/keyboard handlers for individual suggestion rows
    function bindOptionInteraction(element, onSelect) {
        if (!element) return;
        element.setAttribute('aria-selected', 'false');
        element.setAttribute('tabindex', '-1');
        element.addEventListener('click', evt => {
            evt.preventDefault();
            onSelect();
        });
        element.addEventListener('keydown', evt => {
            if (evt.key === 'Enter' || evt.key === ' ' || evt.key === 'Spacebar') {
                evt.preventDefault();
                onSelect();
            }
        });
    }

    // Save the query synchronously so dropdown can reflect latest history after submit
    const handleFormSubmit = () => {
        const q = riotIdInput?.value || '';
        saveLocalHistory(q);
        setBusyState(false);
    };

    // Centralized open routine used by multiple event sources
    const openSuggestions = () => {
        if (isDestroyed) return;
        fetchAndDisplaySuggestions(riotIdInput.value);
        positionDropdown(true);
        justOpened = true;
        if (justOpenedTimer) {
            clearTimeout(justOpenedTimer);
        }
        justOpenedTimer = setTimeout(() => {
            justOpened = false;
            justOpenedTimer = null;
        }, DEBOUNCE_DELAYS.JUST_OPENED);
    };

    const handleInput = () => {
        openSuggestions();
    };

    const handleFocus = () => {
        openSuggestions();
    };

    const handlePointerDown = () => {
        openSuggestions();
    };

    const handleClick = () => {
        openSuggestions();
    };

    const handleBlur = () => {
        // Use requestAnimationFrame for better timing consistency
        requestAnimationFrame(() => {
            if (isDestroyed) return;
            const evaluateBlur = () => {
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
            };

            if (justOpened) {
                setTimeout(() => {
                    justOpened = false;
                    evaluateBlur();
                }, Math.max(DEBOUNCE_DELAYS.JUST_OPENED, 100));
                return;
            }

            evaluateBlur();
        });
    };

    // Keyboard navigation
    // Highlight a suggestion while maintaining ARIA attributes and focus order
    function updateActive(toIndex) {
        const items = Array.from(suggestionsContainer.querySelectorAll('[role="option"]'));
        items.forEach((el, i) => {
            if (i === toIndex) {
                el.classList.add('active');
                el.setAttribute('aria-selected', 'true');
                el.setAttribute('tabindex', '0');
                riotIdInput.setAttribute('aria-activedescendant', el.id);
                try {
                    el.focus({ preventScroll: true });
                } catch {
                    el.focus();
                }
            } else {
                el.classList.remove('active');
                el.setAttribute('aria-selected', 'false');
                el.setAttribute('tabindex', '-1');
            }
        });
    }

    // Normalize keyboard interactions for list navigation, selection, and dismissal
    const handleKeydown = e => {
        const items = Array.from(suggestionsContainer.querySelectorAll('[role="option"]'));
        if (e.key === 'Escape') {
            hideSuggestions();
            return;
        }
        if (e.key === 'Tab') {
            if (!e.shiftKey && items.length) {
                const indexToUse = activeIndex >= 0 ? activeIndex : 0;
                if (items[indexToUse]) {
                    e.preventDefault();
                    items[indexToUse].click();
                    return;
                }
            }
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

    // Close dropdown when clicking/focusing outside the search experience
    const handleDocumentInteraction = event => {
        const target = event.target;
        const isInsideInput = target === riotIdInput || riotIdInput.contains?.(target);
        const isInsideGroup = searchGroup?.contains(target);
        const isInsideDropdown = suggestionsContainer.contains(target);
        let isInsideModal = false;
        if (target && typeof target.closest === 'function') {
            try {
                isInsideModal = Boolean(target.closest('.modal'));
            } catch {
                isInsideModal = false;
            }
        }

        if (justOpened) return;
        if (!isInsideInput && !isInsideGroup && !isInsideDropdown && !isInsideModal) {
            hideSuggestions();
        }
    };

    // Prevent duplicate initialization
    if (instances.has(riotIdInput)) {
        console.warn('Search dropdown already initialized for this element, skipping');
        return instances.get(riotIdInput);
    }

    document.addEventListener(EVENTS.POINTERDOWN, handleDocumentInteraction);
    document.addEventListener(EVENTS.FOCUSIN, handleDocumentInteraction);
    searchForm?.addEventListener(EVENTS.SUBMIT, handleFormSubmit);
    riotIdInput.addEventListener(EVENTS.POINTERDOWN, handlePointerDown);
    riotIdInput.addEventListener(EVENTS.CLICK, handleClick);
    riotIdInput.addEventListener(EVENTS.INPUT, handleInput);
    riotIdInput.addEventListener(EVENTS.FOCUS, handleFocus);
    riotIdInput.addEventListener(EVENTS.BLUR, handleBlur);
    riotIdInput.addEventListener(EVENTS.KEYDOWN, handleKeydown);

    // Public cleanup that detaches DOM listeners and releases instance handle
    const fullCleanup = () => {
        cleanup();
        document.removeEventListener(EVENTS.POINTERDOWN, handleDocumentInteraction);
        document.removeEventListener(EVENTS.FOCUSIN, handleDocumentInteraction);
        searchForm?.removeEventListener(EVENTS.SUBMIT, handleFormSubmit);
        riotIdInput.removeEventListener(EVENTS.POINTERDOWN, handlePointerDown);
        riotIdInput.removeEventListener(EVENTS.CLICK, handleClick);
        riotIdInput.removeEventListener(EVENTS.INPUT, handleInput);
        riotIdInput.removeEventListener(EVENTS.FOCUS, handleFocus);
        riotIdInput.removeEventListener(EVENTS.BLUR, handleBlur);
        riotIdInput.removeEventListener(EVENTS.KEYDOWN, handleKeydown);
        instances.delete(riotIdInput);
    };

    // Store instance for future reference
    instances.set(riotIdInput, fullCleanup);

    // Return cleanup function to allow proper teardown
    return fullCleanup;
}
