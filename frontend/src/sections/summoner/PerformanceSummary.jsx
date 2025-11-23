

import { useMemo, useState } from 'react';
import PropTypes from 'prop-types';
import MetricTile from '../../components/MetricTile.jsx';
import SegmentedControl from '../../components/SegmentedControl.jsx';
import Sparkline from '../../components/Sparkline.jsx';
import Tag from '../../components/Tag.jsx';
import { formatDuration, relativeGameTime, formatQueueById, roleLabel } from '../../utils/formatters.js';
import '../../styles/summoner/performance-summary.css';

const RANGE_OPTIONS = [
  { label: 'Last 40', value: '40' },
  { label: 'Last 100', value: '100' },
  { label: 'All', value: 'all' }
];

const QUEUE_OPTIONS = [
  { label: 'All Queues', value: 'ALL' },
  { label: 'Solo/Duo', value: '420' },
  { label: 'Flex', value: '440' },
  { label: 'ARAM', value: '450' }
];


function buildTimeline(matches, puuid, queueFilter, rangeFilter) {
  if (!Array.isArray(matches)) return [];
  const filtered = matches.filter((match) => {
    if (!match?.info?.participants) return false;
    const participant = match.info.participants.find((p) => p?.puuid === puuid);
    if (!participant) return false;
    if (queueFilter !== 'ALL' && `${match.info.queueId}` !== queueFilter) return false;
    return true;
  });
  const sliced = rangeFilter === 'all' ? filtered : filtered.slice(0, Number(rangeFilter));
  return sliced
    .map((match) => {
      const participant = match.info.participants.find((p) => p?.puuid === puuid);
      const kda = participant.deaths === 0 ? participant.kills + participant.assists : (participant.kills + participant.assists) / Math.max(participant.deaths, 1);
      return {
        timestamp: match.info.gameEndTimestamp || match.info.gameCreation,
        win: participant.win,
        kda,
        damage: participant.totalDamageDealtToChampions || 0,
        cs: (participant.totalMinionsKilled || 0) + (participant.neutralMinionsKilled || 0)
      };
    });
}


export default function PerformanceSummary({ derived, matches, summoner, range = '40', onRangeChange }) {
  const [queue, setQueue] = useState('ALL');
  const [internalRange, setInternalRange] = useState(range || '40');
  const isControlled = typeof onRangeChange === 'function';
  const activeRange = isControlled ? (range || '40') : internalRange;

  const handleRangeChange = (value) => {
    if (isControlled) {
      onRangeChange(value);
    } else {
      setInternalRange(value);
    }
  };

  const timeline = useMemo(() => buildTimeline(matches, summoner?.puuid, queue, activeRange), [matches, summoner?.puuid, queue, activeRange]);

  const rangeSummary = useMemo(() => {
    if (activeRange === 'all') {
      return 'All matches';
    }
    return `Last ${activeRange} matches`;
  }, [activeRange]);

  const winSpark = timeline.map((entry) => (entry.win ? 1 : -1));
  const kdaSpark = timeline.map((entry) => Number(entry.kda.toFixed(2)) || 0);
  const damageSpark = timeline.map((entry) => entry.damage);

  const lastMatch = matches?.[0];
  const lastParticipant = lastMatch?.info?.participants?.find((p) => p?.puuid === summoner?.puuid);
  const lastRole = lastParticipant?.teamPosition;

  return (
    <section className="performance-summary glass-panel">
      <header className="performance-summary__header">
        <div className="performance-summary__title-group">
          <span className="badge-soft">Analytics</span>
          <h3>Performance Insights</h3>
          <p className="performance-summary__description">{rangeSummary}</p>
        </div>
        <div className="performance-summary__controls">
          <SegmentedControl options={RANGE_OPTIONS} value={activeRange} onChange={handleRangeChange} size="sm" />
          <SegmentedControl options={QUEUE_OPTIONS} value={queue} onChange={setQueue} size="sm" />
        </div>
      </header>

      <div className="performance-summary__grid">
        <MetricTile
          label="Win Rate"
          value={`${derived.winRate}%`}
          secondary={`${derived.totalWins}W - ${derived.totalLosses}L`}
          icon="🏆"
          emphasize
          trend={<Sparkline data={winSpark} color="rgba(34,197,94,0.8)" />}
        />
        <MetricTile
          label="KDA Ratio"
          value={derived.kdaRatio === 'Perfect' ? 'Perfect' : derived.kdaRatio}
          secondary={`${derived.avgKda} Avg`}
          icon="⚔️"
          trend={<Sparkline data={kdaSpark} color="rgba(236,72,153,0.8)" />}
        />
        <MetricTile
          label="Avg Damage"
          value={derived.avgDamage.toLocaleString()}
          secondary="To Champions"
          icon="🔥"
          trend={<Sparkline data={damageSpark} color="rgba(99,102,241,0.85)" />}
        />
        <MetricTile
          label="Vision & KP"
          value={`${derived.avgVision} VS`}
          secondary={`${derived.killParticipation}% KP`}
          icon="👁️"
          trend={timeline.length > 0 ? <Sparkline data={timeline.map((entry) => entry.cs)} color="rgba(45,212,191,0.85)" /> : null}
        />
      </div>

      {lastMatch && lastParticipant && (
        <div className="performance-summary__last">
          <div className="last-match-header">
            <span className="last-match-label">Latest Match</span>
            <span className="last-match-time">{relativeGameTime(lastMatch.info.gameEndTimestamp || lastMatch.info.gameCreation)}</span>
          </div>

          <div className="last-match-content">
            <div className="last-match-info">
              <span className={`last-match-result ${lastParticipant.win ? 'is-win' : 'is-loss'}`}>
                {lastParticipant.win ? 'Victory' : 'Defeat'}
              </span>
              <div className="last-match-details">
                <span className="queue-name">{formatQueueById(lastMatch.info.queueId)}</span>
                <span className="duration">{formatDuration(Math.round((lastMatch.info.gameDuration || 0) / 1000))}</span>
              </div>
            </div>

            <div className="last-match-stats">
              <Tag tone="info">{roleLabel(lastRole)}</Tag>
              <div className="kda-tag">
                <span className="kda-val">{lastParticipant.kills}/{lastParticipant.deaths}/{lastParticipant.assists}</span>
              </div>
              {typeof lastMatch.info.lpChange === 'number' && (
                <Tag tone={lastMatch.info.lpChange >= 0 ? 'success' : 'danger'}>
                  {lastMatch.info.lpChange >= 0 ? '+' : ''}{lastMatch.info.lpChange} LP
                </Tag>
              )}
            </div>
          </div>
        </div>
      )}
    </section>
  );
}

PerformanceSummary.propTypes = {
  derived: PropTypes.shape({
    totalWins: PropTypes.number,
    totalLosses: PropTypes.number,
    avgKda: PropTypes.string,
    kdaRatio: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    avgCsPerMin: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    avgGold: PropTypes.number,
    avgDamage: PropTypes.number,
    avgVision: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    killParticipation: PropTypes.number,
    winRate: PropTypes.number
  }).isRequired,
  matches: PropTypes.arrayOf(PropTypes.object),
  summoner: PropTypes.shape({
    puuid: PropTypes.string
  }),
  range: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onRangeChange: PropTypes.func
};
