import { useMemo } from 'react';
import PropTypes from 'prop-types';
import '../../styles/summoner/recent-profiles.css';
import { relativeGameTime } from '../../utils/formatters.js';

const DEFAULT_ICON = 'https://ddragon.leagueoflegends.com/cdn/14.19.1/img/profileicon/0.png';

function buildProfileIcon(profile) {
  if (profile.profileIconUrl && profile.profileIconUrl.startsWith('http')) {
    return profile.profileIconUrl;
  }
  if (profile.profileIconId != null) {
    return `https://ddragon.leagueoflegends.com/cdn/14.19.1/img/profileicon/${profile.profileIconId}.png`;
  }
  return DEFAULT_ICON;
}

export default function RecentProfiles({ profiles, onSelect, onClear, currentRiotId }) {
  const items = useMemo(() => {
    if (!Array.isArray(profiles)) return [];
    return profiles
      .map((profile) => {
        const riotId = profile.riotId;
        const level = Number.isFinite(profile.summonerLevel) ? profile.summonerLevel : null;
        const region = typeof profile.region === 'string' && profile.region.trim().length > 0
          ? profile.region.trim().toUpperCase()
          : null;
        const lastViewed = typeof profile.lastViewed === 'number' ? profile.lastViewed : 0;
        return {
          riotId,
          level,
          region,
          lastViewed,
          icon: buildProfileIcon(profile),
          isCurrent: currentRiotId && riotId
            ? riotId.toLowerCase() === currentRiotId.toLowerCase()
            : false
        };
      })
      .filter((item) => typeof item.riotId === 'string' && item.riotId.length > 0)
      .sort((a, b) => (b.lastViewed || 0) - (a.lastViewed || 0));
  }, [profiles, currentRiotId]);

  const hasItems = items.length > 0;

  return (
    <section className={`recent-profiles glass-panel ${hasItems ? '' : 'is-empty'}`}>
      <header className="recent-profiles__header">
        <div>
          <p className="badge-soft">Recently Visited</p>
          <h2>Recent Profiles</h2>
        </div>
        {hasItems && onClear && (
          <button type="button" className="recent-profiles__clear" onClick={onClear}>
            Clear history
          </button>
        )}
      </header>

      {hasItems ? (
        <div className="recent-profiles__list" role="list">
          {items.map((item) => (
            <button
              key={item.riotId}
              type="button"
              className={`recent-profiles__item ${item.isCurrent ? 'is-current' : ''}`}
              onClick={() => onSelect(item.riotId)}
              aria-current={item.isCurrent ? 'true' : undefined}
            >
              <span className="recent-profiles__thumb" aria-hidden>
                <img src={item.icon} alt="Profile icon" loading="lazy" />
              </span>
              <span className="recent-profiles__meta">
                <span className="recent-profiles__riot-id">{item.riotId}</span>
                <span className="recent-profiles__secondary">
                  {item.region ? `${item.region}` : 'Unknown region'}
                  {item.level != null ? ` · Level ${item.level}` : ''}
                </span>
                {item.lastViewed ? (
                  <span className="recent-profiles__timestamp">
                    Viewed {relativeGameTime(item.lastViewed)}
                  </span>
                ) : null}
              </span>
            </button>
          ))}
        </div>
      ) : (
        <div className="recent-profiles__empty">
          <p>No recent profiles yet</p>
          <span>Search for a summoner to build your history. Profiles you open will stay pinned here.</span>
        </div>
      )}
    </section>
  );
}

RecentProfiles.propTypes = {
  profiles: PropTypes.arrayOf(PropTypes.shape({
    riotId: PropTypes.string,
    profileIconUrl: PropTypes.string,
    profileIconId: PropTypes.number,
    summonerLevel: PropTypes.number,
    region: PropTypes.string,
    lastViewed: PropTypes.number
  })),
  onSelect: PropTypes.func,
  onClear: PropTypes.func,
  currentRiotId: PropTypes.string
};

RecentProfiles.defaultProps = {
  profiles: [],
  onSelect: () => {},
  onClear: undefined,
  currentRiotId: ''
};
