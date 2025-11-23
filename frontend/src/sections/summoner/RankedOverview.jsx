

import PropTypes from 'prop-types';
import { formatQueue, formatRank, formatWinrate } from '../../utils/formatters.js';
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

const TIER_ORDER = ['IRON', 'BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'EMERALD', 'DIAMOND', 'MASTER', 'GRANDMASTER', 'CHALLENGER'];
const RANK_WEIGHT = { IV: 1, III: 2, II: 3, I: 4 };


function crestForTier(tier) {
  if (!tier) return CRESTS.IRON;
  const key = tier.toUpperCase();
  return CRESTS[key] || CRESTS.IRON;
}


function entryScore(entry) {
  if (!entry) return -1;
  const tierIndex = TIER_ORDER.indexOf(entry.tier?.toUpperCase()) + 1;
  const rankWeight = RANK_WEIGHT[entry.rank?.toUpperCase()] || 0;
  const leaguePoints = entry.leaguePoints ?? 0;
  const tierScore = Math.max(tierIndex, 0) * 10000;
  const rankScore = rankWeight * 100;
  return tierScore + rankScore + leaguePoints;
}


function classifyWinrate(winrate) {
  if (winrate >= 50) return 'positive';
  if (winrate <= 45) return 'negative';
  return 'neutral';
}


export default function RankedOverview({ entries, bases }) {
  if (!Array.isArray(entries) || entries.length === 0) {
    return (
      <section className="ranked-overview glass-panel">
        <div className="ranked-overview__empty">
          <p>No Ranked Data</p>
          <span>Play at least 10 games to appear on the ladder.</span>
        </div>
      </section>
    );
  }

  const aggregate = entries.reduce((acc, entry) => {
    acc.wins += entry.wins ?? 0;
    acc.losses += entry.losses ?? 0;
    return acc;
  }, { wins: 0, losses: 0 });

  const totalGames = aggregate.wins + aggregate.losses;
  const overallWinrate = totalGames > 0 ? Math.round((aggregate.wins / totalGames) * 100) : 0;

  const highestEntry = entries.reduce((best, entry) => {
    if (!entry) return best;
    if (!best) return entry;
    return entryScore(entry) > entryScore(best) ? entry : best;
  }, null);

  const highestRankLabel = highestEntry
    ? formatRank(highestEntry.tier, highestEntry.rank, highestEntry.leaguePoints)
    : 'Unranked';

  const winrateClass = classifyWinrate(overallWinrate);

  return (
    <section className="ranked-overview glass-panel">
      <header className="ranked-overview__header">
        <div className="ranked-overview__title-group">
          <span className="badge-soft">Ranked Season</span>
          <h3>Competitive Overview</h3>
        </div>
        <div className="ranked-overview__global-stats">
          <div className="global-stat">
            <span className="label">Peak Rank</span>
            <span className="value">{highestRankLabel}</span>
          </div>
          <div className="global-stat">
            <span className="label">Total Games</span>
            <span className="value">{totalGames}</span>
          </div>
          <div className="global-stat">
            <span className="label">Win Rate</span>
            <span className={`value value--${winrateClass}`}>
              {totalGames > 0 ? `${overallWinrate}%` : '—'}
            </span>
          </div>
        </div>
      </header>

      <div className="ranked-overview__grid">
        {entries.map((entry) => {
          const crest = crestForTier(entry.tier);
          const extension = crest === 'emerald' ? 'svg' : 'png';
          const icon = `${bases?.rankedMiniCrest || ''}${crest}.${extension}`;
          const queueLabel = formatQueue(entry.queueType);
          const gamesPlayed = (entry.wins ?? 0) + (entry.losses ?? 0);
          const entryWinrate = gamesPlayed > 0 ? Math.round((entry.wins / gamesPlayed) * 100) : 0;
          const entryWinrateClass = classifyWinrate(entryWinrate);

          return (
            <article key={`${entry.queueType}-${entry.tier}-${entry.rank}`} className="ranked-card">
              <div className="ranked-card__visual">
                <div className="ranked-card__crest-container">
                  <img src={icon} alt={`${entry.tier} Crest`} loading="lazy" className="ranked-card__crest-img" />
                  <div className="ranked-card__crest-glow" style={{ backgroundColor: getTierColor(entry.tier) }} />
                </div>
              </div>

              <div className="ranked-card__info">
                <div className="ranked-card__header">
                  <span className="ranked-card__queue">{queueLabel}</span>
                  <div className={`ranked-card__winrate tag-${entryWinrateClass}`}>
                    {entryWinrate}% WR
                  </div>
                </div>

                <h4 className="ranked-card__rank-title">
                  {entry.tier} {entry.rank}
                </h4>
                <span className="ranked-card__lp-value">{entry.leaguePoints} LP</span>

                <div className="ranked-card__stats-row">
                  <div className="stat-item">
                    <span className="stat-val">{entry.wins}</span>
                    <span className="stat-lbl">W</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-val">{entry.losses}</span>
                    <span className="stat-lbl">L</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-val">{gamesPlayed}</span>
                    <span className="stat-lbl">Games</span>
                  </div>
                </div>
              </div>
            </article>
          );
        })}
      </div>
    </section>
  );
}

function getTierColor(tier) {
  const colors = {
    IRON: '#a19bd9',
    BRONZE: '#cd7f32',
    SILVER: '#c0c0c0',
    GOLD: '#ffd700',
    PLATINUM: '#00ced1',
    EMERALD: '#50c878',
    DIAMOND: '#b9f2ff',
    MASTER: '#ff00ff',
    GRANDMASTER: '#ff4500',
    CHALLENGER: '#f0e68c'
  };
  return colors[tier?.toUpperCase()] || '#ffffff';
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
