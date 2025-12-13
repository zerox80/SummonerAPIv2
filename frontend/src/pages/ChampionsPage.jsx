

import { Link } from 'react-router-dom';
import { useChampions } from '../hooks/useChampions.js';
import SegmentedControl from '../components/SegmentedControl.jsx';
import { EmptyState } from '../components/LoadState.jsx';
import '../styles/champions/champions-page.css';

const ROLE_OPTIONS = [
  { label: 'All', value: 'ALL' },
  { label: 'Assassin', value: 'ASSASSIN' },
  { label: 'Fighter', value: 'FIGHTER' },
  { label: 'Mage', value: 'MAGE' },
  { label: 'Marksman', value: 'MARKSMAN' },
  { label: 'Support', value: 'SUPPORT' },
  { label: 'Tank', value: 'TANK' }
];

const SORT_OPTIONS = [
  { label: 'Alphabetical', value: 'alpha' },
  { label: 'Role', value: 'roles' }
];


export default function ChampionsPage() {
  const {
    champions,
    isLoading,
    isError,
    error,
    search,
    setSearch,
    role,
    setRole,
    sort,
    setSort
  } = useChampions();

  const championCount = Array.isArray(champions) ? champions.length : 0;
  const activeRoleLabel = ROLE_OPTIONS.find((option) => option.value === role)?.label ?? 'All';
  const activeSortLabel = SORT_OPTIONS.find((option) => option.value === sort)?.label ?? 'Alphabetical';
  const hasSearch = Boolean(search?.trim());
  const filterChips = [];

  if (hasSearch) {
    filterChips.push({ key: 'search', label: `Search: "${search.trim()}"` });
  }

  filterChips.push({ key: 'role', label: role === 'ALL' ? 'All roles' : activeRoleLabel });
  filterChips.push({ key: 'sort', label: `Sort: ${activeSortLabel}` });

  return (
    <div className="champions-page">
      <section className="champions-hero glass-panel">
        <div className="champions-hero__text">
          <p className="badge-soft">Champion Intel</p>
          <h1>Discover the best champion for your next game</h1>
          <p className="champions-hero__subtitle">
            Browse all champions, filter by roles, and compare builds with aggregated data.
          </p>
        </div>
        <div className="champions-hero__search">
          <label htmlFor="champion-search" className="champions-hero__search-label">Search champions</label>
          <input
            id="champion-search"
            type="search"
            placeholder="e.g., Ahri, Aatrox, Caitlyn"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </div>
        <div className="champions-hero__controls">
          <SegmentedControl options={ROLE_OPTIONS} value={role} onChange={setRole} size="md" />
          <SegmentedControl options={SORT_OPTIONS} value={sort} onChange={setSort} size="md" />
        </div>
      </section>

      <section className="champions-grid-section glass-panel">
        {isLoading && (
          <div className="champions-grid">
            {Array.from({ length: 12 }).map((_, i) => (
              <div key={i} className="champion-card champion-card--skeleton" aria-hidden="true">
                <div className="champion-card__image" />
                <div className="champion-card__content">
                  <div className="champion-card__skeleton-line" style={{ width: '70%', height: '1.2rem' }} />
                  <div className="champion-card__skeleton-line" style={{ width: '40%', height: '0.9rem' }} />
                  <div className="champion-card__tags" style={{ marginTop: '0.75rem' }}>
                    <div className="champion-card__skeleton-line" style={{ width: '3rem', height: '1.2rem', borderRadius: '4px' }} />
                    <div className="champion-card__skeleton-line" style={{ width: '3rem', height: '1.2rem', borderRadius: '4px' }} />
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
        {isError && (
          <div className="champions-state error">
            <p>{error?.message || 'Failed to load champions.'}</p>
            <button onClick={() => window.location.reload()} className="cta-button" style={{ marginTop: '1rem' }}>
              Try Again
            </button>
          </div>
        )}

        {!isLoading && !isError && (
          <>
            <header className="champions-grid__header">
              <div className="champions-grid__summary">
                <h2>
                  Showing {championCount} champion{championCount === 1 ? '' : 's'}
                </h2>
                <p>
                  Tailor the roster with precise filters and discover your next main.
                </p>
              </div>
              <div className="champions-grid__filters" aria-live="polite">
                {filterChips.map((chip) => (
                  <span key={chip.key}>{chip.label}</span>
                ))}
              </div>
            </header>
            {championCount > 0 ? (
              <div className="champions-grid">
                {champions.map((champion) => (
                  <Link key={champion.id} to={`/champions/${champion.id}`} className="champion-card" aria-label={`Open details for ${champion.name}`}>
                    <div className="champion-card__image">
                      <img src={`https://ddragon.leagueoflegends.com/cdn/14.19.1/img/champion/${champion.imageFull}`} alt={champion.name} loading="lazy" />
                    </div>
                    <div className="champion-card__content">
                      <h3>{champion.name}</h3>
                      <p>{champion.title}</p>
                      <div className="champion-card__tags">
                        {champion.tags?.map((tag) => (
                          <span key={tag}>{tag}</span>
                        ))}
                      </div>
                    </div>
                  </Link>
                ))}
              </div>
            ) : (
              <div className="champions-state">
                <EmptyState
                  title="No champions found"
                  description="We couldn't find any champions matching your criteria. Try different filters or search terms."
                />
              </div>
            )}
          </>
        )}
      </section>
    </div>
  );
}
