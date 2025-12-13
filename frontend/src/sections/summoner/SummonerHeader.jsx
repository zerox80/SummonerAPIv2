

import PropTypes from 'prop-types';
import { capitalize } from '../../utils/formatters.js';
import '../../styles/summoner/summoner-header.css';

const COMMUNITY_DRAGON_PROFILE_ICON_BASE =
  'https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/profile-icons';

function buildTagline(riotId) {
  if (!riotId || !riotId.includes('#')) return riotId || '';
  const [name, tag] = riotId.split('#');
  return `${name} · #${tag}`;
}


export default function SummonerHeader({ profile, derived }) {
  const riotId = profile?.riotId || profile?.suggestion?.riotId || profile?.summoner?.name;
  const tagline = buildTagline(riotId);
  const iconId = profile?.suggestion?.profileIconId ?? 0;
  const iconSrc = profile?.profileIconUrl || `${COMMUNITY_DRAGON_PROFILE_ICON_BASE}/${iconId}.jpg`;
  const level = profile?.summoner?.summonerLevel || profile?.suggestion?.summonerLevel;

  // Fallback for region if missing, or format it nicely
  const region = profile?.platformRegion ? profile.platformRegion.toUpperCase() : 'EUW1';

  return (
    <section className="summoner-header glass-panel">
      <div className="summoner-header__backdrop" style={{ backgroundImage: `url(${iconSrc})` }} />

      <div className="summoner-header__content">
        <div className="summoner-header__main-info">
          <div className="summoner-header__avatar-container">
            <div className="summoner-header__avatar">
              <img src={iconSrc} alt="Profile icon" loading="lazy" />
            </div>
            <span className="summoner-header__level">{level ?? 0}</span>
          </div>

          <div className="summoner-header__identity">
            <span className="summoner-header__eyebrow">Summoner Profile</span>
            <h1 className="summoner-header__name">{tagline}</h1>
            <div className="summoner-header__badges">
              <span className="summoner-header__region-badge">{region}</span>
              {profile?.primaryRole && (
                <span className="summoner-header__role-badge">{capitalize(profile.primaryRole)}</span>
              )}
            </div>
          </div>
        </div>

        <div className="summoner-header__stats">
          <div className="stat-card">
            <span className="stat-label">Win Rate</span>
            <div className="stat-value-group">
              <span className="stat-value highlight">{derived.winRate}%</span>
              <span className="stat-sub">{derived.totalWins}W - {derived.totalLosses}L</span>
            </div>
          </div>

          <div className="stat-card">
            <span className="stat-label">KDA Ratio</span>
            <div className="stat-value-group">
              <span className="stat-value">{derived.kdaRatio}</span>
              <span className="stat-sub">Perf. Score</span>
            </div>
          </div>

          <div className="stat-card">
            <span className="stat-label">CS / Min</span>
            <div className="stat-value-group">
              <span className="stat-value">{derived.avgCsPerMin || '0.0'}</span>
              <span className="stat-sub">Average</span>
            </div>
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
    riotId: PropTypes.string,
    platformRegion: PropTypes.string,
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
