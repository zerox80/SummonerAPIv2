// Main application initialization
import { initAutofocus } from './autofocus.js';
import { setupImageFallbacks } from './image-fallbacks.js';
import { initHistoryFiltersFallback } from './history-filters.js';

// Autofocus on the riotId input field when the page loads
document.addEventListener('DOMContentLoaded', function initMainAutofocus() {
    initAutofocus();
}, { once: true });

// Fix any garbled separator characters in template rendering
try {
    document.querySelectorAll('span.text-muted.mx-2').forEach(function(el) {
        if (el && /\uFFFD|�/.test(el.textContent)) {
            el.textContent = '•';
        }
    });
} catch (e) { /* safe no-op */ }

// Global image error fallbacks
setupImageFallbacks();

// History filter fallback
document.addEventListener('DOMContentLoaded', function initHistoryFallback() {
    initHistoryFiltersFallback();
}, { once: true });
