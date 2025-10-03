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

function crestForTier(tier) {
  if (!tier) return CRESTS.IRON;
  const key = tier.toUpperCase();
  return CRESTS[key] || CRESTS.IRON;
}

export default function RankedOverview({ entries, bases }) {
  if (!entries || entries.length === 0) {
    return (
      <section className="ranked-overview glass-panel">
        <div className="ranked-overview__empty">
          <p>No Ranked Data</p>
          <span>Spiele mindestens 10 Spiele, um in der Ladder aufzutauchen.</span>
        </div>
      </section>
    );
  }

  return (
    <section className="ranked-overview glass-panel">
      <header className="ranked-overview__header">
        <h3>Ranked Performance</h3>
        <p>Analyse deiner Solo/Duo & Flex Queues mit Live LP Tracking.</p>
      </header>
      <div className="ranked-overview__grid">
        {entries.map((entry) => {
          const crest = crestForTier(entry.tier);
          const icon = `${bases?.rankedMiniCrest || ''}${crest}.png`;
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
                    {entry.lpGain != null && <span className="positive">+{entry.lpGain} LP letzter Sieg</span>}
                    {entry.lpLoss != null && <span className="negative">-{entry.lpLoss} LP letzte Niederlage</span>}
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
