import { useMemo } from 'react';
import PropTypes from 'prop-types';
import SegmentedControl from '../../components/SegmentedControl.jsx';
import Tag from '../../components/Tag.jsx';
import { formatDuration, relativeGameTime, formatQueueById } from '../../utils/formatters.js';
import '../../styles/summoner/match-history.css';

const DEFAULT_DDRAGON_VERSION = '15.20.1';
const DEFAULT_CHAMPION_BASE = `https://ddragon.leagueoflegends.com/cdn/${DEFAULT_DDRAGON_VERSION}/img/champion/`;

const QUEUE_FILTER_OPTIONS = [
  { label: 'All', value: 'ALL' },
  { label: 'Solo/Duo', value: '420' },
  { label: 'Flex', value: '440' },
  { label: 'ARAM', value: '450' }
];

const RESULT_FILTER_OPTIONS = [
  { label: 'All', value: 'ALL' },
  { label: 'Wins', value: 'WIN' },
  { label: 'Losses', value: 'LOSS' }
];

const ROLE_FILTER_OPTIONS = [
  { label: 'All Roles', value: 'ALL' },
  { label: 'Top', value: 'TOP' },
  { label: 'Jungle', value: 'JUNGLE' },
  { label: 'Mid', value: 'MIDDLE' },
  { label: 'ADC', value: 'BOTTOM' },
  { label: 'Support', value: 'UTILITY' }
];


function resolveChampionImage(championSquares, championBase, championName, championId) {
  if (!championName) return null;
  const championByName = championSquares?.[championName];
  if (championByName) return championByName;
  const idKey = championId != null ? String(championId) : null;
  if (idKey && championSquares?.[idKey]) return championSquares[idKey];
  return `${championBase}${championName}.png`;
}

function applyFilters(matches, summoner, filters) {
  if (!Array.isArray(matches)) return [];
  
  return matches.filter((match) => {
    if (!match?.info?.participants) return false;
    const participant = match.info.participants.find((p) => p?.puuid === summoner?.puuid);
    if (!participant) return false;

    if (filters.queue !== 'ALL' && `${match.info.queueId}` !== filters.queue) return false;
    if (filters.result === 'WIN' && !participant.win) return false;
    if (filters.result === 'LOSS' && participant.win) return false;
    if (filters.role !== 'ALL' && participant.teamPosition !== filters.role) return false;

    return true;
  });
}

export default function MatchHistory({ matches, summoner, filters, onFiltersChange, fetchMore, hasMore, isFetchingMore, championSquares, bases }) {
  const filteredMatches = useMemo(() => applyFilters(matches, summoner, filters), [matches, summoner, filters]);
  const championBase = bases?.champSquare
    || (bases?.img ? `${bases.img}champion/` : DEFAULT_CHAMPION_BASE);

  return (
    <section className="match-history glass-panel">
      <header className="match-history__header">
        <div>
          <p className="badge-soft">Match Timeline</p>
          <h3>Match History</h3>
        </div>
        <div className="match-history__filters">
          <SegmentedControl 
            options={QUEUE_FILTER_OPTIONS} 
            value={filters.queue} 
            onChange={(value) => onFiltersChange({ ...filters, queue: value })} 
            size="sm"
          />
          <SegmentedControl 
            options={RESULT_FILTER_OPTIONS} 
            value={filters.result} 
            onChange={(value) => onFiltersChange({ ...filters, result: value })} 
            size="sm"
          />
          <SegmentedControl 
            options={ROLE_FILTER_OPTIONS} 
            value={filters.role} 
            onChange={(value) => onFiltersChange({ ...filters, role: value })} 
            size="sm"
          />
        </div>
      </header>

      {filteredMatches.length === 0 && (
        <div className="match-history__empty">
          <p>No matches found</p>
          <span>Try changing filters or play more games</span>
        </div>
      )}

      <div className="match-history__list">
        {filteredMatches.map((match) => {
          const participant = match.info.participants.find((p) => p?.puuid === summoner?.puuid);
          if (!participant) return null;

          const championImg = resolveChampionImage(
            championSquares,
            championBase,
            participant.championName,
            participant.championId
          );
          const gameTime = relativeGameTime(match.info.gameEndTimestamp || match.info.gameCreation);
          const duration = formatDuration(Math.round(match.info.gameDuration || 0));
          const cs = (participant.totalMinionsKilled || 0) + (participant.neutralMinionsKilled || 0);
          const matchId = match.metadata?.matchId || `${match.info?.gameId || match.info?.gameCreation}-${participant.puuid}`;

          return (
            <article
              key={matchId}
              className={`match-card ${participant.win ? 'is-win' : 'is-loss'}`}
            >
              <div
                className="match-card__summary"
              >
                <div className="match-card__champion">
                  <img src={championImg} alt={participant.championName} loading="lazy" />
                  <div className="match-card__champion-meta">
                    <Tag tone={participant.win ? 'success' : 'danger'}>
                      {participant.win ? 'Win' : 'Loss'}
                    </Tag>
                    <span className="match-card__champion-name">{participant.championName}</span>
                  </div>
                </div>
                <div className="match-card__meta">
                  <span className="match-card__queue">{formatQueueById(match.info.queueId)}</span>
                  <span className="match-card__separator" aria-hidden="true">·</span>
                  <span className="match-card__time">{gameTime}</span>
                  <span className="match-card__separator" aria-hidden="true">·</span>
                  <span className="match-card__duration">{duration}</span>
                </div>
                <div className="match-card__stat match-card__stat--kda">
                  <span className="label">KDA</span>
                  <span className="value">{participant.kills}/{participant.deaths}/{participant.assists}</span>
                  <span className="sub">{participant.deaths === 0 ? 'Perfect' : `${((participant.kills + participant.assists) / Math.max(participant.deaths, 1)).toFixed(2)}:1`}</span>
                </div>
                <div className="match-card__stat match-card__stat--cs">
                  <span className="label">CS</span>
                  <span className="value">{cs}</span>
                </div>
              </div>
            </article>
          );
        })}
      </div>

      {hasMore && (
        <div className="match-history__load-more">
          <button 
            type="button" 
            className="cta-button" 
            onClick={fetchMore} 
            disabled={isFetchingMore}
          >
            {isFetchingMore ? 'Loading more matches ...' : 'Load more matches'}
          </button>
        </div>
      )}
    </section>
  );
}

MatchHistory.propTypes = {
  matches: PropTypes.arrayOf(PropTypes.object),
  summoner: PropTypes.shape({
    puuid: PropTypes.string
  }),
  filters: PropTypes.shape({
    queue: PropTypes.string,
    result: PropTypes.string,
    role: PropTypes.string
  }).isRequired,
  onFiltersChange: PropTypes.func.isRequired,
  fetchMore: PropTypes.func.isRequired,
  hasMore: PropTypes.bool,
  isFetchingMore: PropTypes.bool,
  championSquares: PropTypes.object,
  bases: PropTypes.object
};
