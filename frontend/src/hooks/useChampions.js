import { useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { api } from '../api/client.js';

export function useChampions(options = {}) {
  const [search, setSearch] = useState('');
  const [role, setRole] = useState('ALL');
  const [sort, setSort] = useState('alpha');

  const query = useQuery({
    queryKey: ['champions'],
    queryFn: () => api.champions(options.requestOptions),
    staleTime: 30 * 60 * 1000
  });

  const filtered = useMemo(() => {
    if (!Array.isArray(query.data)) return [];
    const lowered = search.trim().toLowerCase();

    let list = query.data;
    if (lowered.length > 0) {
      list = list.filter((champ) =>
        champ.name?.toLowerCase().includes(lowered) || champ.title?.toLowerCase().includes(lowered)
      );
    }
    if (role !== 'ALL') {
      list = list.filter((champ) => champ.tags?.some((tag) => tag?.toUpperCase() === role));
    }
    if (sort === 'alpha') {
      list = [...list].sort((a, b) => a.name.localeCompare(b.name));
    }
    if (sort === 'roles') {
      list = [...list].sort((a, b) => (a.tags?.[0] || '').localeCompare(b.tags?.[0] || ''));
    }
    return list;
  }, [query.data, search, role, sort]);

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
