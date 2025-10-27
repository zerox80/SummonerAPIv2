/**
 * Utility functions for formatting game data, timestamps, and display values.
 * 
 * <p>This module provides a comprehensive set of formatting utilities for League of Legends
 * game data, including queue types, ranks, winrates, timestamps, durations, KDA ratios,
 * and role labels. It uses dayjs for date/time manipulation and includes mappings for
 * Riot Games' queue IDs and position data.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Queue type formatting with ID-to-name mapping</li>
 *   <li>Rank and tier formatting with LP display</li>
 *   <li>Winrate calculation and percentage formatting</li>
 *   <li>Relative time and duration formatting</li>
 *   <li>KDA ratio calculation with perfect game handling</li>
 *   <li>Role and position label mapping with emojis</li>
 *   <li>Fallback image URL handling</li>
 * </ul>
 * 
 * @module utils/formatters
 * @author zerox80
 * @version 2.0
 */

import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime.js';
import duration from 'dayjs/plugin/duration.js';

// Extend dayjs with relative time and duration plugins
dayjs.extend(relativeTime);
dayjs.extend(duration);

/**
 * Maps queue type constants to human-readable names.
 * 
 * <p>This mapping converts Riot Games' internal queue identifiers
 * to user-friendly display names for the frontend interface.</p>
 * 
 * @constant
 * @type {Object}
 * @property {string} RANKED_SOLO_5x5 - 'Ranked Solo'
 * @property {string} RANKED_FLEX_SR - 'Ranked Flex'
 * @property {string} NORMAL - 'Normal'
 * @property {string} ARAM - 'ARAM'
 * @property {string} CLASH - 'Clash'
 * @property {string} BOT - 'Co-op vs AI'
 * @property {string} ARENA - 'Arena'
 * @property {string} URF - 'URF'
 */
const QUEUE_MAP = {
  RANKED_SOLO_5x5: 'Ranked Solo',
  RANKED_FLEX_SR: 'Ranked Flex',
  NORMAL: 'Normal',
  ARAM: 'ARAM',
  CLASH: 'Clash',
  BOT: 'Co-op vs AI',
  ARENA: 'Arena',
  URF: 'URF'
};

/**
 * Maps queue IDs to queue type constants.
 * 
 * <p>This mapping converts numeric queue IDs from the Riot API to their
 * corresponding queue type constants, which are then mapped to human-readable
 * names. This provides a two-step lookup for queue identification.</p>
 * 
 * @constant
 * @type {Object}
 * @property {string} 420 - 'RANKED_SOLO_5x5' (Ranked Solo/Duo)
 * @property {string} 440 - 'RANKED_FLEX_SR' (Ranked Flex)
 * @property {string} 400 - 'NORMAL' (Draft Normal)
 * @property {string} 430 - 'NORMAL' (Blind Normal)
 * @property {string} 450 - 'ARAM' (All Random All Mid)
 * @property {string} 700 - 'CLASH' (Clash tournament)
 * @property {string} 830/840/850 - 'BOT' (Co-op vs AI difficulties)
 * @property {string} 490 - 'NORMAL' (ARAM)
 * @property {string} 1700 - 'ARENA' (2v2v2v2 Arena)
 * @property {string} 1900 - 'URF' (Ultra Rapid Fire)
 */
const QUEUE_ID_MAP = {
  420: 'RANKED_SOLO_5x5',
  440: 'RANKED_FLEX_SR',
  400: 'NORMAL',
  430: 'NORMAL',
  450: 'ARAM',
  700: 'CLASH',
  830: 'BOT',
  840: 'BOT',
  850: 'BOT',
  490: 'NORMAL',
  1700: 'ARENA',
  1900: 'URF'
};

/**
 * Maps role constants to human-readable labels.
 * 
 * <p>This mapping converts Riot Games' internal role identifiers to
 * user-friendly display names for the frontend interface. It handles
 * both full role names and abbreviations.</p>
 * 
 * @constant
 * @type {Object}
 * @property {string} TOP - 'Top Lane'
 * @property {string} JUNGLE - 'Jungle'
 * @property {string} MIDDLE/MID - 'Mid Lane'
 * @property {string} BOTTOM/ADC - 'ADC'
 * @property {string} SUPPORT/UTILITY - 'Support'
 */
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

/**
 * Maps position constants to emoji icons.
 * 
 * <p>This mapping provides visual emoji representations for different
 * lane positions, enhancing the UI with intuitive icons.</p>
 * 
 * @constant
 * @type {Object}
 * @property {string} TOP - '🔝' (Top Lane)
 * @property {string} JUNGLE - '🌿' (Jungle)
 * @property {string} MIDDLE - '⚔️' (Mid Lane)
 * @property {string} BOTTOM - '🏹' (ADC)
 * @property {string} UTILITY - '🛡️' (Support)
 */
const POSITION_EMOJIS = {
  TOP: '🔝',
  JUNGLE: '🌿',
  MIDDLE: '⚔️',
  BOTTOM: '🏹',
  UTILITY: '🛡️'
};

/**
 * Formats a queue type constant to a human-readable name.
 * 
 * <p>This function converts Riot Games' internal queue type constants
 * to user-friendly display names. If the queue type is not found in the
 * mapping, it returns the original value or 'Unranked' as a fallback.</p>
 * 
 * @param {string} queueType - The queue type constant to format
 * @returns {string} The human-readable queue name
 * 
 * @example
 * formatQueue('RANKED_SOLO_5x5'); // 'Ranked Solo'
 * formatQueue('ARAM'); // 'ARAM'
 * formatQueue('UNKNOWN'); // 'UNKNOWN'
 * formatQueue(null); // 'Unranked'
 */
export function formatQueue(queueType) {
  return QUEUE_MAP[queueType] || queueType || 'Unranked';
}

/**
 * Formats a queue ID to a human-readable name.
 * 
 * <p>This function converts numeric queue IDs from the Riot API to
 * human-readable names. It first maps the ID to a queue type constant,
 * then formats that constant to a display name. Custom games (null ID)
 * are handled as a special case.</p>
 * 
 * @param {number|string} queueId - The queue ID to format
 * @returns {string} The human-readable queue name
 * 
 * @example
 * formatQueueById(420); // 'Ranked Solo'
 * formatQueueById(450); // 'ARAM'
 * formatQueueById(null); // 'Custom'
 * formatQueueById(999); // '999'
 */
export function formatQueueById(queueId) {
  if (queueId == null) return 'Custom';
  const mapped = QUEUE_ID_MAP[queueId];
  return formatQueue(mapped || queueId.toString());
}

/**
 * Formats rank information into a human-readable string.
 * 
 * <p>This function combines tier, rank, and LP information into a formatted
 * rank display. The tier is capitalized properly, and all components are
 * combined with appropriate separators. Unranked players are handled
 * as a special case.</p>
 * 
 * @param {string} tier - The rank tier (e.g., 'GOLD', 'PLATINUM')
 * @param {string} [rank] - The rank division (e.g., 'I', 'II', 'III', 'IV')
 * @param {number} [lp] - League Points (0-100)
 * @returns {string} Formatted rank string
 * 
 * @example
 * formatRank('GOLD', 'II', 75); // 'Gold II · 75 LP'
 * formatRank('PLATINUM', 'I', 0); // 'Platinum I · 0 LP'
 * formatRank(null, null, null); // 'Unranked'
 * formatRank('SILVER', null, 50); // 'Silver · 50 LP'
 */
export function formatRank(tier, rank, lp) {
  if (!tier) {
    return 'Unranked';
  }
  const readableTier = tier.charAt(0).toUpperCase() + tier.slice(1).toLowerCase();
  return `${readableTier} ${rank || ''} · ${lp ?? 0} LP`.trim();
}

/**
 * Calculates and formats winrate percentage.
 * 
 * <p>This function calculates the winrate from wins and losses, rounds it
 * to the nearest whole number, and formats it as a percentage with the
 * 'WR' suffix. Edge cases like zero games played are handled gracefully.</p>
 * 
 * @param {number} wins - Number of wins
 * @param {number} losses - Number of losses
 * @returns {string} Formatted winrate string (e.g., '55% WR')
 * 
 * @example
 * formatWinrate(10, 5); // '67% WR'
 * formatWinrate(0, 0); // '0% WR'
 * formatWinrate(5, 15); // '25% WR'
 */
export function formatWinrate(wins, losses) {
  const total = wins + losses;
  if (!total) {
    return '0% WR';
  }
  const wr = Math.round((wins / total) * 100);
  return `${wr}% WR`;
}

/**
 * Formats a timestamp as relative time from now.
 * 
 * <p>This function converts a timestamp to a human-readable relative time
 * string (e.g., '2 hours ago', '5 minutes ago') using dayjs. It handles
 * null/undefined timestamps gracefully by returning an empty string.</p>
 * 
 * @param {string|Date|number} timestamp - The timestamp to format
 * @returns {string} Relative time string or empty string if timestamp is invalid
 * 
 * @example
 * relativeGameTime(new Date(Date.now() - 3600000)); // 'an hour ago'
 * relativeGameTime('2023-01-01T00:00:00Z'); // '2 months ago' (depends on current time)
 * relativeGameTime(null); // ''
 */
export function relativeGameTime(timestamp) {
  if (!timestamp) return '';
  return dayjs(timestamp).fromNow();
}

/**
 * Formats duration in seconds to MM:SS format.
 * 
 * <p>This function converts a duration in seconds to a formatted time string
 * in MM:SS format. Zero duration is handled as a special case. The function
 * uses dayjs duration for accurate time calculations and pads single digits
 * with leading zeros for consistent formatting.</p>
 * 
 * @param {number} seconds - Duration in seconds
 * @returns {string} Formatted duration string in MM:SS format
 * 
 * @example
 * formatDuration(0); // '0:00'
 * formatDuration(65); // '01:05'
 * formatDuration(3661); // '61:01' (shows total minutes, not hours)
 * formatDuration(null); // '0:00'
 */
export function formatDuration(seconds) {
  if (!seconds) return '0:00';
  const dur = dayjs.duration(seconds, 'seconds');
  return `${dur.minutes().toString().padStart(2, '0')}:${dur.seconds().toString().padStart(2, '0')}`;
}

/**
 * Formats KDA (Kills/Deaths/Assists) with ratio calculation.
 * 
 * <p>This function formats the traditional KDA display with the calculated ratio.
 * Perfect games (zero deaths) are handled as a special case and show 'Perfect'
 * instead of an infinite ratio. The ratio is calculated as (kills + assists) / deaths
 * and rounded to 2 decimal places for normal games.</p>
 * 
 * @param {number} kills - Number of kills
 * @param {number} deaths - Number of deaths
 * @param {number} assists - Number of assists
 * @returns {string} Formatted KDA string with ratio (e.g., '5/2/10 · 7.50:1 KDA')
 * 
 * @example
 * formatKDA(10, 2, 5); // '10/2/5 · 7.50:1 KDA'
 * formatKDA(5, 0, 3); // '5/0/3 · Perfect KDA'
 * formatKDA(0, 5, 2); // '0/5/2 · 0.40:1 KDA'
 */
export function formatKDA(kills, deaths, assists) {
  const kda = deaths === 0 ? kills + assists : ((kills + assists) / Math.max(deaths, 1)).toFixed(2);
  return `${kills}/${deaths}/${assists} · ${deaths === 0 ? 'Perfect' : `${kda}:1`} KDA`;
}

/**
 * Returns an emoji icon for a given lane position.
 * 
 * <p>This function maps lane position constants to their corresponding
 * emoji icons for visual representation in the UI. Unknown positions
 * are mapped to a target emoji as a fallback.</p>
 * 
 * @param {string} position - The lane position (e.g., 'TOP', 'JUNGLE', 'MID')
 * @returns {string} Emoji icon for the position
 * 
 * @example
 * laneEmoji('TOP'); // '🔝'
 * laneEmoji('JUNGLE'); // '🌿'
 * laneEmoji('UNKNOWN'); // '🎯'
 */
export function laneEmoji(position) {
  return POSITION_EMOJIS[position] || '🎯';
}

/**
 * Capitalizes the first letter of a string and lowercases the rest.
 * 
 * <p>This function performs proper title case formatting by capitalizing
 * the first character and converting all subsequent characters to lowercase.
 * Empty or null strings are handled gracefully.</p>
 * 
 * @param {string} value - The string to capitalize
 * @returns {string} Capitalized string or empty string if input is falsy
 * 
 * @example
 * capitalize('hello'); // 'Hello'
 * capitalize('WORLD'); // 'World'
 * capitalize(''); // ''
 * capitalize(null); // ''
 */
export function capitalize(value) {
  if (!value) return '';
  return value.charAt(0).toUpperCase() + value.slice(1).toLowerCase();
}

/**
 * Formats a role value to a human-readable label.
 * 
 * <p>This function converts role constants to user-friendly labels.
 * It first attempts to map the role using the predefined ROLE_LABELS mapping.
 * If the role is not found, it falls back to capitalizing the role value.
 * Empty or null values are mapped to 'All Roles'.</p>
 * 
 * @param {string} value - The role value to format
 * @returns {string} Human-readable role label
 * 
 * @example
 * roleLabel('TOP'); // 'Top Lane'
 * roleLabel('MID'); // 'Mid Lane'
 * roleLabel('UNKNOWN'); // 'Unknown'
 * roleLabel(null); // 'All Roles'
 */
export function roleLabel(value) {
  if (!value) return 'All Roles';
  const upper = value.toUpperCase();
  return ROLE_LABELS[upper] || capitalize(value);
}

/**
 * Returns a fallback image URL if the provided URL is invalid.
 * 
 * <p>This function validates image URLs and provides a fallback URL
 * if the original URL is null, undefined, or doesn't start with 'http'.
 * This is useful for handling missing or broken image URLs gracefully.</p>
 * 
 * @param {string} url - The original image URL to validate
 * @param {string} fallback - The fallback URL to use if original is invalid
 * @returns {string} Valid image URL (original or fallback)
 * 
 * @example
 * fallbackImage('https://example.com/image.jpg', '/default.jpg'); // 'https://example.com/image.jpg'
 * fallbackImage(null, '/default.jpg'); // '/default.jpg'
 * fallbackImage('invalid-url', '/default.jpg'); // '/default.jpg'
 */
export function fallbackImage(url, fallback) {
  if (url && url.startsWith('http')) return url;
  return fallback;
}
