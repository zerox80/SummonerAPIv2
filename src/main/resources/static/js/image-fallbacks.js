/**
 * Image error fallback handling
 * Provides graceful degradation for missing images
 */
import { SELECTORS } from './constants.js';

export function initImageFallbacks() {
    if (document.body.hasAttribute('data-img-fallback-installed')) return;
    document.body.setAttribute('data-img-fallback-installed', 'true');

    try {
        // 1x1 transparent PNG (data URI) to replace broken images if we prefer not to hide
        const TRANSPARENT_PNG = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQIW2NkYGBgAAAABQABhU1aWQAAAABJRU5ErkJggg==';

        function attachImgFallback(selector, mode) {
            document.querySelectorAll(selector).forEach(function(img) {
                // Guard: avoid adding multiple listeners
                if (img.__fallbackBound) return;
                img.__fallbackBound = true;
                img.addEventListener('error', function() {
                    if (mode === 'hide') {
                        img.style.display = 'none';
                    } else if (mode === 'transparent') {
                        // Replace src with transparent pixel only once to avoid infinite loop
                        const fallbackCount = parseInt(img.getAttribute('data-fallback-count') || '0', 10);
                        if (fallbackCount < 1) {
                            img.setAttribute('data-fallback-count', '1');
                            // Validate TRANSPARENT_PNG before applying
                            if (TRANSPARENT_PNG && TRANSPARENT_PNG.startsWith('data:image/png;base64,')) {
                                img.src = TRANSPARENT_PNG;
                            } else {
                                img.style.display = 'none';
                            }
                        } else {
                            img.style.display = 'none';
                        }
                    }
                }, { once: false });
            });
        }

        // Local tier emblem icons may be missing; use non-breaking fallback
        attachImgFallback('.tier-icon', 'transparent');
        // Locally referenced champion icons (e.g., /ui/champions/Name.png) may be missing; hide gracefully
        attachImgFallback('.js-champ-fallback', 'hide');
    } catch (e) {
        // no-op; never break UI
    }
}

// Run immediately
export function setupImageFallbacks() {
    initImageFallbacks();

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            // Re-run for any images added after initial load
            initImageFallbacks();
        });
    }
}
