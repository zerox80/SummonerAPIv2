// Chart initialization and management
export function initCharts(leagueEntries, matches, champLabels, champValues) {
    // Destroy existing charts to prevent memory leaks
    if (window.__chartInstances && Array.isArray(window.__chartInstances)) {
        window.__chartInstances.forEach(ch => {
            if (ch && typeof ch.destroy === 'function') {
                try {
                    ch.destroy();
                } catch(e) {
                    console.debug('Chart destroy failed:', e);
                }
            }
        });
    }
    const charts = [];
    window.__chartInstances = charts;
    const bodyEl = document.body;
    const isDark = () => bodyEl.classList.contains('theme-dark');
    const chartColors = () => ({
        text: isDark() ? 'rgba(255,255,255,0.8)' : 'rgba(0,0,0,0.7)',
        grid: isDark() ? 'rgba(255,255,255,0.08)' : 'rgba(0,0,0,0.08)'
    });
    const fmt = (ts) => {
        try {
            const d = new Date(ts);
            return d.toLocaleString(undefined, { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' });
        } catch { return String(ts); }
    };

    function buildLpSeries(queueId, queueTypeKey) {
        const filtered = matches
            .filter(m => m.q === queueId && m.d !== null && typeof m.t === 'number')
            .sort((a,b) => a.t - b.t);
        if (filtered.length === 0) return null;

        const currentEntry = leagueEntries.find(e => e.queueType === queueTypeKey);
        const deltas = filtered.map(m => m.d);
        const times = filtered.map(m => m.t);

        if (currentEntry && typeof currentEntry.lp === 'number') {
            let cur = currentEntry.lp;
            if (typeof cur !== 'number' || Number.isNaN(cur)) cur = 0;
            const abs = new Array(deltas.length);
            for (let i = deltas.length - 1; i >= 0; i--) { 
                abs[i] = cur; 
                const delta = deltas[i] ?? 0;
                cur -= (typeof delta === 'number' && !Number.isNaN(delta)) ? delta : 0;
            }
            return { times, values: abs };
        } else {
            let sum = 0; 
            const rel = deltas.map(d => (sum += (d ?? 0)));
            // Validate that we have meaningful data
            const hasNonZero = rel.some(v => v !== 0 && !Number.isNaN(v));
            if (!hasNonZero) return null;
            return { times, values: rel };
        }
    }

    const solo = buildLpSeries(420, 'RANKED_SOLO_5x5');
    const flex = buildLpSeries(440, 'RANKED_FLEX_SR');
    const lpCtx = document.getElementById('lpChart');
    const lpContainer = lpCtx ? lpCtx.parentElement : null;
    
    if (lpCtx && (solo || flex)) {
        if (lpContainer) {
            lpCtx.classList.remove('d-none');
            lpContainer.querySelectorAll('.chart-empty-msg').forEach(node => node.remove());
        }
        const colors = chartColors();
        const allTimes = Array.from(new Set([...(solo?.times||[]), ...(flex?.times||[])].filter(t => t != null && typeof t === 'number' && !Number.isNaN(t)))).sort((a,b)=>a-b);
        const labels = allTimes.map(fmt);
        
        function align(series) {
            if (!series) return new Array(allTimes.length).fill(null);
            return allTimes.map(t => {
                const idx = series.times.indexOf(t);
                return idx >= 0 ? series.values[idx] : null;
            });
        }
        
        const ds = [];
        if (solo) ds.push({ label: 'Solo/Duo', data: align(solo), borderColor: '#C8AA6E', backgroundColor: 'rgba(200,170,110,0.2)', tension: .25, spanGaps: true });
        if (flex) ds.push({ label: 'Flex', data: align(flex), borderColor: '#0AC8B9', backgroundColor: 'rgba(10,200,185,0.2)', tension: .25, spanGaps: true });

        try {
            const lpChart = new Chart(lpCtx, {
                type: 'line',
                data: { labels, datasets: ds },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        x: { ticks: { color: colors.text }, grid: { color: colors.grid } },
                        y: { ticks: { color: colors.text }, grid: { color: colors.grid } }
                    },
                    plugins: { legend: { labels: { color: colors.text } } }
                }
            });
            if (lpChart) charts.push(lpChart);
        } catch (error) {
            console.error('Failed to create LP chart:', error);
            lpCtx.parentElement?.insertAdjacentHTML('beforeend', '<p class="text-danger small mt-2">Chart rendering failed</p>');
        }
    } else if (lpCtx && lpContainer) {
        lpCtx.classList.add('d-none');
        if (!lpContainer.querySelector('.chart-empty-msg')) {
            const msg = document.createElement('p');
            msg.className = 'text-muted small chart-empty-msg mb-0';
            msg.textContent = 'No ranked LP data available for recent matches yet.';
            lpContainer.appendChild(msg);
        }
    }

    const champCtx = document.getElementById('championChart');
    const champContainer = champCtx ? champCtx.parentElement : null;
    if (champCtx && champLabels.length > 0) {
        if (champContainer) {
            champCtx.classList.remove('d-none');
            champContainer.querySelectorAll('.chart-empty-msg').forEach(node => node.remove());
        }
        const colors = chartColors();
        const combined = champLabels.map((l,i)=>({ label:l, value: champValues[i]||0 }));
        combined.sort((a,b)=>b.value-a.value);
        const top = combined.slice(0,8);
        const otherTotal = combined.slice(8).reduce((s,c)=>s+c.value,0);
        if (otherTotal>0) top.push({ label:'Other', value: otherTotal });
        const palette = ['#C8AA6E','#0AC8B9','#1E90FF','#A78BFA','#34D399','#F59E0B','#F472B6','#60A5FA','#FFD166','#06D6A0'];
        try {
            const champChart = new Chart(champCtx, {
                type: 'doughnut',
                data: { labels: top.map(x=>x.label), datasets: [{ data: top.map(x=>x.value), backgroundColor: top.map((_,i)=>palette[i%palette.length]) }] },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    radius: '80%',
                    cutout: '65%',
                    layout: { padding: 8 },
                    plugins: { legend: { labels: { color: colors.text } } }
                }
            });
            if (champChart) charts.push(champChart);
        } catch (error) {
            console.error('Failed to create champion chart:', error);
            champCtx.parentElement?.insertAdjacentHTML('beforeend', '<p class="text-danger small mt-2">Chart rendering failed</p>');
        }
    } else if (champCtx && champContainer) {
        champCtx.classList.add('d-none');
        if (!champContainer.querySelector('.chart-empty-msg')) {
            const msg = document.createElement('p');
            msg.className = 'text-muted small chart-empty-msg mb-0';
            msg.textContent = 'No champion distribution data available yet.';
            champContainer.appendChild(msg);
        }
    }

    // Resize charts when collapsible sections are shown
    try {
        document.querySelectorAll('.collapse').forEach(el => {
            el.addEventListener('shown.bs.collapse', () => {
                // Use requestAnimationFrame for better timing with CSS transitions
                requestAnimationFrame(() => {
                    requestAnimationFrame(() => {
                        charts.forEach(ch => {
                            if (ch && typeof ch.resize === 'function') {
                                try {
                                    ch.resize();
                                } catch (e) {
                                    console.debug('Chart resize failed:', e);
                                }
                            }
                        });
                    });
                });
            });
        });
    } catch (e) {
        console.debug('Failed to attach collapse listeners:', e);
    }

    // Return update function for theme changes
    return function updateChartsTheme() {
        if (!charts || charts.length === 0) return;
        
        const colors = chartColors();
        charts.forEach(ch => {
            if (!ch) return;
            try {
                // Safely update chart options
                if (ch.options?.scales?.x?.ticks) ch.options.scales.x.ticks.color = colors.text;
                if (ch.options?.scales?.x?.grid) ch.options.scales.x.grid.color = colors.grid;
                if (ch.options?.scales?.y?.ticks) ch.options.scales.y.ticks.color = colors.text;
                if (ch.options?.scales?.y?.grid) ch.options.scales.y.grid.color = colors.grid;
                if (ch.options?.plugins?.legend?.labels) ch.options.plugins.legend.labels.color = colors.text;
                
                // Use update with 'none' mode for immediate effect without animation
                if (typeof ch.update === 'function') {
                    ch.update('none');
                }
            } catch (e) {
                console.warn('Chart theme update failed:', e);
                // If update fails, try resize as fallback
                if (typeof ch.resize === 'function') {
                    try {
                        ch.resize();
                    } catch (resizeError) {
                        console.debug('Chart resize fallback also failed:', resizeError);
                    }
                }
            }
        });
    };
}
