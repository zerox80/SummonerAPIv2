import { Link } from 'react-router-dom';
import { useChampions } from '../hooks/useChampions.js';
import SegmentedControl from '../components/SegmentedControl.jsx';
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
        {isLoading && <p className="champions-state">Loading champions ...</p>}
        {isError && <p className="champions-state error">{error?.message || 'Failed to load champions.'}</p>}

        {!isLoading && !isError && (
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
        )}
        {!isLoading && !isError && champions.length === 0 && (
          <p className="champions-state">No champions match your filters.</p>
        )}
      </section>
    </div>
  );
}

