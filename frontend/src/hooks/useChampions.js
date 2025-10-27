/**
 * React hooks for managing champion data and filtering operations.
 * 
 * <p>This module provides custom hooks for fetching champion data from the API
 * and filtering champions based on search terms, roles, and sorting preferences.
 * It uses React Query for data fetching and caching, and provides a clean
 * interface for champion list management.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Real-time champion filtering by name and title</li>
 *   <li>Role-based filtering (TOP, JUNGLE, MID, ADC, SUPPORT)</li>
 *   <li>Alphabetical and role-based sorting</li>
 *   <li>React Query integration for caching and background updates</li>
 *   <li>Type-safe filtering with default values</li>
 * </ul>
 * 
 * @module hooks/useChampions
 * @author zerox80
 * @version 2.0
 */

import { useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { api } from '../api/client.js';

/**
 * Filters an array of champions based on search criteria, role, and sorting preferences.
 * 
 * <p>This function provides comprehensive filtering capabilities for champion lists,
 * supporting text search across champion names and titles, role filtering, and
 * multiple sorting options. It's designed to be pure and side-effect free.</p>
 * 
 * @param {Array} champions - Array of champion objects to filter
 * @param {Object} [options={}] - Filtering options
 * @param {string} [options.search=''] - Search term to filter by champion name or title
 * @param {string} [options.role='ALL'] - Role to filter by ('ALL', 'TOP', 'JUNGLE', 'MID', 'ADC', 'SUPPORT')
 * @param {string} [options.sort='alpha'] - Sort order ('alpha' for alphabetical, 'roles' for role-based)
 * @returns {Array} Filtered and sorted array of champions
 * 
 * @example
 * // Filter by search term
 * const filtered = filterChampions(champions, { search: 'Ahri' });
 * 
 * @example
 * // Filter by role and sort
 * const midChamps = filterChampions(champions, { role: 'MID', sort: 'alpha' });
 */
export function filterChampions(champions, { search = '', role = 'ALL', sort = 'alpha' } = {}) {
  if (!Array.isArray(champions)) {
    return [];
  }

  const lowered = search.trim().toLowerCase();
  let list = champions;

  if (lowered.length > 0) {
    list = list.filter((champ) =>
      champ.name?.toLowerCase().includes(lowered) || champ.title?.toLowerCase().includes(lowered)
    );
  }

  if (role !== 'ALL') {
    const roleUpper = role.toUpperCase();
    list = list.filter((champ) => champ.tags?.some((tag) => tag?.toUpperCase() === roleUpper));
  }

  if (sort === 'alpha') {
    list = [...list].sort((a, b) => a.name.localeCompare(b.name));
  } else if (sort === 'roles') {
    list = [...list].sort((a, b) => (a.tags?.[0] || '').localeCompare(b.tags?.[0] || ''));
  }

  return list;
}

/**
 * Custom React hook for managing champion data with filtering capabilities.
 * 
 * <p>This hook provides a complete solution for fetching and managing champion data,
 * including search functionality, role filtering, and sorting. It returns the filtered
 * results along with state setters and loading states from React Query.</p>
 * 
 * <p>The hook automatically handles data fetching, caching, and background refetching.
 * Champion data is cached for 30 minutes to reduce API calls.</p>
 * 
 * @param {Object} [options={}] - Hook configuration options
 * @param {Object} [options.requestOptions] - Additional options to pass to the API request
 * @returns {Object} Hook result object containing champion data and controls
 * @returns {Array} returns.champions - Filtered and sorted champion array
 * @returns {boolean} returns.isLoading - Whether the data is currently loading
 * @returns {boolean} returns.isError - Whether there was an error fetching data
 * @returns {boolean} returns.isSuccess - Whether the data was successfully fetched
 * @returns {string} returns.search - Current search term
 * @returns {string} returns.role - Current role filter
 * @returns {string} returns.sort - Current sort preference
 * @returns {Function} returns.setSearch - Function to update search term
 * @returns {Function} returns.setRole - Function to update role filter
 * @returns {Function} returns.setSort - Function to update sort preference
 * 
 * @example
 * // Basic usage
 * const { champions, isLoading, setSearch, setRole } = useChampions();
 * 
 * @example
 * // With custom request options
 * const { champions } = useChampions({ requestOptions: { locale: 'de_DE' } });
 */
export function useChampions(options = {}) {
  const [search, setSearch] = useState('');
  const [role, setRole] = useState('ALL');
  const [sort, setSort] = useState('alpha');

  const query = useQuery({
    queryKey: ['champions'],
    queryFn: () => api.champions(options.requestOptions),
    staleTime: 30 * 60 * 1000
  });

  const filtered = useMemo(
    () => filterChampions(query.data, { search, role, sort }),
    [query.data, search, role, sort]
  );

  return {
    ...query,
    champions: filtered,
    search,
    role,
    sort,
    setSearch,
    setRole,
    setSort
  };
}
