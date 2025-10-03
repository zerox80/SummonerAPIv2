// Main application initialization
import { initSearchDropdown } from './search-dropdown.js';
import { initMatchFilters } from './match-filters.js';
import { initThemeToggle } from './theme-toggle.js';
import { initCharts } from './charts-init.js';
import { initLoadMore } from './load-more.js';

document.addEventListener('DOMContentLoaded', function () {
    // Image error fallbacks are handled globally in main.js via setupImageFallbacks()
    // Removed duplicate error handling to avoid conflicts

    // Initialize search dropdown with suggestions
    initSearchDropdown();

    // Initialize match filters
    let matchFiltersCleanup = null;
    try {
        matchFiltersCleanup = initMatchFilters();
    } catch (error) {
        console.error('Match filters initialization failed:', error);
    }

    // Get chart data from Thymeleaf
    const leagueEntries = Array.isArray(window.leagueEntriesData) ? window.leagueEntriesData : [];
    const matches = Array.isArray(window.matchesData) ? window.matchesData : [];
    const champLabels = Array.isArray(window.champLabelsData) ? window.champLabelsData : [];
    const champValues = Array.isArray(window.champValuesData) ? window.champValuesData : [];

    // Initialize charts and get update callback with error boundary
    let updateCharts;
    try {
        updateCharts = initCharts(leagueEntries, matches, champLabels, champValues);
    } catch (error) {
        console.error('Chart initialization failed:', error);
        updateCharts = undefined;
    }

    // Initialize theme toggle with chart update callback (may be undefined if no charts)
    // Provide a no-op function if updateCharts is undefined
    const chartCallback = (typeof updateCharts === 'function') ? updateCharts : () => {};
    try {
        initThemeToggle(chartCallback);
    } catch (error) {
        console.error('Theme toggle initialization failed:', error);
    }

    // Initialize load more functionality
    const matchesPageSizeRaw = window.matchesPageSizeData;
    const matchesPageSize = Number.isFinite(Number(matchesPageSizeRaw)) ? Number(matchesPageSizeRaw) : 10;
    try {
        initLoadMore(matchesPageSize);
    } catch (error) {
        console.error('Load more initialization failed:', error);
    }

    if (typeof matchFiltersCleanup === 'function') {
        window.addEventListener('beforeunload', () => {
            try {
                matchFiltersCleanup();
            } catch (cleanupError) {
                console.debug('Match filters cleanup failed on unload:', cleanupError);
            }
        }, { once: true });
    }
});
