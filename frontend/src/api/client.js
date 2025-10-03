const DEFAULT_HEADERS = {
  'Content-Type': 'application/json'
};

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

export const api = {
  profile: ({ riotId, includeMatches = true, signal }) =>
    request(`/api/profile?riotId=${encodeURIComponent(riotId)}&includeMatches=${includeMatches}`, { signal }),
  matches: ({ riotId, start = 0, count = 10, signal }) =>
    request(`/api/matches?riotId=${encodeURIComponent(riotId)}&start=${start}&count=${count}`, { signal }),
  suggestions: ({ query, signal }) =>
    request(`/api/summoner-suggestions?query=${encodeURIComponent(query)}`, { signal }),
  champions: ({ signal } = {}) => request('/api/champions', { signal }),
  champion: ({ id, signal }) => request(`/api/champions/${encodeURIComponent(id)}`, { signal }),
  championBuild: ({ id, signal, queueId, role } = {}) => {
    const params = new URLSearchParams();
    if (queueId) params.set('queueId', queueId);
    if (role) params.set('role', role);
    const suffix = params.toString() ? `?${params.toString()}` : '';
    return request(`/api/champions/${encodeURIComponent(id)}/build${suffix}`, { signal });
  }
};
