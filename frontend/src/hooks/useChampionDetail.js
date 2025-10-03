import { useQuery } from '@tanstack/react-query';
import { api } from '../api/client.js';

export function useChampionDetail(id, options = {}) {
  return useQuery({
    queryKey: ['champion-detail', id],
    queryFn: () => api.champion({ id, ...options.requestOptions }),
    enabled: Boolean(id),
    staleTime: 30 * 60 * 1000
  });
}

export function useChampionBuild(id, { queueId, role, enabled = true } = {}) {
  return useQuery({
    queryKey: ['champion-build', id, queueId, role],
    queryFn: () => api.championBuild({ id, queueId, role }),
    enabled: Boolean(id) && enabled
  });
}
