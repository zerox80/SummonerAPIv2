// Simple client-side filtering for champions list
(function(){
  function normalize(s){ return (s||'').toLowerCase(); }
  function hasTag(tagsCSV, tag){
    const tags = (tagsCSV||'').split(',').map(t=>t.trim());
    return tags.includes(tag);
  }

  function applyFilters(){
    const q = normalize(document.getElementById('champSearch')?.value || '');
    const activeBtn = document.querySelector('.btn-group [data-role].active');
    const role = activeBtn ? activeBtn.getAttribute('data-role') : 'all';

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
    document.querySelectorAll('.btn-group [data-role]').forEach(btn => {
      btn.addEventListener('click', function(e){
        e.preventDefault();
        document.querySelectorAll('.btn-group [data-role]').forEach(b=>b.classList.remove('active'));
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
