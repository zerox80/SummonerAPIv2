/**
 * Component for displaying summoner ranked performance overview.
 *
 * <p>This module provides a ranked overview component that displays a summoner's
 * ranked statistics across different queue types. It shows rank information,
 * win/loss records, win rates, and LP changes for both Solo/Duo and Flex
 * ranked queues with appropriate rank crests and visual indicators.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Display of ranked tier, division, and league points</li>
 *   <li>Win/loss records and win rate calculations</li>
 *   <li>LP gain/loss tracking for recent matches</li>
 *   <li>Visual rank crests for all tiers</li>
 *   <li>Support for multiple queue types</li>
 *   <li>Empty state handling for unranked players</li>
 * </ul>
 *
 * @module sections/summoner/RankedOverview
 * @author zerox80
 * @version 2.0
 */

import PropTypes from 'prop-types';
import { formatRank, formatWinrate } from '../../utils/formatters.js';
import '../../styles/summoner/ranked-overview.css';

const CRESTS = {
  IRON: 'iron',
  BRONZE: 'bronze',
  SILVER: 'silver',
  GOLD: 'gold',
  PLATINUM: 'platinum',
  EMERALD: 'emerald',
  DIAMOND: 'diamond',
  MASTER: 'master',
  GRANDMASTER: 'grandmaster',
  CHALLENGER: 'challenger'
};

/**
 * Gets the crest for a given tier.
 *
 * @param {string} tier - The tier.
 * @returns {string} The crest.
 */
function crestForTier(tier) {
  if (!tier) return CRESTS.IRON;
  const key = tier.toUpperCase();
  return CRESTS[key] || CRESTS.IRON;
}

/**
 * Renders an overview of a summoner's ranked performance.
 *
 * @param {object} props - The component props.
 * @param {Array<object>} props.entries - The list of ranked entries.
 * @param {object} props.bases - The base URLs for images.
 * @returns {React.ReactElement} The rendered component.
 */
export default function RankedOverview({ entries, bases }) {
  if (!entries || entries.length === 0) {
    return (
      <section className="ranked-overview glass-panel">
        <div className="ranked-overview__empty">
          <p>No Ranked Data</p>
          <span>Play at least 10 games to appear on the ladder.</span>
        </div>
      </section>
    );
  }

  return (
    <section className="ranked-overview glass-panel">
      <header className="ranked-overview__header">
        <h3>Ranked Performance</h3>
        <p>Analyze your Solo/Duo & Flex queues with live LP tracking.</p>
      </header>
      <div className="ranked-overview__grid">
        {entries.map((entry) => {
          const crest = crestForTier(entry.tier);
          const extension = crest === 'emerald' ? 'svg' : 'png';
          const icon = `${bases?.rankedMiniCrest || ''}${crest}.${extension}`;
          return (
            <article key={`${entry.queueType}-${entry.tier}-${entry.rank}`} className="ranked-card">
              <div className="ranked-card__crest">
                <img src={icon} alt={`${entry.tier} Crest`} loading="lazy" />
              </div>
              <div className="ranked-card__content">
                <h4>{entry.queueType === 'RANKED_SOLO_5x5' ? 'Solo/Duo' : 'Flex'}</h4>
                <p className="ranked-card__rank">{formatRank(entry.tier, entry.rank, entry.leaguePoints)}</p>
                <div className="ranked-card__stats">
                  <span>{entry.wins}W / {entry.losses}L</span>
                  <span>{formatWinrate(entry.wins, entry.losses)}</span>
                </div>
                {(entry.lpGain != null || entry.lpLoss != null) && (
                  <div className="ranked-card__lp">
                    {entry.lpGain != null && <span className="positive">+{entry.lpGain} LP last win</span>}
                    {entry.lpLoss != null && <span className="negative">-{entry.lpLoss} LP last loss</span>}
                  </div>
                )}
              </div>
            </article>
          );
        })}
      </div>
    </section>
  );
}

RankedOverview.propTypes = {
  entries: PropTypes.arrayOf(PropTypes.shape({
    queueType: PropTypes.string,
    tier: PropTypes.string,
    rank: PropTypes.string,
    leaguePoints: PropTypes.number,
    wins: PropTypes.number,
    losses: PropTypes.number,
    lpGain: PropTypes.number,
    lpLoss: PropTypes.number
  })),
  bases: PropTypes.object
};
