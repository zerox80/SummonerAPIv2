// Simple client-side filtering for champions list

(function(){
  const ROLE_BUTTON_SELECTOR = '.btn-group [data-role]';

  function normalize(s){
    return (s || '').toString().trim().toLowerCase();
  }

  function hasTag(tagsCSV, tag){
    if (!tagsCSV) return false;
    const expected = normalize(tag);
    return tagsCSV.split(',')
      .map(part => normalize(part))
      .filter(Boolean)
      .includes(expected);
  }

  function applyFilters(){
    const q = normalize(document.getElementById('champSearch')?.value || '');
    const activeBtn = document.querySelector(`${ROLE_BUTTON_SELECTOR}.active`);
    const role = activeBtn ? normalize(activeBtn.getAttribute('data-role')) : 'all';

    const cards = document.querySelectorAll('#champList > [data-name]');
    let visible = 0;
    cards.forEach(card => {
      const name = normalize(card.getAttribute('data-name'));
      const title = normalize(card.getAttribute('data-title'));
      const tags = card.getAttribute('data-tags') || '';

      const matchText = !q || name.includes(q) || title.includes(q);
      const matchRole = (role === 'all') || hasTag(tags, role);
      const show = (matchText && matchRole);
      card.style.display = show ? '' : 'none';
      if (show) visible++;
    });

    const statusRegion = document.querySelector('#champSearchStatus');
    if (statusRegion) {
      const base = visible === 0 ? 'No champions match your filters.' : `Showing ${visible} champion${visible === 1 ? '' : 's'}.`;
      const roleMsg = role === 'all' ? '' : ` Role filter: ${role}.`;
      statusRegion.textContent = `${base}${roleMsg}`.trim();
    }
  }

  function focusButtonAt(index, buttons){
    if (index < 0) index = buttons.length - 1;
    if (index >= buttons.length) index = 0;
    const btn = buttons[index];
    btn.focus();
    btn.click();
  }

  function initRoleButtons(){
    const buttons = Array.from(document.querySelectorAll(ROLE_BUTTON_SELECTOR));
    buttons.forEach((btn, idx) => {
      btn.setAttribute('role', 'radio');
      const isActive = btn.classList.contains('active');
      btn.setAttribute('aria-pressed', isActive ? 'true' : 'false');
      btn.setAttribute('aria-checked', isActive ? 'true' : 'false');
      btn.addEventListener('click', function(e){
        e.preventDefault();
        buttons.forEach(b => {
          b.classList.remove('active');
          b.setAttribute('aria-pressed', 'false');
          b.setAttribute('aria-checked', 'false');
        });
        this.classList.add('active');
        this.setAttribute('aria-pressed', 'true');
        this.setAttribute('aria-checked', 'true');
        applyFilters();
      });
      btn.addEventListener('keydown', function(e) {
        if (e.key === ' ' || e.key === 'Enter') {
          e.preventDefault();
          this.click();
        } else if (e.key === 'ArrowRight' || e.key === 'ArrowDown') {
          e.preventDefault();
          focusButtonAt(idx + 1, buttons);
        } else if (e.key === 'ArrowLeft' || e.key === 'ArrowUp') {
          e.preventDefault();
          focusButtonAt(idx - 1, buttons);
        }
      });
    });
  }

  // Use a more robust initialization pattern that handles both scenarios
  function initChampionsPage() {
    // Prevent duplicate initialization
    if (window.__championsPageInitialized) {
      console.warn('Champions page already initialized, skipping');
      return;
    }
    window.__championsPageInitialized = true;
    
    const input = document.getElementById('champSearch');
    if (input) {
      input.addEventListener('input', applyFilters);
    }
    initRoleButtons();
    applyFilters();
    window.addEventListener('beforeunload', () => {
      window.__championsPageInitialized = false;
    }, { once: true });
  }
  
  // Check if DOM is already loaded
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initChampionsPage);
  } else {
    // DOM is already ready, execute immediately
    initChampionsPage();
  }
})();
