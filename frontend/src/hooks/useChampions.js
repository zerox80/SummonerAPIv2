import { useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { api } from '../api/client.js';

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
