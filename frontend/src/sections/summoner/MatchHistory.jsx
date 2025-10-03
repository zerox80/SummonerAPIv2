import { useMemo } from 'react';
import PropTypes from 'prop-types';
import SegmentedControl from '../../components/SegmentedControl.jsx';
import Tag from '../../components/Tag.jsx';
import { formatDuration, relativeGameTime, formatQueueById, formatKDA, roleLabel } from '../../utils/formatters.js';
import '../../styles/summoner/match-history.css';

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

export default function MatchHistory({ matches, summoner, filters, onFiltersChange, fetchMore, hasMore, isFetchingMore, championSquares }) {
  const filteredMatches = useMemo(() => applyFilters(matches, summoner, filters), [matches, summoner, filters]);

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

          const championImg = championSquares?.[participant.championName] || `https://ddragon.leagueoflegends.com/cdn/14.19.1/img/champion/${participant.championName}.png`;
          const gameTime = relativeGameTime(match.info.gameEndTimestamp || match.info.gameCreation);
          const duration = formatDuration(Math.round((match.info.gameDuration || 0) / 1000));
          const kda = formatKDA(participant.kills, participant.deaths, participant.assists);
          const cs = (participant.totalMinionsKilled || 0) + (participant.neutralMinionsKilled || 0);

          return (
            <article key={match.metadata?.matchId || Math.random()} className={`match-card ${participant.win ? 'is-win' : 'is-loss'}`}>
              <div className="match-card__champion">
                <img src={championImg} alt={participant.championName} loading="lazy" />
                <span className="match-card__champion-name">{participant.championName}</span>
              </div>
              <div className="match-card__info">
                <div className="match-card__queue">
                  <Tag tone={participant.win ? 'success' : 'danger'}>
                    {participant.win ? 'Win' : 'Loss'}
                  </Tag>
                  <span>{formatQueueById(match.info.queueId)}</span>
                </div>
                <div className="match-card__time">
                  <span>{gameTime}</span>
                  <span>·</span>
                  <span>{duration}</span>
                </div>
              </div>
              <div className="match-card__stats">
                <div className="match-card__stat">
                  <span className="label">KDA</span>
                  <span className="value">{participant.kills}/{participant.deaths}/{participant.assists}</span>
                </div>
                <div className="match-card__stat">
                  <span className="label">CS</span>
                  <span className="value">{cs}</span>
                </div>
                <div className="match-card__stat">
                  <span className="label">Gold</span>
                  <span className="value">{(participant.goldEarned || 0).toLocaleString()}</span>
                </div>
                <div className="match-card__stat">
                  <span className="label">Damage</span>
                  <span className="value">{(participant.totalDamageDealtToChampions || 0).toLocaleString()}</span>
                </div>
              </div>
              <div className="match-card__role">
                <Tag tone="info">{roleLabel(participant.teamPosition)}</Tag>
                {typeof match.info.lpChange === 'number' && (
                  <Tag tone={match.info.lpChange >= 0 ? 'success' : 'danger'}>
                    {match.info.lpChange >= 0 ? '+' : ''}{match.info.lpChange} LP
                  </Tag>
                )}
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
  championSquares: PropTypes.object
};
