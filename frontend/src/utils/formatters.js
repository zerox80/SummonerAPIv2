import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime.js';
import duration from 'dayjs/plugin/duration.js';

dayjs.extend(relativeTime);
dayjs.extend(duration);


const QUEUE_MAP = {
  RANKED_SOLO_5x5: 'Ranked Solo',
  RANKED_FLEX_SR: 'Ranked Flex',
  NORMAL: 'Normal',
  ARAM: 'ARAM',
  CLASH: 'Clash',
  BOT: 'Co-op vs AI'
};

const QUEUE_ID_MAP = {
  420: 'RANKED_SOLO_5x5',
  440: 'RANKED_FLEX_SR',
  400: 'NORMAL',
  430: 'NORMAL',
  450: 'ARAM',
  700: 'CLASH',
  830: 'BOT',
  840: 'BOT',
  850: 'BOT'
};

const ROLE_LABELS = {
  TOP: 'Top Lane',
  JUNGLE: 'Jungle',
  MIDDLE: 'Mid Lane',
  MID: 'Mid Lane',
  BOTTOM: 'ADC',
  ADC: 'ADC',
  SUPPORT: 'Support',
  UTILITY: 'Support'
};

const POSITION_EMOJIS = {
  TOP: '🔝',
  JUNGLE: '🌿',
  MIDDLE: '⚔️',
  BOTTOM: '🏹',
  UTILITY: '🛡️'
};

export function formatQueue(queueType) {
  return QUEUE_MAP[queueType] || queueType || 'Unranked';
}

export function formatQueueById(queueId) {
  if (queueId == null) return 'Custom';
  const mapped = QUEUE_ID_MAP[queueId];
  return formatQueue(mapped || queueId.toString());
}

export function formatRank(tier, rank, lp) {
  if (!tier) {
    return 'Unranked';
  }
  const readableTier = tier.charAt(0).toUpperCase() + tier.slice(1).toLowerCase();
  return `${readableTier} ${rank || ''} · ${lp ?? 0} LP`.trim();
}

export function formatWinrate(wins, losses) {
  const total = wins + losses;
  if (!total) {
    return '0% WR';
  }
  const wr = Math.round((wins / total) * 100);
  return `${wr}% WR`;
}

export function relativeGameTime(timestamp) {
  if (!timestamp) return '';
  return dayjs(timestamp).fromNow();
}

export function formatDuration(seconds) {
  if (!seconds) return '0:00';
  const dur = dayjs.duration(seconds, 'seconds');
  return `${dur.minutes().toString().padStart(2, '0')}:${dur.seconds().toString().padStart(2, '0')}`;
}

export function formatKDA(kills, deaths, assists) {
  const kda = deaths === 0 ? kills + assists : ((kills + assists) / Math.max(deaths, 1)).toFixed(2);
  return `${kills}/${deaths}/${assists} · ${deaths === 0 ? 'Perfect' : `${kda}:1`} KDA`;
}

export function laneEmoji(position) {
  return POSITION_EMOJIS[position] || '🎯';
}

export function capitalize(value) {
  if (!value) return '';
  return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
}

export function roleLabel(value) {
  if (!value) return 'All Roles';
  const upper = value.toUpperCase();
  return ROLE_LABELS[upper] || capitalize(value);
}

export function fallbackImage(url, fallback) {
  if (url && url.startsWith('http')) return url;
  return fallback;
}
