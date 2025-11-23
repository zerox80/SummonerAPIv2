import { useMemo } from 'react';
import PropTypes from 'prop-types';
import Tag from '../../components/Tag.jsx';
import '../../styles/summoner/top-champions.css';

function enrichChampions(championPlayCounts, matches, summoner, championSquares, bases) {
  if (!Array.isArray(matches) || !summoner) return [];

  let sourceCounts = championPlayCounts;

  // Fallback: Calculate from matches if backend data is missing
  if (!Array.isArray(sourceCounts) || sourceCounts.length === 0) {
    const counts = {};
    matches.forEach((m) => {
      const p = m.info.participants.find((part) => part.puuid === summoner.puuid);
      if (p) {
        counts[p.championName] = (counts[p.championName] || 0) + 1;
      }
    });
    sourceCounts = Object.entries(counts)
      .map(([name, count]) => ({ championName: name, count }))
      .sort((a, b) => b.count - a.count)
      .slice(0, 5);
  }

  return sourceCounts.map((champ) => {
    const champMatches = matches.filter((m) => {
      const p = m.info.participants.find((part) => part.puuid === summoner.puuid);
      return p && p.championName === champ.championName;
    });

    const wins = champMatches.filter((m) => {
      const p = m.info.participants.find((part) => part.puuid === summoner.puuid);
      return p.win;
    }).length;

    const total = champMatches.length;
    // If we derived counts from matches, total should match count. 
    // If using backend data, total might differ if matches array is partial.
    // We use the calculated total from available matches for consistency in stats.

    const losses = total - wins;
    const winrate = total > 0 ? Math.round((wins / total) * 100) : 0;

    const kdaSum = champMatches.reduce((acc, m) => {
      const p = m.info.participants.find((part) => part.puuid === summoner.puuid);
      const kda = p.deaths === 0 ? p.kills + p.assists : (p.kills + p.assists) / p.deaths;
      return acc + kda;
    }, 0);

    const avgKda = total > 0 ? (kdaSum / total).toFixed(2) : '0.00';

    // Construct image URL
    let imageUrl = '';
    if (championSquares && championSquares[champ.championName]) {
      const version = bases?.version || 'latest'; // Fallback if version missing
      imageUrl = `https://ddragon.leagueoflegends.com/cdn/${version}/img/champion/${championSquares[champ.championName].image.full}`;
    }

    return {
      name: champ.championName,
      playCount: total > 0 ? total : champ.count, // Prefer local count if available
      wins,
      losses,
      winrate,
      avgKda,
      imageUrl
    };
  }).filter(c => c.playCount > 0); // Only show champions we actually have data for
}

export default function TopChampions({ championPlayCounts, matches, summoner, championSquares, bases, range }) {
  const enrichedChampions = useMemo(
    () => enrichChampions(championPlayCounts, matches, summoner, championSquares, bases),
    [championPlayCounts, matches, summoner, championSquares, bases]
  );

  if (!Array.isArray(enrichedChampions) || enrichedChampions.length === 0) {
    return (
      <section className="top-champions glass-panel">
        <div className="top-champions__empty">
          <p>No Champion Data</p>
          <span>Play more games to see your top champions.</span>
        </div>
      </section>
    );
  }

  return (
    <section className="top-champions glass-panel">
      <header className="top-champions__header">
        <span className="badge-soft">Mastery</span>
        <h3>Top Champions</h3>
        <p className="top-champions__subtitle">Most played in the last {range === 'all' ? 'recorded' : range} matches</p>
      </header>

      <div className="top-champions__grid">
        {enrichedChampions.map((champ) => (
          <article key={champ.name} className="champion-card">
            <div className="champion-card__visual">
              <div className="champion-card__image-wrapper">
                <img src={champ.imageUrl} alt={champ.name} loading="lazy" />
                <div className="champion-card__glow" />
              </div>
              <div className="champion-card__rank-badge">{champ.playCount}</div>
            </div>

            <div className="champion-card__content">
              <div className="champion-card__head">
                <h4>{champ.name}</h4>
                <span className={`winrate-badge ${champ.winrate >= 50 ? 'positive' : 'negative'}`}>
                  {champ.winrate}% WR
                </span>
              </div>

              <div className="champion-card__stats">
                <div className="stat-row">
                  <span className="label">KDA</span>
                  <span className="value">{champ.avgKda}</span>
                </div>
                <div className="stat-row">
                  <span className="label">Record</span>
                  <span className="value">{champ.wins}W - {champ.losses}L</span>
                </div>
              </div>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

TopChampions.propTypes = {
  championPlayCounts: PropTypes.array,
  matches: PropTypes.array,
  summoner: PropTypes.object,
  championSquares: PropTypes.object,
  bases: PropTypes.object,
  range: PropTypes.oneOfType([PropTypes.string, PropTypes.number])
};
