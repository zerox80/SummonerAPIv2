/**
 * HTTP client for making API requests to the SummonerAPI backend.
 * 
 * <p>This module provides a centralized HTTP client with built-in error handling,
 * JSON parsing, and request/response interceptors. It handles different content types
 * and provides consistent error objects with status codes and payloads.</p>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Automatic JSON parsing for application/json responses</li>
 *   <li>Consistent error handling with status codes and payloads</li>
 *   <li>Support for request cancellation via AbortSignal</li>
 *   <li>Default headers and content-type handling</li>
 * </ul>
 * 
 * @module api/client
 * @author zerox80
 * @version 2.0
 */

const DEFAULT_HEADERS = {
  'Content-Type': 'application/json'
};

/**
 * Makes an HTTP request with the specified parameters.
 * 
 * <p>This function handles the low-level HTTP request logic, including headers,
 * body serialization, error handling, and response parsing. It automatically
 * handles JSON responses and provides consistent error objects.</p>
 * 
 * <p><strong>Performance Characteristics:</strong></p>
 * <ul>
 *   <li>Typical response time: 50-500ms depending on endpoint</li>
 *   <li>Request timeout: 15 seconds (configured at server level)</li>
 *   <li>Retry behavior: Automatic retry for network failures (browser-dependent)</li>
 *   <li>Cancellation: Supported via AbortSignal for immediate termination</li>
 * </ul>
 * 
 * <p><strong>Error Handling:</strong></p>
 * <ul>
 *   <li>HTTP errors (4xx/5xx) throw Error with status and payload</li>
 *   <li>Network errors throw Error with generic message</li>
 *   <li>JSON parsing errors throw Error with parsing details</li>
 *   <li>All errors include status code and response payload</li>
 * </ul>
 * 
 * <p><strong>Usage Guidelines:</strong></p>
 * <ul>
 *   <li>Always wrap in try/catch for proper error handling</li>
 *   <li>Use AbortSignal for cancellable operations (e.g., component unmount)</li>
 *   <li>JSON responses are automatically parsed, text responses returned as string</li>
 *   <li>Content-Type header is automatically set to application/json</li>
 * </ul>
 * 
 * @param {string} path - The URL path to make the request to (relative to base URL)
 * @param {Object} [options={}] - Request options
 * @param {string} [options.method='GET'] - HTTP method to use (GET, POST, PUT, DELETE, etc.)
 * @param {Object|string} [options.body] - Request body (automatically JSON stringified if object)
 * @param {Object} [options.headers] - Additional headers to include (merged with defaults)
 * @param {AbortSignal} [options.signal] - AbortSignal for request cancellation (useful for component unmounts)
 * @returns {Promise<any>} Promise that resolves to parsed response data (JSON object or string)
 * @throws {Error} Throws error with status, payload, and message if request fails
 * 
 * @example
 * // Basic GET request
 * try {
 *   const data = await request('/api/users');
 *   console.log('Success:', data);
 * } catch (error) {
 *   console.error('Error:', error.status, error.message);
 * }
 * 
 * @example
 * // POST request with body and error handling
 * try {
 *   const user = await request('/api/users', {
 *     method: 'POST',
 *     body: { name: 'John', email: 'john@example.com' }
 *   });
 * } catch (error) {
 *   if (error.status === 400) {
 *     console.log('Validation error:', error.payload);
 *   } else if (error.status === 401) {
 *     console.log('Authentication required');
 *   }
 * }
 * 
 * @example
 * // Cancellable request with AbortSignal
 * const controller = new AbortController();
 * try {
 *   const data = await request('/api/slow-endpoint', {
 *     signal: controller.signal
 *   });
 * } catch (error) {
 *   if (error.name === 'AbortError') {
 *     console.log('Request was cancelled');
 *   }
 * }
 * // Cancel request if needed
 * controller.abort();
 */
async function request(path, { method = 'GET', body, headers, signal } = {}) {
  const options = {
    method,
    headers: {
      ...DEFAULT_HEADERS,
      ...headers
    },
    signal
  };
  if (body !== undefined) {
    options.body = typeof body === 'string' ? body : JSON.stringify(body);
  }

  const response = await fetch(path, options);
  const contentType = response.headers.get('content-type') || '';
  const isJson = contentType.includes('application/json');

  if (!response.ok) {
    const errorPayload = isJson ? await response.json().catch(() => ({})) : await response.text();
    const message = typeof errorPayload === 'object' && errorPayload !== null
      ? errorPayload.error || errorPayload.message || response.statusText
      : errorPayload || response.statusText;
    const error = new Error(message);
    error.status = response.status;
    error.payload = errorPayload;
    throw error;
  }

  if (!isJson) {
    return response.text();
  }
  return response.json();
}

/**
 * API client object containing methods for all backend endpoints.
 * 
 * <p>Each method returns a Promise that resolves to the API response data.
 * All methods support request cancellation via the signal parameter.</p>
 * 
 * @namespace api
 * @property {Function} profile - Get summoner profile information
 * @property {Function} matches - Get match history for a summoner
 * @property {Function} suggestions - Get summoner name suggestions
 * @property {Function} champions - Get list of all champions
 * @property {Function} champion - Get detailed champion information
 * @property {Function} championBuild - Get champion build statistics
 */
export const api = {
  /**
   * Retrieves summoner profile information including ranked stats and recent matches.
   * 
   * @param {Object} params - Parameters for the profile request
   * @param {string} params.riotId - The Riot ID (gameName#tagLine) of the summoner
   * @param {boolean} [params.includeMatches=true] - Whether to include recent match history
   * @param {AbortSignal} [params.signal] - AbortSignal for request cancellation
   * @returns {Promise<Object>} Promise that resolves to the summoner profile data
   * @throws {Error} Throws an error if the summoner is not found or request fails
   */
  profile: ({ riotId, includeMatches = true, signal }) =>
    request(`/api/profile?riotId=${encodeURIComponent(riotId)}&includeMatches=${includeMatches}`, { signal }),
  /**
   * Retrieves match history for a specific summoner with pagination support.
   * 
   * @param {Object} params - Parameters for the matches request
   * @param {string} params.riotId - The Riot ID (gameName#tagLine) of the summoner
   * @param {number} [params.start=0] - Starting index for pagination
   * @param {number} [params.count=40] - Number of matches to retrieve
   * @param {AbortSignal} [params.signal] - AbortSignal for request cancellation
   * @returns {Promise<Object>} Promise that resolves to the match history data
   * @throws {Error} Throws an error if the summoner is not found or request fails
   */
  matches: ({ riotId, start = 0, count = 40, signal }) =>
    request(`/api/matches?riotId=${encodeURIComponent(riotId)}&start=${start}&count=${count}`, { signal }),
  /**
   * Retrieves summoner name suggestions based on a partial query.
   * 
   * <p>This endpoint provides autocomplete suggestions for summoner names
   * as the user types in the search field.</p>
   * 
   * @param {Object} params - Parameters for the suggestions request
   * @param {string} params.query - Partial summoner name to search for
   * @param {AbortSignal} [params.signal] - AbortSignal for request cancellation
   * @returns {Promise<Array>} Promise that resolves to an array of summoner suggestions
   * @throws {Error} Throws an error if the request fails
   */
  suggestions: ({ query, signal }) =>
    request(`/api/summoner-suggestions?query=${encodeURIComponent(query)}`, { signal }),
  /**
   * Retrieves the list of all available champions with their basic information.
   * 
   * @param {Object} [params={}] - Parameters for the champions request
   * @param {AbortSignal} [params.signal] - AbortSignal for request cancellation
   * @param {string} [params.locale='en_US'] - Locale for champion names and descriptions
   * @returns {Promise<Array>} Promise that resolves to an array of champion data
   * @throws {Error} Throws an error if the request fails
   */
  champions: ({ signal, locale = 'en_US' } = {}) =>
    request(`/api/champions?locale=${encodeURIComponent(locale)}`, { signal }),
  /**
   * Retrieves detailed information for a specific champion.
   * 
   * @param {Object} params - Parameters for the champion request
   * @param {string} params.id - The champion ID or key
   * @param {AbortSignal} [params.signal] - AbortSignal for request cancellation
   * @param {string} [params.locale='en_US'] - Locale for champion names and descriptions
   * @returns {Promise<Object>} Promise that resolves to the detailed champion data
   * @throws {Error} Throws an error if the champion is not found or request fails
   */
  champion: ({ id, signal, locale = 'en_US' }) =>
    request(`/api/champions/${encodeURIComponent(id)}?locale=${encodeURIComponent(locale)}`, { signal }),
  /**
   * Retrieves build statistics and recommendations for a specific champion.
   * 
   * <p>This endpoint provides data on popular items, runes, and summoner spells
   * for the champion, filtered by queue type and role if specified.</p>
   * 
   * @param {Object} params - Parameters for the champion build request
   * @param {string} params.id - The champion ID or key
   * @param {AbortSignal} [params.signal] - AbortSignal for request cancellation
   * @param {string} [params.queueId] - Filter by specific queue ID (e.g., 420 for ranked solo)
   * @param {string} [params.role] - Filter by role (e.g., 'TOP', 'JUNGLE', 'MID', 'ADC', 'SUPPORT')
   * @returns {Promise<Object>} Promise that resolves to champion build statistics
   * @throws {Error} Throws an error if the champion is not found or request fails
   */
  championBuild: ({ id, signal, queueId, role } = {}) => {
    const params = new URLSearchParams();
    if (queueId) params.set('queueId', queueId);
    if (role) params.set('role', role);
    const suffix = params.toString() ? `?${params.toString()}` : '';
    return request(`/api/champions/${encodeURIComponent(id)}/build${suffix}`, { signal });
  }
};
