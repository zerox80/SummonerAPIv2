

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
        <div className="ranked-overview__title">
          <p className="badge-soft">Ranked Snapshot</p>
          <h3>Competitive Performance</h3>
          <p>Track your ladder position across queues at a glance.</p>
        </div>
        <ul className="ranked-overview__metrics" aria-label="Ranked overview metrics">
          <li className="ranked-overview__metric">
            <span className="ranked-overview__metric-label">Highest Rank</span>
            <span className="ranked-overview__metric-value">{highestRankLabel}</span>
          </li>
          <li className="ranked-overview__metric">
            <span className="ranked-overview__metric-label">Total Games</span>
            <span className="ranked-overview__metric-value">{totalGames}</span>
          </li>
          <li className="ranked-overview__metric">
            <span className="ranked-overview__metric-label">Overall WR</span>
            <span className={`ranked-overview__metric-value ranked-overview__metric-value--${winrateClass}`}>
              {totalGames > 0 ? `${overallWinrate}%` : '—'}
            </span>
          </li>
          <li className="ranked-overview__metric">
            <span className="ranked-overview__metric-label">Queues Tracked</span>
            <span className="ranked-overview__metric-value">{entries.length}</span>
          </li>
        </ul>
      </header>
      <div className="ranked-overview__list">
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
              <div className="ranked-card__row">
                <div className="ranked-card__heading">
                  <div className="ranked-card__crest">
                    <img src={icon} alt={`${entry.tier} Crest`} loading="lazy" />
                  </div>
                  <div className="ranked-card__content">
                    <span className="ranked-card__queue">{queueLabel}</span>
                    <p className="ranked-card__rank">{formatRank(entry.tier, entry.rank, entry.leaguePoints)}</p>
                  </div>
                </div>
                <span className={`ranked-card__indicator ranked-card__indicator--${entryWinrateClass}`}>
                  {formatWinrate(entry.wins, entry.losses)}
                </span>
              </div>
              <div className="ranked-card__meta">
                <div>
                  <span className="ranked-card__label">Games</span>
                  <span className="ranked-card__value">{gamesPlayed}</span>
                </div>
                <div>
                  <span className="ranked-card__label">Wins</span>
                  <span className="ranked-card__value">{entry.wins ?? 0}</span>
                </div>
                <div>
                  <span className="ranked-card__label">Losses</span>
                  <span className="ranked-card__value">{entry.losses ?? 0}</span>
                </div>
                <div>
                  <span className="ranked-card__label">Current LP</span>
                  <span className="ranked-card__value">{`${entry.leaguePoints ?? 0} LP`}</span>
                </div>
              </div>
              {(entry.lpGain != null || entry.lpLoss != null) && (
                <div className="ranked-card__lp">
                  {entry.lpGain != null && <span className="positive">+{entry.lpGain} LP last win</span>}
                  {entry.lpLoss != null && <span className="negative">-{entry.lpLoss} LP last loss</span>}
                </div>
              )}
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
