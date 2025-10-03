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
    cards.forEach(card => {
      const name = normalize(card.getAttribute('data-name'));
      const title = normalize(card.getAttribute('data-title'));
      const tags = card.getAttribute('data-tags') || '';

      const matchText = !q || name.includes(q) || title.includes(q);
      const matchRole = (role === 'all') || hasTag(tags, role);
      card.style.display = (matchText && matchRole) ? '' : 'none';
    });
  }

  function initRoleButtons(){
    document.querySelectorAll(ROLE_BUTTON_SELECTOR).forEach(btn => {
      btn.setAttribute('role', 'radio');
      btn.setAttribute('aria-pressed', btn.classList.contains('active') ? 'true' : 'false');
      btn.addEventListener('click', function(e){
        e.preventDefault();
        document.querySelectorAll(ROLE_BUTTON_SELECTOR).forEach(b => {
          b.classList.remove('active');
          b.setAttribute('aria-pressed', 'false');
        });
        this.classList.add('active');
        this.setAttribute('aria-pressed', 'true');
        applyFilters();
      });
      btn.addEventListener('keydown', function(e) {
        if (e.key === ' ' || e.key === 'Enter') {
          e.preventDefault();
          this.click();
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
