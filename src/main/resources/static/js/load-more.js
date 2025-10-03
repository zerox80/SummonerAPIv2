// Load more matches (client-side pagination)
export function initLoadMore(matchesPageSize = 10) {
    const matchList = document.getElementById('matchList');
    const loadBtn = document.getElementById('loadMoreBtn');
    if (!matchList || !loadBtn) return;

    loadBtn.setAttribute('aria-live', 'polite');
    loadBtn.setAttribute('aria-busy', 'false');
    
    const riotId = matchList.getAttribute('data-riotid') || '';
    const searchedPuuid = matchList.getAttribute('data-puuid') || '';
    const champSquareBase = matchList.getAttribute('data-champsquare') || '';
    const PAGE = matchesPageSize;
    let loaded = matchList.querySelectorAll('.match-row').length;
    let loadingPlaceholder = null;

    function setLoading(v){
        const sp = loadBtn.querySelector('.spinner-border');
        if (v) {
            loadBtn.setAttribute('disabled','true');
            loadBtn.setAttribute('aria-busy','true');
            sp && sp.classList.remove('d-none');
            showLoadingPlaceholder();
        } else {
            loadBtn.removeAttribute('disabled');
            loadBtn.setAttribute('aria-busy','false');
            sp && sp.classList.add('d-none');
            removeLoadingPlaceholder();
        }
    }

    function showLoadingPlaceholder(){
        if (loadingPlaceholder || !matchList) return;
        const skeleton = document.createElement('div');
        skeleton.className = 'list-group-item match-row match-row-loading';
        skeleton.setAttribute('aria-hidden','true');
        skeleton.innerHTML = `
            <div class="placeholder-glow w-100">
                <span class="placeholder col-8"></span>
                <span class="placeholder col-6 mt-2"></span>
                <span class="placeholder col-5 mt-2"></span>
            </div>`;
        matchList.appendChild(skeleton);
        loadingPlaceholder = skeleton;
    }

    function removeLoadingPlaceholder(){
        if (loadingPlaceholder && loadingPlaceholder.parentElement) {
            loadingPlaceholder.remove();
        }
        loadingPlaceholder = null;
    }

    function queueName(q){
        const map = {420:'Ranked Solo/Duo',440:'Ranked Flex',400:'Normal Draft',430:'Normal Blind',450:'ARAM',700:'Clash',1700:'Arena',1900:'URF',0:'Custom',1100:'TFT Ranked',1090:'TFT Normal',1130:'TFT Double Up',1160:'TFT Hyper Roll'};
        return map[q] || ('Queue ' + q);
    }

    function fmtDur(sec){ const m=Math.floor((sec||0)/60), s=(sec||0)%60; return `${m}m ${s}s`; }

    // Sanitize text to prevent XSS
    function sanitizeText(text) {
        if (!text) return '';
        return String(text).replace(/[<>&"']/g, (char) => {
            const entities = { '<': '&lt;', '>': '&gt;', '&': '&amp;', '"': '&quot;', "'": '&#39;' };
            return entities[char] || char;
        });
    }

    function champIcon(p){
        if (!p || !p.championName) return '';
        if (!champSquareBase) {
            console.warn('champSquareBase is not defined');
            // Fallback to Community Dragon CDN
            return `https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-icons/${p.championId || -1}.png`;
        }
        // Sanitize champion name for URL safety
        const safeName = encodeURIComponent(p.championName);
        return champSquareBase + safeName + '.png';
    }

    function buildParticipantRow(p, searched){
        if (!p) {
            console.warn('Invalid participant data');
            return document.createElement('tr');
        }
        
        const tr = document.createElement('tr');
        if (p.puuid === searched) tr.classList.add('table-info','self-row');
        const td1 = document.createElement('td');
        const img = document.createElement('img');
        const iconSrc = champIcon(p);
        if (iconSrc) {
            img.src = iconSrc;
            img.alt = p.championName || 'Champion';
            img.width=24;
            img.height=24;
            img.className='me-1 align-middle rounded';
            img.addEventListener('error', ()=>{ img.style.display='none'; });
            td1.appendChild(img);
        }
        const spanName = document.createElement('span'); spanName.textContent = p.championName || 'Champion'; td1.appendChild(spanName);
        const td2 = document.createElement('td');
        if (p.riotIdGameName && p.riotIdTagline && !(p.riotIdGameName.trim()==='' && p.riotIdTagline.trim()==='')){
            const a = document.createElement('a');
            a.className='text-break';
            a.href = `/search?riotId=${encodeURIComponent(p.riotIdGameName + '#' + p.riotIdTagline)}`;
            a.textContent = `${p.riotIdGameName}#${p.riotIdTagline}`;
            a.title = 'Search this player';
            td2.appendChild(a);
        } else {
            const span = document.createElement('span'); span.className='text-break'; span.textContent = p.summonerName || 'Unknown Player';
            td2.appendChild(span);
        }
        const td3 = document.createElement('td'); 
        td3.className='fw-medium'; 
        // Use p.kda if available (from server), otherwise construct it
        td3.textContent = p.kda || `${p.kills ?? 0}/${p.deaths ?? 0}/${p.assists ?? 0}`;
        tr.appendChild(td1); tr.appendChild(td2); tr.appendChild(td3);
        return tr;
    }

    function buildMatchRowEl(m){
        if (!m || !m.info) {
            console.warn('Invalid match data provided to buildMatchRowEl');
            const errorRow = document.createElement('div');
            errorRow.className = 'list-group-item match-row';
            errorRow.setAttribute('data-q', '0');
            errorRow.setAttribute('data-k', '0');
            errorRow.setAttribute('data-d', '0');
            errorRow.setAttribute('data-a', '0');
            errorRow.setAttribute('data-win', 'false');
            errorRow.setAttribute('data-remake', 'false');
            errorRow.textContent = 'Invalid match data';
            return errorRow;
        }
        
        const info = m.info || {}; 
        const meta = m.metadata || {}; 
        const parts = Array.isArray(info.participants) ? info.participants : [];
        const me = parts.find(p => p && p.puuid === searchedPuuid) || null;
        const gd = Number(info.gameDuration||0); 
        const isRemake = gd > 0 && gd <= 300;
        const row = document.createElement('div');
        row.className = 'list-group-item match-row';
        row.classList.add(isRemake ? 'remake' : (me && me.win ? 'win' : 'loss'));
        row.setAttribute('data-q', String(info.queueId||0));
        row.setAttribute('data-k', String(me?.kills||0));
        row.setAttribute('data-d', String(me?.deaths||0));
        row.setAttribute('data-a', String(me?.assists||0));
        row.setAttribute('data-win', String(!!(me && me.win)));
        row.setAttribute('data-remake', String(isRemake));

        const hiddenIdx = document.createElement('span'); hiddenIdx.className='d-none search-index';
        if (me?.championName) {
            const idxSpan = document.createElement('span'); 
            idxSpan.textContent = me.championName; 
            hiddenIdx.appendChild(idxSpan);
        }
        parts.forEach(p=>{ 
            if (!p || !p.riotIdGameName || !p.riotIdTagline) return;
            const s=document.createElement('span'); 
            s.textContent = `${p.riotIdGameName}#${p.riotIdTagline}`; 
            hiddenIdx.appendChild(s); 
        });
        row.appendChild(hiddenIdx);

        const top = document.createElement('div'); top.className='d-flex w-100 justify-content-between';
        const h5 = document.createElement('h5'); h5.className='mb-1';
        const qLbl = document.createElement('span'); qLbl.textContent = queueName(info.queueId||0);
        h5.appendChild(qLbl);
        const dur = document.createElement('span'); dur.textContent = ` (${fmtDur(gd)})`; h5.appendChild(dur);
        top.appendChild(h5);
        const right = document.createElement('div'); right.className='d-flex align-items-center';
        const badge = document.createElement('span'); badge.className = 'badge rounded-pill px-3 py-2 fw-semibold';
        badge.textContent = isRemake ? 'Remake' : ((me && me.win) ? 'Victory' : 'Defeat');
        if (isRemake) {
            badge.classList.add('bg-secondary-subtle','text-secondary-emphasis');
        } else if (me && me.win) {
            badge.classList.add('bg-success-subtle','text-success-emphasis');
        } else {
            badge.classList.add('bg-danger-subtle','text-danger-emphasis');
        }
        right.appendChild(badge);
        if (!isRemake && typeof info.lpChange === 'number'){
            const lp = document.createElement('span');
            const sign = info.lpChange > 0 ? '+' : '';
            lp.textContent = `${sign}${info.lpChange} LP`;
            lp.className = info.lpChange>0 ? 'lp-badge gain ms-2' : (info.lpChange<0 ? 'lp-badge loss ms-2' : 'lp-badge neutral ms-2');
            right.appendChild(lp);
        }
        top.appendChild(right); row.appendChild(top);

        const pMeta = document.createElement('p'); pMeta.className='mb-2 match-details-summary';
        const small1 = document.createElement('small'); 
        // Sanitize match ID to prevent XSS
        const sanitizedMatchId = meta?.matchId ? String(meta.matchId).replace(/[<>&"']/g, '') : 'N/A';
        small1.textContent = `Match ID: ${sanitizedMatchId}`; 
        pMeta.appendChild(small1);
        const sep = document.createTextNode(' • '); pMeta.appendChild(sep);
        const small2 = document.createElement('small'); 
        try { 
            if (info.gameEndTimestamp) { 
                const d = new Date(info.gameEndTimestamp); 
                small2.textContent = d.toLocaleString(); 
            } else {
                small2.textContent = '--';
            }
        } catch {
            small2.textContent = '--';
        }
        pMeta.appendChild(small2);
        row.appendChild(pMeta);

        if (me){
            const perf = document.createElement('div'); perf.className='mb-3 p-2 player-performance';
            const strong = document.createElement('strong'); strong.textContent='Your Performance:'; perf.appendChild(strong);
            const img = document.createElement('img'); img.src = champIcon(me); img.alt = me.championName || 'Champ'; img.width=32; img.height=32; img.className='me-1 align-middle rounded'; img.addEventListener('error',()=>{ img.style.display='none'; }); perf.appendChild(img);
            const name = document.createElement('span'); name.className='fw-medium'; name.textContent = me.championName || 'Champ'; perf.appendChild(name);
            const dash = document.createTextNode(' - '); perf.appendChild(dash);
            const kda = document.createElement('span'); kda.className='fw-bold'; kda.textContent = `${me.kills ?? 0}/${me.deaths ?? 0}/${me.assists ?? 0}`; perf.appendChild(kda);
            row.appendChild(perf);
        }

        if (parts.length){
            const details = document.createElement('details'); details.className='mt-2 w-100';
            const summary = document.createElement('summary'); summary.className='btn btn-sm btn-participants'; 
            summary.textContent = 'Show All Participants ';
            const icon = document.createElement('i');
            icon.className = 'fas fa-users fa-xs';
            icon.setAttribute('aria-hidden', 'true');
            summary.appendChild(icon);
            details.appendChild(summary);
            
            // Blue Team with proper table structure
            const blueH = document.createElement('h6'); blueH.className='mt-3 text-primary'; blueH.textContent='Blue Team'; details.appendChild(blueH);
            const blueDiv = document.createElement('div'); blueDiv.className='table-responsive';
            const blueTable = document.createElement('table'); blueTable.className='table table-sm table-hover mt-2 match-table team-blue-table table-borderless align-middle';
            const blueThead = document.createElement('thead');
            const blueHeaderRow = document.createElement('tr');
            ['Champion', 'Player', 'KDA'].forEach(text => {
                const th = document.createElement('th');
                th.scope = 'col';
                th.textContent = text;
                blueHeaderRow.appendChild(th);
            });
            blueThead.appendChild(blueHeaderRow);
            blueTable.appendChild(blueThead);
            const blueTbody = document.createElement('tbody'); 
            // Normalize teamId to number for consistent comparison
            parts.filter(p=>p && Number(p.teamId) === 100).forEach(p=>blueTbody.appendChild(buildParticipantRow(p,searchedPuuid))); 
            blueTable.appendChild(blueTbody);
            blueDiv.appendChild(blueTable);
            details.appendChild(blueDiv);
            
            // Red Team with proper table structure
            const redH = document.createElement('h6'); redH.className='mt-3 text-danger'; redH.textContent='Red Team'; details.appendChild(redH);
            const redDiv = document.createElement('div'); redDiv.className='table-responsive';
            const redTable = document.createElement('table'); redTable.className='table table-sm table-hover mt-2 match-table team-red-table table-borderless align-middle';
            const redThead = document.createElement('thead');
            const redHeaderRow = document.createElement('tr');
            ['Champion', 'Player', 'KDA'].forEach(text => {
                const th = document.createElement('th');
                th.scope = 'col';
                th.textContent = text;
                redHeaderRow.appendChild(th);
            });
            redThead.appendChild(redHeaderRow);
            redTable.appendChild(redThead);
            const redTbody = document.createElement('tbody'); 
            // Normalize teamId to number for consistent comparison
            parts.filter(p=>p && Number(p.teamId) === 200).forEach(p=>redTbody.appendChild(buildParticipantRow(p,searchedPuuid))); 
            redTable.appendChild(redTbody);
            redDiv.appendChild(redTable);
            details.appendChild(redDiv);
            
            row.appendChild(details);
        }
        return row;
    }

    let recentUpdateCache = { isValid: false, timestamp: 0 };
    const CACHE_DURATION = 5000; // 5 seconds
    
    function updateRecentFromDom(){
        const body = document.getElementById('recentChampsBody');
        const countEl = document.getElementById('recentCount');
        if (!body || !countEl) return;
        
        try {
            // Use cache if recent enough
            const now = Date.now();
            if (recentUpdateCache.isValid && (now - recentUpdateCache.timestamp) < CACHE_DURATION) {
                return;
            }
            
            // Use cached reference to avoid re-querying DOM
            const rows = Array.from(matchList?.querySelectorAll('.match-row') || []);
            const counts = new Map();
            rows.forEach(r => {
                const nameEl = r.querySelector('.search-index span');
                const name = (nameEl?.textContent || '').trim();
                if (name) counts.set(name, (counts.get(name) || 0) + 1);
            });
            const top = Array.from(counts.entries()).sort((a,b)=>b[1]-a[1]).slice(0,5);
            body.innerHTML = '';
            if (top.length === 0) {
                const p = document.createElement('p'); p.className='text-muted mb-0'; p.textContent='No champion data from recent matches to display.';
                body.appendChild(p);
            } else {
                const ul = document.createElement('ul'); ul.className='list-unstyled mb-0';
                top.forEach(([name, cnt]) => {
                    const li = document.createElement('li'); li.className='d-flex align-items-center mb-2';
                    const img = document.createElement('img'); 
                    // Sanitize champion name for URL
                    img.src = champSquareBase + encodeURIComponent(name) + '.png'; 
                    img.alt = name; 
                    img.className='me-2 rounded recent-champ-icon'; 
                    img.loading='lazy';
                    img.addEventListener('error', ()=>{ img.style.display='none'; });
                    li.appendChild(img);
                    const sName = document.createElement('span'); sName.className='fw-medium'; sName.textContent = name; li.appendChild(sName);
                    const dot = document.createElement('span'); dot.className='text-muted mx-2'; dot.textContent='•'; li.appendChild(dot);
                    const sCnt = document.createElement('span'); sCnt.textContent = String(cnt); li.appendChild(sCnt);
                    const sGames = document.createElement('span'); sGames.className='ms-1 text-muted'; sGames.textContent='games'; li.appendChild(sGames);
                    ul.appendChild(li);
                });
                body.appendChild(ul);
            }
            countEl.textContent = String(rows.length);
            
            // Update cache
            recentUpdateCache = { isValid: true, timestamp: now };
        } catch(err) {
            console.warn('Failed to update recent champions:', err);
            // Invalidate cache on error and show fallback
            recentUpdateCache = { isValid: false, timestamp: 0 };
            if (body) {
                const errorMsg = document.createElement('p');
                errorMsg.className = 'text-muted mb-0';
                errorMsg.textContent = 'Unable to display recent champions.';
                body.innerHTML = '';
                body.appendChild(errorMsg);
            }
        }
    }

    let loadMoreAttempts = 0;
    const MAX_LOAD_ATTEMPTS = 3;
    
    async function loadMore(){
        if (!riotId) return;
        setLoading(true);
        try {
            recentUpdateCache = { isValid: false, timestamp: 0 };
            const res = await fetch(`/api/matches?riotId=${encodeURIComponent(riotId)}&start=${loaded}&count=${PAGE}`);
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            const arr = await res.json();
            if (!Array.isArray(arr) || arr.length === 0){
                loadBtn.setAttribute('disabled','true');
                loadBtn.setAttribute('aria-label', 'No more matches');
                const label = loadBtn.querySelector('.label');
                if (label) {
                    label.textContent = 'No more matches';
                }
                return;
            }
            const frag = document.createDocumentFragment();
            arr.forEach(m => frag.appendChild(buildMatchRowEl(m)));
            matchList.appendChild(frag);
            loaded = matchList.querySelectorAll('.match-row').length;
            loadMoreAttempts = 0; // Reset on success
            if (typeof window.refreshMatchRows === 'function') window.refreshMatchRows();
            updateRecentFromDom();
        } catch(err){ 
            console.error('Load more failed:', err);
            loadMoreAttempts++;
            if (loadMoreAttempts >= MAX_LOAD_ATTEMPTS) {
                loadBtn.setAttribute('disabled','true');
                loadBtn.setAttribute('aria-label', 'Error loading matches');
                const label = loadBtn.querySelector('.label');
                if (label) {
                    label.textContent = 'Error loading matches';
                }
            } else {
                // Show retry indicator
                loadBtn.setAttribute('aria-label', `Load more (attempt ${loadMoreAttempts + 1} of ${MAX_LOAD_ATTEMPTS})`);
            }
        } finally {
            setLoading(false);
        }
    }

    loadBtn.addEventListener('click', function(e){ e.preventDefault(); loadMore(); });
    updateRecentFromDom();
}
