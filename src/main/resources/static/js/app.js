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
    initMatchFilters();

    // Get chart data from Thymeleaf
    const leagueEntries = window.leagueEntriesData || [];
    const matches = window.matchesData || [];
    const champLabels = window.champLabelsData || [];
    const champValues = window.champValuesData || [];

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
    const matchesPageSize = window.matchesPageSizeData || 10;
    initLoadMore(matchesPageSize);
});
