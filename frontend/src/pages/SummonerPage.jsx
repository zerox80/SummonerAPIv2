import { useEffect, useMemo, useState } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useSearchParams } from 'react-router-dom';
import { api } from '../api/client.js';
import { LoadingState, ErrorState, EmptyState } from '../components/LoadState.jsx';
import SearchPanel from '../sections/summoner/SearchPanel.jsx';
import SummonerHeader from '../sections/summoner/SummonerHeader.jsx';
import RankedOverview from '../sections/summoner/RankedOverview.jsx';
import PerformanceSummary from '../sections/summoner/PerformanceSummary.jsx';
import TopChampions from '../sections/summoner/TopChampions.jsx';
import MatchHistory from '../sections/summoner/MatchHistory.jsx';
import '../styles/page-summoner.css';


export default function SummonerPage() {
  const queryClient = useQueryClient();
  const [searchParams, setSearchParams] = useSearchParams();
  const riotIdParam = (searchParams.get('riotId') || '').trim();
  const [matches, setMatches] = useState([]);
  const [range, setRange] = useState('40');
  const [filters, setFilters] = useState({ queue: 'ALL', result: 'ALL', role: 'ALL' });
  const [hasMoreMatches, setHasMoreMatches] = useState(false);
  const [isFetchingMore, setIsFetchingMore] = useState(false);

  
  const profileQuery = useQuery({
    queryKey: ['profile', riotIdParam],
    queryFn: () => api.profile({ riotId: riotIdParam, includeMatches: true }),
    enabled: riotIdParam.length > 0,
    staleTime: 60_000
  });

  
  useEffect(() => {
    if (profileQuery.data?.matchHistory) {
      const initialMatches = profileQuery.data.matchHistory;
      setMatches(initialMatches);
      const size = initialMatches.length;
      const pageSize = profileQuery.data.matchesPageSize || 40;
      setHasMoreMatches(size >= pageSize);
    } else {
      setMatches([]);
      setHasMoreMatches(false);
    }
  }, [profileQuery.data]);

  
  const handleSubmitRiotId = (riotId) => {
    if (riotId && riotId !== riotIdParam) {
      setSearchParams({ riotId });
    }
  };

  
  const riotIdResolved = profileQuery.data?.riotId || riotIdParam;

  
  const derived = useMemo(() => {
    if (!profileQuery.data || matches.length === 0) {
      return {
        totalWins: 0,
        totalLosses: 0,
        avgKda: '0/0/0',
        kdaRatio: '0.0',
        avgCsPerMin: 0,
        avgGold: 0,
        avgDuration: 0,
        winRate: 0,
        avgDamage: 0,
        avgVision: 0,
        killParticipation: 0
      };
    }

    const targetCount = range === 'all' ? matches.length : Number(range);
    const filteredMatches = matches.slice(0, targetCount);

    let wins = 0;
    let losses = 0;
    let kills = 0;
    let deaths = 0;
    let assists = 0;
    let cs = 0;
    let gold = 0;
    let durationSeconds = 0;
    let damage = 0;
    let vision = 0;
    let killParticipation = 0;
    
    
    filteredMatches.forEach((match) => {
      const { info } = match;
      if (!info || !info.participants) return;
      const participant = info.participants.find((p) => p && p.puuid === profileQuery.data?.summoner?.puuid);
      if (!participant) return;
      if (participant.win) wins += 1; else losses += 1;
      kills += participant.kills;
      deaths += participant.deaths;
      assists += participant.assists;
      cs += (participant.totalMinionsKilled || 0) + (participant.neutralMinionsKilled || 0);
      gold += participant.goldEarned || 0;
      durationSeconds += info.gameDuration || 0;
      if (participant.totalDamageDealtToChampions) {
        damage += participant.totalDamageDealtToChampions;
      }
      if (typeof participant.visionScore === 'number') {
        vision += participant.visionScore;
      }
      if (participant.teamId != null) {
        const team = info.participants.filter((p) => p.teamId === participant.teamId);
        const teamKills = team.reduce((acc, curr) => acc + (curr?.kills || 0), 0);
        if (teamKills > 0) {
          killParticipation += ((participant.kills + participant.assists) / teamKills);
        }
      }
    });
    
    const totalGames = wins + losses;
    const kdaRatio = deaths === 0 ? 'Perfect' : ((kills + assists) / Math.max(deaths, 1)).toFixed(2);
    const avgCsPerMin = totalGames === 0 || durationSeconds === 0 ? 0 : (cs / (durationSeconds / 60)).toFixed(1);
    const avgGold = totalGames === 0 ? 0 : Math.round(gold / totalGames);
    const avgDuration = totalGames === 0 ? 0 : Math.round(durationSeconds / totalGames);
    const avgDamage = totalGames === 0 ? 0 : Math.round(damage / totalGames);
    const avgVision = totalGames === 0 ? 0 : (vision / totalGames).toFixed(1);
    const winRate = totalGames === 0 ? 0 : Math.round((wins / totalGames) * 100);
    const kp = totalGames === 0 ? 0 : Math.round((killParticipation / totalGames) * 100);
    return {
      totalWins: wins,
      totalLosses: losses,
      avgKda: `${Math.round(kills / Math.max(totalGames, 1))}/${Math.round(deaths / Math.max(totalGames, 1))}/${Math.round(assists / Math.max(totalGames, 1))}`,
      kdaRatio,
      avgCsPerMin,
      avgGold,
      avgDuration,
      winRate,
      avgDamage,
      avgVision,
      killParticipation: kp
    };
  }, [matches, profileQuery.data, range]);

  
  const handleLoadMore = async () => {
    if (!riotIdResolved || isFetchingMore || !hasMoreMatches) return;
    setIsFetchingMore(true);
    const start = matches.length;
    const count = profileQuery.data?.matchesPageSize || 40;
    try {
      const more = await api.matches({ riotId: riotIdResolved, start, count });
      if (Array.isArray(more) && more.length > 0) {
        setMatches((prev) => [...prev, ...more]);
        setHasMoreMatches(more.length >= count);
      } else {
        setHasMoreMatches(false);
      }
    } catch (error) {
      setHasMoreMatches(false);
    } finally {
      setIsFetchingMore(false);
    }
  };

  
  const handleRangeChange = (newRange) => {
    setRange(newRange);
  };

  
  useEffect(() => {
    setHasMoreMatches(false);
    if (!riotIdParam) {
      queryClient.removeQueries({ queryKey: ['profile'] });
      setMatches([]);
    }
  }, [queryClient, riotIdParam]);

  
  useEffect(() => {
    const autoLoadMatches = async () => {
      if (!riotIdResolved || isFetchingMore) return;

      const targetCount = range === '100' ? 100 : (range === 'all' ? 999999 : Number(range));
      if (matches.length >= targetCount || !hasMoreMatches) return;

      setIsFetchingMore(true);
      const start = matches.length;
      const count = profileQuery.data?.matchesPageSize || 40;

      try {
        const more = await api.matches({ riotId: riotIdResolved, start, count });
        if (Array.isArray(more) && more.length > 0) {
          setMatches((prev) => [...prev, ...more]);
          setHasMoreMatches(more.length >= count);
        } else {
          setHasMoreMatches(false);
        }
      } catch (error) {
        setHasMoreMatches(false);
      } finally {
        setIsFetchingMore(false);
      }
    };

    autoLoadMatches();
  }, [range, matches.length, hasMoreMatches, riotIdResolved, isFetchingMore, profileQuery.data]);

  return (
    <div className="summoner-page">
      <SearchPanel onSubmit={handleSubmitRiotId} initialValue={riotIdParam} isLoading={profileQuery.isFetching} />

      {!riotIdParam && (
        <EmptyState
          title="Search for a Riot ID"
          description="Example: HideonBush#KR1 or G2 Caps#1323. Get a blazing-fast analysis dashboard."
        />
      )}

      {profileQuery.isLoading && riotIdParam && <LoadingState message="Loading profile ..." />}

      {profileQuery.isError && riotIdParam && (
        <ErrorState
          message={profileQuery.error?.message || 'Failed to load profile'}
          onRetry={profileQuery.refetch}
        />
      )}

      {profileQuery.data && !profileQuery.data.error && (
        <div className="summoner-page__grid">
          <div className="summoner-page__primary">
            <SummonerHeader profile={profileQuery.data} derived={derived} />
            <PerformanceSummary
              derived={derived}
              matches={matches}
              summoner={profileQuery.data.summoner}
              range={range}
              onRangeChange={handleRangeChange}
            />
            <MatchHistory
              matches={matches}
              summoner={profileQuery.data.summoner}
              filters={filters}
              onFiltersChange={setFilters}
              fetchMore={handleLoadMore}
              hasMore={hasMoreMatches}
              isFetchingMore={isFetchingMore}
              championSquares={profileQuery.data.championSquares}
              bases={profileQuery.data?.bases}
              matchesPageSize={profileQuery.data?.matchesPageSize}
            />
          </div>
          <aside className="summoner-page__sidebar">
            <RankedOverview entries={profileQuery.data.leagueEntries} bases={profileQuery.data.bases} />
            <TopChampions
              championPlayCounts={profileQuery.data.championPlayCounts}
              matches={matches}
              summoner={profileQuery.data.summoner}
              bases={profileQuery.data.bases}
              championSquares={profileQuery.data.championSquares}
              range={range}
            />
          </aside>
        </div>
      )}

      {profileQuery.data?.error && (
        <ErrorState message={profileQuery.data.error} />
      )}
    </div>
  );
}
