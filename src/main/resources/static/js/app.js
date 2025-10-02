// Main application initialization
import { initSearchDropdown } from './search-dropdown.js';
import { initMatchFilters } from './match-filters.js';
import { initThemeToggle } from './theme-toggle.js';
import { initCharts } from './charts-init.js';
import { initLoadMore } from './load-more.js';

document.addEventListener('DOMContentLoaded', function () {
    // Install image error fallbacks for champion images
    try {
        document.querySelectorAll('img[src*="/ui/champions/"]').forEach(img => {
            img.addEventListener('error', function(){
                this.style.display = 'none';
                const s = this.nextElementSibling;
                if (s) { s.textContent = this.alt; s.classList.add('text-muted','small'); }
            }, { once: true });
        });
    } catch (_) {}

    // Initialize search dropdown with suggestions
    initSearchDropdown();

    // Initialize match filters
    initMatchFilters();

    // Get chart data from Thymeleaf
    const leagueEntries = window.leagueEntriesData || [];
    const matches = window.matchesData || [];
    const champLabels = window.champLabelsData || [];
    const champValues = window.champValuesData || [];

    // Initialize charts and get update callback
    const updateCharts = initCharts(leagueEntries, matches, champLabels, champValues);

    // Initialize theme toggle with chart update callback
    initThemeToggle(updateCharts);

    // Initialize load more functionality
    const matchesPageSize = window.matchesPageSizeData || 10;
    initLoadMore(matchesPageSize);
});
