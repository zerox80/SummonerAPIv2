/**
 * React hooks for fetching champion data from the API.
 * 
 * <p>This module provides custom hooks that wrap React Query for fetching
 * champion details and build statistics. These hooks handle caching, background
 * refetching, and loading states automatically, providing a clean interface
 * for components to consume champion data.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Automatic caching with 30-minute stale time</li>
 *   <li>Conditional fetching based on champion ID availability</li>
 *   <li>Support for queue and role filtering for build data</li>
 *   <li>Error handling and loading state management</li>
 * </ul>
 * 
 * @module hooks/useChampionDetail
 * @author zerox80
 * @version 2.0
 */

import { useQuery } from '@tanstack/react-query';
import { api } from '../api/client.js';

/**
 * Custom hook for fetching detailed champion information.
 * 
 * <p>This hook fetches comprehensive champion data including abilities, lore,
 * stats, and other detailed information. It uses React Query for caching and
 * automatically handles loading and error states.</p>
 * 
 * @param {string} id - The champion ID or key to fetch details for
 * @param {Object} [options={}] - Additional options for the hook
 * @param {Object} [options.requestOptions] - Options passed to the API request
 * @param {AbortSignal} [options.requestOptions.signal] - AbortSignal for request cancellation
 * @param {string} [options.requestOptions.locale='en_US'] - Locale for champion data
 * @returns {Object} React Query result object with data, error, and loading states
 * @returns {Object} returns.data - Champion detail data if successful
 * @returns {Error} returns.error - Error object if request failed
 * @returns {boolean} returns.isLoading - True if request is in progress
 * @returns {boolean} returns.isError - True if request failed
 * @returns {boolean} returns.isSuccess - True if request succeeded
 * 
 * @example
 * // Basic usage
 * const { data: champion, isLoading, error } = useChampionDetail('Anivia');
 * 
 * @example
 * // With custom locale
 * const { data: champion } = useChampionDetail('Anivia', {
 *   requestOptions: { locale: 'ko_KR' }
 * });
 */
export function useChampionDetail(id, options = {}) {
  return useQuery({
    queryKey: ['champion-detail', id],
    queryFn: () => api.champion({ id, ...options.requestOptions }),
    enabled: Boolean(id),
    staleTime: 30 * 60 * 1000
  });
}

/**
 * Custom hook for fetching champion build statistics and recommendations.
 * 
 * <p>This hook fetches statistical data on popular items, runes, and summoner spells
 * for a specific champion. The data can be filtered by queue type and role to provide
 * more targeted recommendations. It uses React Query for caching and automatically
 * handles loading and error states.</p>
 * 
 * @param {string} id - The champion ID or key to fetch build data for
 * @param {Object} [options={}] - Additional options for the hook
 * @param {string} [options.queueId] - Filter by specific queue ID (e.g., '420' for ranked solo)
 * @param {string} [options.role] - Filter by role (e.g., 'TOP', 'JUNGLE', 'MID', 'ADC', 'SUPPORT')
 * @param {boolean} [options.enabled=true] - Whether to enable the query (useful for conditional fetching)
 * @returns {Object} React Query result object with data, error, and loading states
 * @returns {Object} returns.data - Champion build statistics if successful
 * @returns {Error} returns.error - Error object if request failed
 * @returns {boolean} returns.isLoading - True if request is in progress
 * @returns {boolean} returns.isError - True if request failed
 * @returns {boolean} returns.isSuccess - True if request succeeded
 * 
 * @example
 * // Basic usage
 * const { data: build, isLoading, error } = useChampionBuild('Anivia');
 * 
 * @example
 * // Filtered by queue and role
 * const { data: build } = useChampionBuild('Anivia', {
 *   queueId: '420',
 *   role: 'MID'
 * });
 * 
 * @example
 * // Conditional fetching
 * const { data: build } = useChampionBuild('Anivia', {
 *   enabled: shouldShowBuild
 * });
 */
export function useChampionBuild(id, { queueId, role, enabled = true } = {}) {
  return useQuery({
    queryKey: ['champion-build', id, queueId, role],
    queryFn: () => api.championBuild({ id, queueId, role }),
    enabled: Boolean(id) && enabled
  });
}
