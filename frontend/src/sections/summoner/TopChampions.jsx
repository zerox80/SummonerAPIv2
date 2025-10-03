import { useMemo } from 'react';
import PropTypes from 'prop-types';
import Tag from '../../components/Tag.jsx';
import '../../styles/summoner/top-champions.css';

export default function TopChampions({ championPlayCounts, matches, summoner, championSquares }) {
  const enrichedChampions = useMemo(() => {
    if (!championPlayCounts || !matches || !summoner) return [];
    
    const result = Object.entries(championPlayCounts).map(([championName, playCount]) => {
      const championMatches = matches.filter((match) => {
        if (!match?.info?.participants) return false;
        const participant = match.info.participants.find((p) => p?.puuid === summoner.puuid);
        return participant && participant.championName === championName;
      });

      let wins = 0;
      let losses = 0;
      let kills = 0;
      let deaths = 0;
      let assists = 0;

      championMatches.forEach((match) => {
        const participant = match.info.participants.find((p) => p?.puuid === summoner.puuid);
        if (participant) {
          if (participant.win) wins += 1; else losses += 1;
          kills += participant.kills || 0;
          deaths += participant.deaths || 0;
          assists += participant.assists || 0;
        }
      });

      const totalGames = wins + losses;
      const winrate = totalGames > 0 ? Math.round((wins / totalGames) * 100) : 0;
      const avgKda = totalGames > 0 ? ((kills + assists) / Math.max(deaths, 1)).toFixed(2) : '0.00';
      const imageUrl = championSquares?.[championName] || `https://ddragon.leagueoflegends.com/cdn/14.19.1/img/champion/${championName}.png`;

      return {
        name: championName,
        playCount,
        wins,
        losses,
        winrate,
        avgKda,
        imageUrl
      };
    });

    return result.sort((a, b) => b.playCount - a.playCount).slice(0, 6);
  }, [championPlayCounts, matches, summoner, championSquares]);

  if (enrichedChampions.length === 0) {
    return (
      <section className="top-champions glass-panel">
        <header className="top-champions__header">
          <p className="badge-soft">Champion Pool</p>
          <h3>Your Top Champions</h3>
        </header>
        <div className="top-champions__empty">
          <p>No champion data yet</p>
        </div>
      </section>
    );
  }

  return (
    <section className="top-champions glass-panel">
      <header className="top-champions__header">
        <p className="badge-soft">Champion Pool</p>
        <h3>Your Top Champions</h3>
        <p className="top-champions__subtitle">Your most played champs with performance insights</p>
      </header>
      <div className="top-champions__grid">
        {enrichedChampions.map((champ) => (
          <article key={champ.name} className="champion-card">
            <div className="champion-card__media">
              <img src={champ.imageUrl} alt={champ.name} loading="lazy" />
            </div>
            <div className="champion-card__content">
              <h4>{champ.name}</h4>
              <div className="champion-card__stats">
                <span>{champ.playCount} games</span>
                <Tag tone={champ.winrate >= 50 ? 'success' : 'danger'}>{champ.winrate}% WR</Tag>
              </div>
              <div className="champion-card__meta">
                <span>KDA: {champ.avgKda}</span>
                <span>{champ.wins}W / {champ.losses}L</span>
              </div>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

TopChampions.propTypes = {
  championPlayCounts: PropTypes.object,
  matches: PropTypes.arrayOf(PropTypes.object),
  summoner: PropTypes.shape({
    puuid: PropTypes.string
  }),
  championSquares: PropTypes.object
};
