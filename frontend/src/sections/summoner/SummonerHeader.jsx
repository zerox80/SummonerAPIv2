/**
 * Component for displaying summoner profile header information.
 *
 * <p>This module provides a header component that displays comprehensive information
 * about a summoner including their profile icon, level, region, performance
 * statistics, and key metrics. It presents the data in a visually appealing
 * layout with emphasis on important performance indicators.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Profile icon and level display with badge</li>
 *   <li>Formatted Riot ID with tagline</li>
 *   <li>Key performance metrics grid layout</li>
 *   <li>Highlighted statistics section</li>
 *   <li>Responsive design with glass panel styling</li>
 *   <li>Support for both summoner and suggestion data</li>
 * </ul>
 *
 * @module sections/summoner/SummonerHeader
 * @author zerox80
 * @version 2.0
 */

import PropTypes from 'prop-types';
import { capitalize } from '../../utils/formatters.js';
import '../../styles/summoner/summoner-header.css';

/**
 * Builds a tagline from a Riot ID.
 *
 * @param {string} riotId - The Riot ID.
 * @returns {string} The formatted tagline.
 */
function buildTagline(riotId) {
  if (!riotId || !riotId.includes('#')) return riotId || '';
  const [name, tag] = riotId.split('#');
  return `${name} · #${tag}`;
}

/**
 * Renders a header for a summoner's profile.
 *
 * @param {object} props - The component props.
 * @param {object} props.profile - The summoner's profile data.
 * @param {object} props.derived - Derived stats from the summoner's matches.
 * @returns {React.ReactElement} The rendered component.
 */
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
            <p className="label">Overall W/L</p>
            <p className="value">{derived.totalWins}W / {derived.totalLosses}L</p>
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
    totalWins: PropTypes.number,
    totalLosses: PropTypes.number,
    avgKda: PropTypes.string,
    kdaRatio: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    avgCsPerMin: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    avgGold: PropTypes.number
  }).isRequired
};
