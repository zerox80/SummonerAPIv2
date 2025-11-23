

import PropTypes from 'prop-types';
import { capitalize } from '../../utils/formatters.js';
import '../../styles/summoner/summoner-header.css';


function buildTagline(riotId) {
  if (!riotId || !riotId.includes('#')) return riotId || '';
  const [name, tag] = riotId.split('#');
  return `${name} · #${tag}`;
}


export default function SummonerHeader({ profile, derived }) {
  const riotId = profile?.suggestion?.riotId || profile?.summoner?.name;
  const tagline = buildTagline(riotId);
  const iconSrc = profile?.profileIconUrl
    || (profile?.suggestion?.profileIconId != null
      ? `https://ddragon.leagueoflegends.com/cdn/14.19.1/img/profileicon/${profile.suggestion.profileIconId}.png`
      : 'https://ddragon.leagueoflegends.com/cdn/14.19.1/img/profileicon/0.png');
  const level = profile?.summoner?.summonerLevel || profile?.suggestion?.summonerLevel;

  return (
    <section className="summoner-header glass-panel">
      <div className="summoner-header__media">
        <span className="summoner-header__avatar" aria-hidden>
          <img src={iconSrc} alt="Profile icon" loading="lazy" />
        </span>
        <span className="summoner-header__badge">Level {level ?? 0}</span>
      </div>
      <div className="summoner-header__meta">
        <div>
          <p className="summoner-header__eyebrow">Summoner Snapshot</p>
          <h2>{tagline}</h2>
        </div>
        <div className="summoner-header__grid">
          <div>
            <p className="label">Account Region</p>
            <p className="value">{profile?.summoner?.region ? profile.summoner.region.toUpperCase() : '—'}</p>
          </div>

          <div>
            <p className="label">Avg KDA</p>
            <p className="value">{derived.avgKda}</p>
          </div>
          <div>
            <p className="label">Most Played Role</p>
            <p className="value">{capitalize(profile?.primaryRole) || '—'}</p>
          </div>
        </div>
        <div className="summoner-header__highlight">
          <div>
            <span className="summoner-header__highlight-label">Perf. KDA Ratio</span>
            <span className="summoner-header__highlight-value">{derived.kdaRatio}</span>
          </div>
          <div>
            <span className="summoner-header__highlight-label">Avg CS / Min</span>
            <span className="summoner-header__highlight-value">{derived.avgCsPerMin || '0.0'}</span>
          </div>
          <div>
            <span className="summoner-header__highlight-label">Avg Gold</span>
            <span className="summoner-header__highlight-value">{derived.avgGold.toLocaleString()}</span>
          </div>
        </div>
      </div>
    </section>
  );
}

SummonerHeader.propTypes = {
  profile: PropTypes.shape({
    summoner: PropTypes.object,
    suggestion: PropTypes.object,
    profileIconUrl: PropTypes.string,
    primaryRole: PropTypes.string
  }),
  derived: PropTypes.shape({

    avgKda: PropTypes.string,
    kdaRatio: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    avgCsPerMin: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    avgGold: PropTypes.number
  }).isRequired
};
