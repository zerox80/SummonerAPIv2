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
      btn.addEventListener('click', function(e){
        e.preventDefault();
        document.querySelectorAll(ROLE_BUTTON_SELECTOR).forEach(b=>b.classList.remove('active'));
        this.classList.add('active');
        applyFilters();
      });
    });
  }

  document.addEventListener('DOMContentLoaded', function(){
    const input = document.getElementById('champSearch');
    if (input) {
      input.addEventListener('input', applyFilters);
    }
    initRoleButtons();
    applyFilters();
  });
})();
