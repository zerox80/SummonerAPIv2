// Main application initialization
import { initSearchDropdown } from './search-dropdown.js';
import { initMatchFilters } from './match-filters.js';
import { initThemeToggle } from './theme-toggle.js';
import { initCharts } from './charts-init.js';
import { initLoadMore } from './load-more.js';

document.addEventListener('DOMContentLoaded', function () {
    // Install image error fallbacks for champion images (including dynamic content)
    // Use event delegation to handle both initial and dynamically added images
    document.body.addEventListener('error', function(e) {
        const img = e.target;
        if (img.tagName === 'IMG' && img.src.includes('/ui/champions/')) {
            img.style.display = 'none';
            const s = img.nextElementSibling;
            if (s) { 
                s.textContent = img.alt; 
                s.classList.add('text-muted','small'); 
            }
        }
    }, true); // Use capture phase to catch errors

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
