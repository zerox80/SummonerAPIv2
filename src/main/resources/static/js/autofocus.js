/**
 * Autofocus functionality for form inputs
 * Only focuses when no search results are present
 */
export function initAutofocus() {
    const riotIdInput = document.querySelector('#riotId');

    // Only autofocus if we're on the home page without search results
    if (riotIdInput && !document.querySelector('.match-row') && !document.querySelector('.league-entry')) {
        riotIdInput.focus();
    }
}
